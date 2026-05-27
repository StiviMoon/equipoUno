package historia.numeroOcho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import historia.numeroOcho.ui.theme.My// MainActivity.kt (Actividad de prueba independiente)
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import historia.numeroOcho.data.database.RetoRepository
import historia.numeroOcho.data.model.Reto
import historia.numeroOcho.ui.dialog.EditarRetoDialog
import historia.numeroOcho.ui.dialog.EditarRetoDialogListener

class MainActivity : AppCompatActivity(), EditarRetoDialogListener {

    private lateinit var retoRepository: RetoRepository
    private lateinit var containerRetos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retoRepository = RetoRepository(this)

        // Layout de prueba
        val scrollView = ScrollView(this)
        containerRetos = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }
        scrollView.addView(containerRetos)
        setContentView(scrollView)

        cargarListaRetos()
    }

    private fun cargarListaRetos() {
        containerRetos.removeAllViews()
        val retos = retoRepository.obtenerTodosLosRetos()

        if (retos.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "No hay retos disponibles"
                textSize = 16f
            }
            containerRetos.addView(tvEmpty)
        } else {
            retos.forEach { reto ->
                agregarItemReto(reto)
            }
        }
    }

    private fun agregarItemReto(reto: Reto) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 16)
        }

        val tvDescripcion = TextView(this).apply {
            text = reto.descripcion
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val btnEditar = Button(this).apply {
            text = "Editar"
            setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            setBackgroundColor(resources.getColor(android.R.color.transparent, null))
            setOnClickListener {
                mostrarDialogoEdicion(reto.id)
            }
        }

        itemLayout.addView(tvDescripcion)
        itemLayout.addView(btnEditar)
        containerRetos.addView(itemLayout)

        // Línea divisoria
        val divider = android.widget.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            )
            setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
        }
        containerRetos.addView(divider)
    }

    private fun mostrarDialogoEdicion(retoId: Long) {
        val dialog = EditarRetoDialog.newInstance(retoId)
        dialog.show(supportFragmentManager, "EditarRetoDialog")
    }

    override fun onRetoActualizado(retoId: Long, nuevaDescripcion: String) {
        Toast.makeText(this, "Reto actualizado: $nuevaDescripcion", Toast.LENGTH_SHORT).show()
        cargarListaRetos() // Refrescar lista
    }

    override fun onEdicionCancelada() {
        Toast.makeText(this, "Edición cancelada", Toast.LENGTH_SHORT).show()
    }
}