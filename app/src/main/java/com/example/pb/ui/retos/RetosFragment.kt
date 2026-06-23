package com.example.pb.ui.retos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.data.AppDatabase
import com.example.pb.databinding.FragmentRetosBinding
import com.example.pb.model.Reto
import com.example.pb.repository.RetoRepository
import com.example.pb.viewmodel.AudioViewModel
import com.example.pb.viewmodel.RetosUiState
import com.example.pb.viewmodel.RetosViewModel
import com.example.pb.viewmodel.RetosViewModelFactory
import kotlinx.coroutines.launch

/**
 * RetosFragment — Pantalla de gestión de retos (HU 6.0, HU 8.0, HU 9.0).
 *
 * Muestra la lista de retos almacenados en la base de datos local (Room/SQLite)
 * usando un RecyclerView. Permite al usuario:
 * - Ver todos los retos (HU 6.0)
 * - Editar un reto existente abriendo [EditarRetoDialog] (HU 8.0)
 * - Eliminar un reto con confirmación mediante un cuadro de diálogo (HU 9.0)
 * - Agregar nuevos retos mediante [AgregarRetoDialog] (HU 6.0)
 *
 * Sigue el patrón MVVM: esta clase solo gestiona la UI y delega
 * toda la lógica de datos a [RetosViewModel].
 */
class RetosFragment : Fragment() {

    // _binding es anulable para liberarla en onDestroyView y evitar fugas de memoria.
    // 'binding' (sin underscore) es la referencia segura que se usa en el resto del código.
    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    // activityViewModels comparte el mismo AudioViewModel con todas las pantallas
    private val audioViewModel: AudioViewModel by activityViewModels()

    // viewModels crea el ViewModel con una fábrica personalizada (RetosViewModelFactory)
    // porque el ViewModel necesita recibir el RetoRepository como parámetro
    private val viewModel: RetosViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).retoDao()
        RetosViewModelFactory(RetoRepository(dao))
    }

    // Adaptador del RecyclerView que dibuja cada reto en la lista
    private lateinit var adapter: RetosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout XML y crea el binding para acceder a las vistas de forma segura
        _binding = FragmentRetosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pausa la música de fondo al entrar a la pantalla de retos
        audioViewModel.pauseBgMusic()

        // Al tocar la flecha de atrás en el toolbar, reanuda la música y regresa a Home
        binding.toolbarRetos.setNavigationOnClickListener {
            audioViewModel.resumeIfEnabled()
            findNavController().navigateUp()
        }

        setupRecyclerView()
        setupFab()
        observeRetos()
    }

    /**
     * Configura el RecyclerView y su adaptador.
     *
     * El adaptador recibe dos lambdas (funciones) como parámetros:
     * - onEdit: abre el diálogo de edición cuando el usuario toca el lápiz (HU 8.0)
     * - onDelete: llama a confirmarEliminar cuando el usuario toca el tacho (HU 9.0)
     */
    private fun setupRecyclerView() {
        adapter = RetosAdapter(
            onEdit   = { reto -> EditarRetoDialog.newInstance(reto).show(childFragmentManager, EditarRetoDialog.TAG) },
            onDelete = { reto -> confirmarEliminar(reto) } // HU 9.0: muestra el diálogo de confirmación
        )
        binding.rvRetos.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvRetos.adapter = adapter
    }

    /**
     * Configura el botón flotante naranja (FAB) para agregar nuevos retos.
     * Al tocarlo abre el diálogo AgregarRetoDialog (HU 6.0).
     */
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            AgregarRetoDialog().show(childFragmentManager, AgregarRetoDialog.TAG)
        }
    }

    /**
     * Observa el estado de la UI (StateFlow) emitido por el ViewModel.
     *
     * El StateFlow tiene 3 posibles estados:
     * - [RetosUiState.Loading]: cargando los datos iniciales (oculta todo).
     * - [RetosUiState.Success]: hay retos en la BD → muestra la lista.
     * - [RetosUiState.Empty]: no hay retos → muestra el texto de "lista vacía".
     *
     * No es necesario recargar manualmente: Room emite automáticamente
     * una nueva lista cada vez que hay un cambio (inserción, edición o borrado).
     */
    private fun observeRetos() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle garantiza que solo se recolectan datos
            // cuando el Fragment está visible (STARTED), evitando fugas de memoria.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RetosUiState.Success -> {
                            // Hay retos: actualiza el RecyclerView con la nueva lista
                            adapter.submitList(state.retos)
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvRetos.visibility = View.VISIBLE
                        }
                        is RetosUiState.Empty -> {
                            // No hay retos: oculta la lista y muestra el mensaje vacío
                            adapter.submitList(emptyList())
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvRetos.visibility = View.GONE
                        }
                        is RetosUiState.Loading -> {
                            // Estado inicial mientras Room carga los datos
                            binding.tvEmpty.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    /**
     * HU 9.0 — Cuadro de diálogo para confirmar la eliminación de un reto.
     *
     * Criterios implementados:
     * - Criterio 1: fondo blanco redondeado (bg_dialog_white_rounded.xml).
     * - Criterio 2: título "¿Desea eliminar el siguiente reto?:" centrado en negro negrita.
     * - Criterio 3: muestra la descripción real del reto desde la base de datos.
     * - Criterio 4: botón "NO" en naranja → cierra el diálogo sin borrar nada.
     * - Criterio 5: botón "SI" en naranja → elimina el reto en Room y cierra el diálogo.
     * - Criterio 6: setCancelable(false) → el diálogo NO se cierra al tocar fuera.
     *
     * @param reto El objeto Reto que el usuario quiere eliminar (viene del Adapter).
     */
    private fun confirmarEliminar(reto: Reto) {
        // Crear el cuadro de diálogo nativo de Android con el layout personalizado
        val dialog = android.app.Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_reto)

        // Truco clave: Android pone un fondo gris rectanglar por defecto.
        // Al poner TRANSPARENT aquí, solo se ve el fondo blanco del XML (Criterio 1).
        dialog.window?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
        )

        // Criterio 6: bloquea el cierre por toque externo y por botón de atrás del celular
        dialog.setCancelable(false)

        // Busca las vistas dentro del layout del diálogo por su ID
        val tvRetoDescription = dialog.findViewById<android.widget.TextView>(R.id.tvRetoDescription)
        val btnNo = dialog.findViewById<android.widget.TextView>(R.id.btnNo)
        val btnSi = dialog.findViewById<android.widget.TextView>(R.id.btnSi)

        // Criterio 3: muestra la descripción real del reto en el cuadro de diálogo
        tvRetoDescription.text = reto.descripcion

        // Criterio 4: al presionar NO, solo cierra el diálogo sin tocar la BD
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Criterio 5: al presionar SI, elimina el reto en la BD y cierra el diálogo.
        // El StateFlow detectará el cambio y actualizará la lista automáticamente.
        btnSi.setOnClickListener {
            viewModel.eliminarReto(reto) // → ViewModel → Repository → DAO → Room (DELETE)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        audioViewModel.resumeIfEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Libera la referencia al binding para evitar fugas de memoria
        _binding = null
    }
}
