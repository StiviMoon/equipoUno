#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.platypus import (SimpleDocTemplate, Paragraph, Spacer, Table,
                                 TableStyle, HRFlowable, Preformatted, PageBreak)
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY

PDF_PATH = "informe_mi_contribucion_PB.pdf"

doc = SimpleDocTemplate(
    PDF_PATH, pagesize=A4,
    rightMargin=2*cm, leftMargin=2*cm,
    topMargin=2*cm, bottomMargin=2*cm
)

ORANGE  = colors.HexColor("#FF6600")
DARK    = colors.HexColor("#1A1A1A")
GRAY    = colors.HexColor("#444444")
LIGHT   = colors.HexColor("#F5F5F5")
GREEN   = colors.HexColor("#2E7D32")
BLUE    = colors.HexColor("#1565C0")

styles = getSampleStyleSheet()
def s(name, **kw):
    return ParagraphStyle(name, parent=styles["Normal"], **kw)

title_style  = s("T",  fontSize=22, textColor=ORANGE, alignment=TA_CENTER, spaceAfter=4,  fontName="Helvetica-Bold")
sub_style    = s("S",  fontSize=10, textColor=GRAY,   alignment=TA_CENTER, spaceAfter=14)
h1_style     = s("H1", fontSize=15, textColor=ORANGE, spaceAfter=6, spaceBefore=18, fontName="Helvetica-Bold")
h2_style     = s("H2", fontSize=12, textColor=BLUE,   spaceAfter=4, spaceBefore=12, fontName="Helvetica-Bold")
h3_style     = s("H3", fontSize=10, textColor=DARK,   spaceAfter=3, spaceBefore=8,  fontName="Helvetica-Bold")
body_style   = s("B",  fontSize=9,  textColor=DARK,   spaceAfter=4, leading=14, alignment=TA_JUSTIFY)
code_style   = s("C",  fontSize=8,  fontName="Courier", backColor=LIGHT, spaceAfter=6,
                 spaceBefore=4, leftIndent=10, rightIndent=10, leading=13)
bullet_style = s("BU", fontSize=9,  textColor=DARK,   spaceAfter=3, leading=14, leftIndent=18, bulletIndent=8)
crit_style   = s("CR", fontSize=9,  textColor=GREEN,  spaceAfter=3, leading=14, fontName="Helvetica-Bold")
badge_style  = s("BA", fontSize=9,  textColor=colors.white, alignment=TA_CENTER, fontName="Helvetica-Bold")

def HR(): return HRFlowable(width="100%", thickness=1.5, color=ORANGE, spaceAfter=8, spaceBefore=4)
def HR2(): return HRFlowable(width="100%", thickness=0.5, color=GRAY, spaceAfter=6, spaceBefore=4)
def H1(t): return Paragraph(t, h1_style)
def H2(t): return Paragraph(t, h2_style)
def H3(t): return Paragraph(t, h3_style)
def P(t):  return Paragraph(t, body_style)
def B(t):  return Paragraph(f"✔ {t}", bullet_style)
def Code(t): return Preformatted(t, code_style)
def Crit(n, t): return Paragraph(f"▶ Criterio {n}: {t}", crit_style)

def tabla(data, col_widths=None):
    t = Table(data, colWidths=col_widths, repeatRows=1)
    t.setStyle(TableStyle([
        ("BACKGROUND",    (0,0), (-1,0), ORANGE),
        ("TEXTCOLOR",     (0,0), (-1,0), colors.white),
        ("FONTNAME",      (0,0), (-1,0), "Helvetica-Bold"),
        ("FONTSIZE",      (0,0), (-1,-1), 8),
        ("ALIGN",         (0,0), (-1,-1), "LEFT"),
        ("VALIGN",        (0,0), (-1,-1), "MIDDLE"),
        ("ROWBACKGROUNDS",(0,1), (-1,-1), [colors.white, LIGHT]),
        ("GRID",          (0,0), (-1,-1), 0.5, GRAY),
        ("LEFTPADDING",   (0,0), (-1,-1), 6),
        ("RIGHTPADDING",  (0,0), (-1,-1), 6),
        ("TOPPADDING",    (0,0), (-1,-1), 4),
        ("BOTTOMPADDING", (0,0), (-1,-1), 4),
    ]))
    return t

story = []

# ══════════════════════════════════════════════════════════
# PORTADA
# ══════════════════════════════════════════════════════════
story.append(Spacer(1, 1.5*cm))
story.append(Paragraph("INFORME DE CONTRIBUCIÓN PERSONAL", title_style))
story.append(Paragraph("Proyecto: Pico Botella (PB) — Android / Kotlin", title_style))
story.append(Spacer(1, 0.4*cm))
story.append(Paragraph("Repositorio: https://github.com/StiviMoon/PB  |  Versión: 1.1.0", sub_style))
story.append(Paragraph("Historias de Usuario asignadas: HU 9.0 (Eliminar Reto) y HU 12 (Mostrar Reto Aleatorio)", sub_style))
story.append(HR())

story.append(P(
    "Este documento detalla de forma exhaustiva el trabajo que realicé en el proyecto grupal "
    "<b>Pico Botella</b>. Se explica criterio por criterio qué se hizo, qué herramientas y "
    "componentes de Android se utilizaron, y cómo se escribió el código para cumplir con "
    "cada requerimiento de las dos historias de usuario que me fueron asignadas."
))
story.append(Spacer(1, 0.3*cm))

# ══════════════════════════════════════════════════════════
# CONTEXTO TÉCNICO GENERAL
# ══════════════════════════════════════════════════════════
story.append(H1("1. Contexto Técnico del Proyecto"))
story.append(P(
    "La aplicación sigue el patrón de arquitectura <b>MVVM (Model-View-ViewModel)</b>, "
    "el estándar de Android moderno. Esto significa que el código está separado en tres capas:"
))
story.append(B("<b>Vista (Fragment/Dialog):</b> Solo muestra datos y captura las acciones del usuario."))
story.append(B("<b>ViewModel:</b> Contiene la lógica de negocio (insertar, editar, eliminar retos). No depende de la UI."))
story.append(B("<b>Repository + Room:</b> Gestiona la base de datos SQLite local. Cuando los datos cambian, notifica automáticamente a la Vista."))
story.append(Spacer(1, 0.3*cm))

story.append(H3("Herramientas y librerías que afectan mi trabajo:"))
story.append(tabla(
    [
        ["Herramienta / Librería", "Para qué la usé en mis HUs"],
        ["Room (SQLite)", "Para leer y eliminar retos de la base de datos local"],
        ["Kotlin Coroutines", "Para hacer operaciones de BD en segundo plano sin bloquear la pantalla"],
        ["StateFlow / Flow", "Para que la lista de retos se actualice automáticamente al eliminar uno"],
        ["android.app.Dialog", "Para construir los cuadros de diálogo personalizados de HU 9.0 y HU 12"],
        ["HttpURLConnection", "Para descargar el JSON de la API de Pokémon (HU 12)"],
        ["Dispatchers.IO", "Para ejecutar la descarga de la imagen del Pokémon en hilo secundario"],
        ["ViewGroup.LayoutParams", "Para controlar el tamaño del diálogo a pantalla completa (HU 12)"],
        ["ConstraintLayout", "Usado en dialog_random_reto.xml para posicionar el botón sobresaliente"],
        ["Shape Drawables (XML)", "Para crear el fondo oscuro redondeado y el círculo del Pokémon"],
    ],
    col_widths=[5.5*cm, 11*cm]
))
story.append(HR())

# ══════════════════════════════════════════════════════════
# HU 9.0
# ══════════════════════════════════════════════════════════
story.append(H1("2. HU 9.0 — Cuadro de Diálogo: Eliminar Reto"))
story.append(P(
    "<b>Historia de Usuario:</b> Como App, quiero poder mostrarle al jugador un cuadro de diálogo "
    "para que el jugador elimine un determinado reto."
))
story.append(Spacer(1, 0.2*cm))

story.append(tabla(
    [
        ["Campo", "Valor"],
        ["ID", "HU 9.0"],
        ["Actor", "App"],
        ["Acción", "Poder mostrarle al jugador un cuadro de diálogo"],
        ["Consecuencia", "Que el jugador elimine un determinado reto"],
        ["Archivos creados/modificados", "RetosFragment.kt  |  dialog_delete_reto.xml  |  RetosViewModel.kt"],
    ],
    col_widths=[4*cm, 12.5*cm]
))
story.append(Spacer(1, 0.4*cm))

# --- criterios HU 9 ---
story.append(H2("2.1. Criterios de Aceptación — Implementación Detallada"))
story.append(HR2())

story.append(Crit(1, "El cuadro de diálogo debe tener un fondo de color blanco."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Creé el archivo de diseño <b>dialog_delete_reto.xml</b>. El componente raíz es un "
    "<b>LinearLayout</b> al que le asigné el atributo <b>android:background</b> apuntando "
    "al recurso drawable <b>bg_dialog_white_rounded.xml</b>, que define un rectángulo "
    "blanco con esquinas redondeadas. En el código Kotlin, al crear el Dialog, configuré "
    "la ventana para que su fondo sea transparente, de modo que solo se vea el fondo blanco "
    "del layout y no el fondo gris predeterminado de Android:"
))
story.append(Code(
"""// RetosFragment.kt — función confirmarEliminar()
val dialog = android.app.Dialog(requireContext())
dialog.setContentView(R.layout.dialog_delete_reto)
dialog.window?.setBackgroundDrawable(
    android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
)"""
))

story.append(HR2())
story.append(Crit(2, 'Título en la parte superior con el texto "¿Desea eliminar el siguiente reto?:", centrado y en negro (bold).'))
story.append(H3("¿Qué hice?"))
story.append(P(
    "En el <b>dialog_delete_reto.xml</b> coloqué un <b>TextView</b> como primer hijo del LinearLayout con los siguientes atributos:"
))
story.append(Code(
"""<TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="¿Desea eliminar el siguiente reto?:"
    android:textColor="#000000"          <!-- negro puro -->
    android:textStyle="bold"             <!-- negrita -->
    android:textSize="18sp"
    android:textAlignment="center"       <!-- centrado -->
    android:layout_marginBottom="16dp"/>"""
))

story.append(HR2())
story.append(Crit(3, "Mostrar la descripción del reto que se quiere eliminar (información viene de la base de datos)."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "En el XML coloqué un segundo <b>TextView</b> con id <b>tvRetoDescription</b>. "
    "En el código Kotlin, dentro de la función <b>confirmarEliminar(reto: Reto)</b> "
    "del <b>RetosFragment</b>, accedí a ese TextView y le asigné el texto del objeto "
    "Reto que ya viene cargado de la base de datos:"
))
story.append(Code(
"""// El objeto 'reto' ya viene de la BD cuando el usuario pulsa eliminar
val tvRetoDescription = dialog.findViewById<TextView>(R.id.tvRetoDescription)
tvRetoDescription.text = reto.descripcion   // ← descripción real de la BD"""))

story.append(HR2())
story.append(Crit(4, 'Texto "NO" de color naranja. Al hacer clic cierra el diálogo y vuelve a la lista de retos.'))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Usé un <b>TextView</b> (no un Button) con id <b>btnNo</b> y le asigné el color naranja "
    "<b>#FF6F00</b>. El componente es clickeable y tiene efecto visual de ripple. "
    "En Kotlin le programé el listener para simplemente cerrar el diálogo:"
))
story.append(Code(
"""<!-- dialog_delete_reto.xml -->
<TextView
    android:id="@+id/btnNo"
    android:text="NO"
    android:textColor="#FF6F00"
    android:textStyle="bold"
    android:clickable="true"
    android:focusable="true"
    android:background="?attr/selectableItemBackgroundBorderless" />

// RetosFragment.kt
val btnNo = dialog.findViewById<TextView>(R.id.btnNo)
btnNo.setOnClickListener {
    dialog.dismiss()   // solo cierra el diálogo, no borra nada
}"""))

story.append(HR2())
story.append(Crit(5, 'Texto "SI" de color naranja. Al hacer clic, elimina el reto de SQLite e inmediatamente desaparece de la lista.'))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Al igual que el NO, usé un <b>TextView</b> con id <b>btnSi</b> y color naranja. "
    "En su listener llamé al <b>ViewModel</b> para que ejecute la eliminación. "
    "Gracias al patrón MVVM y al <b>Flow</b> reactivo de Room, la lista se actualiza "
    "sola sin necesidad de recargar la pantalla:"
))
story.append(Code(
"""// RetosFragment.kt
val btnSi = dialog.findViewById<TextView>(R.id.btnSi)
btnSi.setOnClickListener {
    viewModel.eliminarReto(reto)   // ← llama al ViewModel
    dialog.dismiss()
}

// RetosViewModel.kt
fun eliminarReto(reto: Reto) {
    viewModelScope.launch {          // Coroutine — hilo secundario
        repository.deleteReto(reto)  // ejecuta DELETE en SQLite
    }
}

// RetoRepository.kt
suspend fun deleteReto(reto: Reto) = dao.delete(reto)

// RetoDao.kt
@Delete
suspend fun delete(reto: Reto)   // Room genera el SQL automáticamente"""))
story.append(P(
    "Flujo completo al presionar SI: <b>Vista → ViewModel → Repository → DAO → SQLite</b>. "
    "Room detecta el cambio, el Flow emite la nueva lista sin ese reto, "
    "el ViewModel actualiza el StateFlow, y el Fragment refresca el RecyclerView. "
    "Todo esto ocurre en menos de un segundo."
))

story.append(HR2())
story.append(Crit(6, "El diálogo solo desaparece al dar clic en NO o SI. Al tocar fuera del diálogo NO debe cerrarse."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Esta es una propiedad específica de <b>android.app.Dialog</b>. Llamé al método "
    "<b>setCancelable(false)</b> justo después de crear el diálogo. "
    "Esto instruye al sistema operativo Android para que ignore todos los toques "
    "fuera del área del diálogo y también que no se cierre con el botón de atrás del dispositivo:"
))
story.append(Code(
"""val dialog = android.app.Dialog(requireContext())
dialog.setContentView(R.layout.dialog_delete_reto)
dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
dialog.setCancelable(false)   // ← Criterio 6: fuerza al usuario a elegir SI o NO
// ... configuración de botones ...
dialog.show()"""))

story.append(Spacer(1, 0.3*cm))
story.append(H2("2.2. Resumen de Archivos que Creé/Modifiqué para HU 9.0"))
story.append(tabla(
    [
        ["Archivo", "Tipo", "Qué hice"],
        ["dialog_delete_reto.xml", "Layout XML", "Creé el diseño del diálogo: fondo blanco, título, descripción dinámica, botones NO y SI en naranja"],
        ["bg_dialog_white_rounded.xml", "Drawable XML", "Definí el shape blanco con esquinas redondeadas para el fondo del diálogo"],
        ["RetosFragment.kt", "Kotlin", "Añadí la función confirmarEliminar() que infla el diálogo, conecta la descripción del reto y gestiona los eventos de los botones"],
        ["RetosViewModel.kt", "Kotlin", "Ya existía; usé la función eliminarReto(reto) que ejecuta el delete en Room mediante Coroutine"],
    ],
    col_widths=[4.5*cm, 2.5*cm, 9.5*cm]
))
story.append(HR())
story.append(PageBreak())

# ══════════════════════════════════════════════════════════
# HU 12
# ══════════════════════════════════════════════════════════
story.append(H1("3. HU 12 — Mostrar Reto Aleatorio"))
story.append(P(
    "<b>Historia de Usuario:</b> Como App, quiero mostrar un cuadro de diálogo al jugador "
    "para que pueda conocer el reto aleatorio que debe realizar un determinado jugador."
))
story.append(Spacer(1, 0.2*cm))

story.append(tabla(
    [
        ["Campo", "Valor"],
        ["ID", "HU 12"],
        ["Actor", "App"],
        ["Acción", "Mostrar un cuadro de diálogo al jugador"],
        ["Consecuencia", "Conocer el reto aleatorio que debe realizar un determinado jugador"],
        ["Archivos creados/modificados", "HomeFragment.kt  |  dialog_random_reto.xml  |  PokemonRepository.kt  |  bg_dialog_rounded.xml  |  bg_circle_pokemon.xml  |  btn_orange_rounded.xml"],
    ],
    col_widths=[4*cm, 12.5*cm]
))
story.append(Spacer(1, 0.4*cm))

story.append(H2("3.1. Criterios de Aceptación — Implementación Detallada"))
story.append(HR2())

story.append(Crit(1, "Cuadro de diálogo personalizado con fondo negro degradado, transparencia sutil y bordes redondeados de color blanco."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Creé el archivo <b>dialog_random_reto.xml</b> usando un <b>ConstraintLayout</b> externo "
    "con fondo transparente, y un ConstraintLayout interno como contenedor del diálogo "
    "con el atributo <b>android:background=\"@drawable/bg_dialog_rounded\"</b>. "
    "Este drawable es un shape XML que define el fondo oscuro con transparencia y el borde blanco:"
))
story.append(Code(
"""<!-- bg_dialog_rounded.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#CC000000"/>        <!-- negro con ~80% opacidad -->
    <stroke android:width="2dp" android:color="#FFFFFF"/>   <!-- borde blanco -->
    <corners android:radius="16dp"/>          <!-- esquinas redondeadas -->
</shape>"""))
story.append(P(
    "En Kotlin, al crear el Dialog configuré la ventana para que ocupe el ancho completo "
    "de la pantalla, para que el diálogo se vea centrado y grande:"
))
story.append(Code(
"""val dialog = Dialog(requireContext())
dialog.setContentView(R.layout.dialog_random_reto)
dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
dialog.window?.setLayout(
    ViewGroup.LayoutParams.MATCH_PARENT,    // ancho: toda la pantalla
    ViewGroup.LayoutParams.WRAP_CONTENT     // alto: lo que necesite el contenido
)"""))

story.append(HR2())
story.append(Crit(2, "Círculo con borde blanco y fondo negro en la parte superior. Mostrar imagen aleatoria de un Pokémon consumiendo la API."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "En el <b>dialog_random_reto.xml</b> posicioné un <b>ImageView</b> con id <b>ivPokemon</b> "
    "usando constraints del ConstraintLayout para que esté anclado entre el borde superior "
    "del diálogo y el borde inferior del mismo, logrando el efecto de que 'sobresale' en la parte superior. "
    "Le asigné el background <b>bg_circle_pokemon.xml</b>:"
))
story.append(Code(
"""<!-- bg_circle_pokemon.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">           <!-- forma circular perfecta -->
    <solid android:color="#000000"/>
    <stroke android:width="3dp" android:color="#FFFFFF"/>
</shape>

<!-- dialog_random_reto.xml -->
<ImageView
    android:id="@+id/ivPokemon"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:background="@drawable/bg_circle_pokemon"
    android:padding="12dp"
    android:scaleType="fitCenter"
    app:layout_constraintTop_toTopOf="@id/dialogContainer"
    app:layout_constraintBottom_toTopOf="@id/dialogContainer"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"/>"""))

story.append(P(
    "Para obtener la imagen aleatoria del Pokémon, creé la clase <b>PokemonRepository.kt</b> "
    "que realiza la petición HTTP a la API especificada. "
    "La petición corre en <b>Dispatchers.IO</b> (hilo de red) para no bloquear la interfaz:"
))
story.append(Code(
"""// PokemonRepository.kt
class PokemonRepository {
    suspend fun getRandomPokemonImageUrl(): String? = withContext(Dispatchers.IO) {
        val url = URL("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val json = connection.inputStream.bufferedReader().use { it.readText() }
            val pokemonArray = JSONObject(json).getJSONArray("pokemon")
            val randomPokemon = pokemonArray.getJSONObject(Random.nextInt(pokemonArray.length()))
            var imageUrl = randomPokemon.getString("img")
            // Android no permite http:// en versiones modernas, se reemplaza por https://
            if (imageUrl.startsWith("http://"))
                imageUrl = imageUrl.replace("http://", "https://")
            return@withContext imageUrl
        }
        return@withContext null
    }
}"""))

story.append(P(
    "La imagen se descarga DESPUÉS de mostrar el diálogo (en una Coroutine separada) "
    "para que el jugador vea el reto inmediatamente y la imagen aparezca en cuanto cargue:"
))
story.append(Code(
"""// HomeFragment.kt — dentro de mostrarRetoAleatorio()
dialog.show()   // ← primero se muestra el diálogo con el texto del reto

launch {        // ← luego, en paralelo, se descarga la imagen
    val imageUrl = PokemonRepository().getRandomPokemonImageUrl()
    imageUrl?.let {
        val bitmap = withContext(Dispatchers.IO) {
            val conn = URL(it).openConnection() as HttpURLConnection
            conn.connectTimeout = 5000; conn.readTimeout = 5000; conn.connect()
            BitmapFactory.decodeStream(conn.inputStream)
        }
        if (dialog.isShowing) {   // solo si el diálogo sigue abierto
            dialog.findViewById<ImageView>(R.id.ivPokemon).setImageBitmap(bitmap)
        }
    }
}"""))

story.append(HR2())
story.append(Crit(3, "Texto de color blanco (bold) que muestra el reto a realizar. El reto debe venir de la base de datos local y debe ser aleatorio."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "En el XML coloqué un <b>TextView</b> con id <b>tvReto</b>, color blanco y estilo bold. "
    "En el código, consulté la base de datos local usando el método <b>getRandomReto()</b> "
    "del DAO, que ejecuta la consulta <b>SELECT * FROM retos ORDER BY RANDOM() LIMIT 1</b>:"
))
story.append(Code(
"""<!-- dialog_random_reto.xml -->
<TextView
    android:id="@+id/tvReto"
    android:textColor="#FFFFFF"
    android:textSize="18sp"
    android:textStyle="bold"
    android:textAlignment="center"/>

// HomeFragment.kt
val dao = AppDatabase.getInstance(requireContext()).retoDao()
val reto = dao.getRandomReto()                    // ← consulta aleatoria a la BD
val retoTexto = reto?.descripcion ?: "No hay retos disponibles."
dialog.findViewById<TextView>(R.id.tvReto).text = retoTexto

// RetoDao.kt — la consulta SQL que usé
@Query("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1")
suspend fun getRandomReto(): Reto?"""))

story.append(HR2())
story.append(Crit(4, 'Botón naranja "Cerrar" en la parte inferior-central. La parte superior dentro del diálogo, la inferior por fuera.'))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Este fue el reto visual más interesante. Usé un <b>AppCompatButton</b> con el background "
    "<b>btn_orange_rounded.xml</b> (shape con relleno naranja y esquinas redondeadas). "
    "Para lograr que la parte superior del botón quede DENTRO del diálogo y la inferior FUERA, "
    "usé las restricciones de ConstraintLayout anclando simultáneamente el borde superior "
    "e inferior del botón al borde inferior del contenedor del diálogo:"
))
story.append(Code(
"""<!-- dialog_random_reto.xml — posicionamiento del botón Cerrar -->
<androidx.appcompat.widget.AppCompatButton
    android:id="@+id/btnCerrar"
    android:background="@drawable/btn_orange_rounded"
    android:text="Cerrar"
    android:textColor="#FFFFFF"
    android:paddingHorizontal="32dp"
    app:layout_constraintTop_toBottomOf="@id/dialogContainer"   <!-- top = borde inferior del diálogo -->
    app:layout_constraintBottom_toBottomOf="@id/dialogContainer"<!-- bottom = mismo punto -->
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"/>
<!-- Al coincidir Top y Bottom en el mismo borde, el botón queda centrado en ese punto,
     con la mitad superior dentro del diálogo y la mitad inferior fuera -->"""))

story.append(HR2())
story.append(Crit(5, 'Al hacer clic en "Cerrar", el diálogo desaparece y el juego vuelve a la pantalla principal listo para nueva partida.'))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Al botón de cerrar le asigné un listener que: "
    "(1) oculta el diálogo con <b>dialog.dismiss()</b>, "
    "(2) hace visible de nuevo el botón de girar la botella, "
    "(3) reanuda la música de fondo si el audio estaba activado:"
))
story.append(Code(
"""// HomeFragment.kt
dialog.findViewById<Button>(R.id.btnCerrar).setOnClickListener {
    dialog.dismiss()          // cierra el diálogo
    resetButton()             // muestra de nuevo el botón "Presióname" con animación
    audioViewModel.resumeIfEnabled()  // reanuda la música si estaba activa
}

private fun resetButton() {
    binding.btnPress.visibility = View.VISIBLE
    binding.btnPress.startAnim(R.anim.btn_blink)  // vuelve la animación parpadeante
}"""))

story.append(HR2())
story.append(Crit(6, "El diálogo solo desaparece al hacer clic en el botón 'Cerrar'. Al tocar por fuera NO debe cerrarse."))
story.append(H3("¿Qué hice?"))
story.append(P(
    "Exactamente igual que en la HU 9.0, llamé al método <b>setCancelable(false)</b> "
    "del Dialog. Esto garantiza que: (a) los toques fuera del diálogo son ignorados, "
    "y (b) el botón de atrás del teléfono tampoco lo cierra. "
    "La ÚNICA forma de cerrar el diálogo es presionar el botón naranja 'Cerrar':"
))
story.append(Code(
"""val dialog = Dialog(requireContext())
dialog.setContentView(R.layout.dialog_random_reto)
dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
dialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
dialog.setCancelable(false)   // ← Criterio 6
// ... configuración ...
dialog.show()"""))

story.append(Spacer(1, 0.3*cm))
story.append(H2("3.2. Resumen de Archivos que Creé/Modifiqué para HU 12"))
story.append(tabla(
    [
        ["Archivo", "Tipo", "Qué hice"],
        ["dialog_random_reto.xml", "Layout XML", "Diseñé el diálogo completo: contenedor oscuro, ImageView circular, texto del reto en blanco, botón naranja sobresaliente"],
        ["bg_dialog_rounded.xml", "Drawable XML", "Shape negro semitransparente con borde blanco y esquinas redondeadas (fondo del diálogo)"],
        ["bg_circle_pokemon.xml", "Drawable XML", "Shape oval negro con borde blanco (fondo circular del ImageView del Pokémon)"],
        ["btn_orange_rounded.xml", "Drawable XML", "Shape naranja con esquinas redondeadas para el botón Cerrar"],
        ["PokemonRepository.kt", "Kotlin", "Creé esta clase. Hace la petición HTTP a la API de Pokémon, parsea el JSON y retorna una URL de imagen aleatoria"],
        ["HomeFragment.kt", "Kotlin", "Añadí la función mostrarRetoAleatorio(): consulta la BD, crea el diálogo, carga la imagen en paralelo y gestiona el botón Cerrar"],
        ["RetoDao.kt", "Kotlin", "Añadí la función getRandomReto() con la consulta SQL ORDER BY RANDOM() LIMIT 1"],
    ],
    col_widths=[4.5*cm, 2.5*cm, 9.5*cm]
))
story.append(HR())

# ══════════════════════════════════════════════════════════
# CONCLUSIÓN
# ══════════════════════════════════════════════════════════
story.append(H1("4. Conclusión"))
story.append(P(
    "Las dos Historias de Usuario que me fueron asignadas (<b>HU 9.0</b> y <b>HU 12</b>) "
    "fueron implementadas cumpliendo al 100% con todos y cada uno de sus Criterios de Aceptación. "
    "A continuación, el resumen de tecnologías y decisiones de diseño que tomé:"
))
story.append(Spacer(1, 0.2*cm))
story.append(tabla(
    [
        ["Decisión Técnica", "Justificación"],
        ["Usar android.app.Dialog en lugar de DialogFragment para HU 9.0", "Permite control total sobre el layout y el comportamiento del cuadro de forma directa desde el Fragment que ya tiene el contexto del reto seleccionado."],
        ["setCancelable(false) en ambos diálogos", "Requerimiento explícito del Criterio 6 de ambas HUs. Obliga al jugador a tomar una decisión consciente."],
        ["Coroutine separada para descargar la imagen (HU 12)", "El diálogo se muestra inmediatamente con el reto. La imagen del Pokémon aparece en cuanto carga, sin bloquear la experiencia del usuario."],
        ["ConstraintLayout con constraints duales en el botón Cerrar", "Técnica de posicionamiento que logra el efecto visual de que el botón 'sobresale' del borde inferior del diálogo, exactamente como muestra el diseño de la HU 12."],
        ["HttpURLConnection nativo (sin librerías externas)", "El proyecto no incluye Retrofit ni Glide, así que usé la API de red nativa de Java/Kotlin para no agregar dependencias no autorizadas."],
        ["Sustitución http:// → https:// en URLs de la API", "Android bloquea tráfico HTTP en claro por defecto (cleartext restriction). Esta corrección permite que las imágenes del Pokémon carguen correctamente."],
    ],
    col_widths=[5.5*cm, 11*cm]
))

doc.build(story)
print(f"PDF generado: {PDF_PATH}")
