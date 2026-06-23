#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable, Preformatted
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY

PDF_PATH = "informe_tecnico_PB.pdf"

doc = SimpleDocTemplate(
    PDF_PATH,
    pagesize=A4,
    rightMargin=2*cm, leftMargin=2*cm,
    topMargin=2*cm, bottomMargin=2*cm
)

ORANGE = colors.HexColor("#FF6600")
DARK   = colors.HexColor("#1A1A1A")
GRAY   = colors.HexColor("#444444")
LIGHT  = colors.HexColor("#F5F5F5")

styles = getSampleStyleSheet()

def s(name, **kw):
    return ParagraphStyle(name, parent=styles["Normal"], **kw)

title_style   = s("MyTitle",  fontSize=22, textColor=ORANGE, alignment=TA_CENTER, spaceAfter=4, fontName="Helvetica-Bold")
sub_style     = s("MySub",    fontSize=10, textColor=GRAY,   alignment=TA_CENTER, spaceAfter=14)
h1_style      = s("MyH1",    fontSize=14, textColor=ORANGE, spaceAfter=6, spaceBefore=16, fontName="Helvetica-Bold")
h2_style      = s("MyH2",    fontSize=11, textColor=DARK,   spaceAfter=4, spaceBefore=10, fontName="Helvetica-Bold")
body_style    = s("MyBody",   fontSize=9,  textColor=DARK,   spaceAfter=4, leading=14, alignment=TA_JUSTIFY)
code_style    = s("MyCode",   fontSize=8,  textColor=colors.HexColor("#2D2D2D"),
                  fontName="Courier", backColor=LIGHT, spaceAfter=6, spaceBefore=4,
                  leftIndent=10, rightIndent=10, leading=13)
bullet_style  = s("MyBullet", fontSize=9, textColor=DARK, spaceAfter=3, leading=14, leftIndent=16, bulletIndent=6)

def HR():
    return HRFlowable(width="100%", thickness=1, color=ORANGE, spaceAfter=8, spaceBefore=4)

def H1(text): return Paragraph(text, h1_style)
def H2(text): return Paragraph(text, h2_style)
def P(text):  return Paragraph(text, body_style)
def B(text):  return Paragraph(f"• {text}", bullet_style)
def Code(text): return Preformatted(text, code_style)

def tabla(data, col_widths=None, header=True):
    t = Table(data, colWidths=col_widths, repeatRows=1 if header else 0)
    style = [
        ("BACKGROUND", (0,0), (-1,0), ORANGE),
        ("TEXTCOLOR",  (0,0), (-1,0), colors.white),
        ("FONTNAME",   (0,0), (-1,0), "Helvetica-Bold"),
        ("FONTSIZE",   (0,0), (-1,-1), 8),
        ("ALIGN",      (0,0), (-1,-1), "LEFT"),
        ("VALIGN",     (0,0), (-1,-1), "MIDDLE"),
        ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, LIGHT]),
        ("GRID",       (0,0), (-1,-1), 0.5, GRAY),
        ("LEFTPADDING",(0,0), (-1,-1), 6),
        ("RIGHTPADDING",(0,0),(-1,-1), 6),
        ("TOPPADDING", (0,0), (-1,-1), 4),
        ("BOTTOMPADDING",(0,0),(-1,-1),4),
    ]
    t.setStyle(TableStyle(style))
    return t

story = []

# ─── PORTADA ──────────────────────────────────────────────────
story.append(Spacer(1, 2*cm))
story.append(Paragraph("INFORME TÉCNICO", title_style))
story.append(Paragraph("Aplicación Android — Pico Botella (PB)", title_style))
story.append(Spacer(1, 0.4*cm))
story.append(Paragraph("Repositorio: https://github.com/StiviMoon/PB", sub_style))
story.append(Paragraph("Plataforma: Android (Kotlin) | Arquitectura: MVVM | Versión: 1.1.0", sub_style))
story.append(HR())

# ─── 1. DESCRIPCIÓN GENERAL ───────────────────────────────────
story.append(H1("1. Descripción General del Proyecto"))
story.append(P(
    '"Pico Botella" es una aplicación de entretenimiento para Android que simula el clásico juego de la botella. '
    'Los jugadores se ubican alrededor del dispositivo, presionan el botón parpadeante para girar la botella virtual '
    'y el jugador señalado debe cumplir el reto que la aplicación muestre de forma aleatoria. '
    'La app permite gestionar una lista personalizada de retos guardados localmente en SQLite mediante Room, '
    'y consume una API externa de Pokémon para hacer la experiencia visual más entretenida.'
))
story.append(HR())

# ─── 2. TECNOLOGÍAS ───────────────────────────────────────────
story.append(H1("2. Tecnologías y Dependencias Utilizadas"))
story.append(tabla(
    [
        ["Tecnología", "Propósito"],
        ["Kotlin", "Lenguaje principal de desarrollo"],
        ["Android SDK 36 (mín. 24)", "SDK objetivo — compatible con Android 7.0+"],
        ["MVVM + StateFlow", "Patrón de arquitectura: separa lógica de UI"],
        ["Room (SQLite)", "Base de datos local para persistir los retos"],
        ["Kotlin Coroutines", "Operaciones asíncronas sin bloquear la interfaz"],
        ["Navigation Component", "Navegación entre Fragments"],
        ["ViewBinding", "Acceso seguro a vistas XML desde Kotlin"],
        ["ViewModel + LiveData", "Estado persistente ante rotaciones de pantalla"],
        ["MediaPlayer", "Reproducción de música de fondo y sonido de giro"],
        ["HttpURLConnection", "Petición HTTP a la API de Pokémon"],
        ["Material3 (Material Design)", "Componentes visuales modernos de Google"],
    ],
    col_widths=[6*cm, 10.5*cm]
))
story.append(HR())

# ─── 3. ESTRUCTURA ────────────────────────────────────────────
story.append(H1("3. Estructura del Proyecto"))
story.append(Code(
"""PB-Github/
└── app/src/main/
    ├── java/com/example/pb/
    │   ├── MainActivity.kt              ← Actividad única (Single Activity)
    │   ├── data/
    │   │   ├── AppDatabase.kt           ← Configuración de Room (Singleton)
    │   │   └── RetoDao.kt               ← Consultas SQL para la tabla retos
    │   ├── model/
    │   │   └── Reto.kt                  ← Entidad de base de datos
    │   ├── repository/
    │   │   ├── RetoRepository.kt        ← Intermediario DAO ↔ ViewModel
    │   │   └── PokemonRepository.kt     ← Consumo de API externa de Pokémon
    │   ├── ui/
    │   │   ├── splash/SplashFragment.kt
    │   │   ├── home/HomeFragment.kt
    │   │   ├── instrucciones/InstruccionesFragment.kt
    │   │   ├── retos/
    │   │   │   ├── RetosFragment.kt
    │   │   │   ├── RetosAdapter.kt
    │   │   │   ├── AgregarRetoDialog.kt
    │   │   │   └── EditarRetoDialog.kt
    │   │   └── toolbar/CustomToolbarView.kt
    │   ├── utils/AnimationUtils.kt
    │   └── viewmodel/
    │       ├── RetosViewModel.kt
    │       └── AudioViewModel.kt
    └── res/
        ├── layout/      ← Diseños de pantallas y diálogos
        ├── drawable/    ← Íconos y fondos personalizados (shapes)
        ├── raw/         ← Archivos de audio (.ogg)
        └── values/      ← Colores, strings, temas"""
))
story.append(HR())

# ─── 4. ARQUITECTURA MVVM ─────────────────────────────────────
story.append(H1("4. Arquitectura MVVM"))
story.append(P(
    'La aplicación implementa el patrón <b>MVVM (Model-View-ViewModel)</b>, el estándar recomendado por Google para apps Android modernas. '
    'El flujo de datos es el siguiente:'
))
story.append(Code("  [Fragment / Vista]  ←observa StateFlow─  [ViewModel]  ←llama─  [Repository]  ←consulta─  [Room / API]"))
story.append(B("<b>Vista (Fragment):</b> Solo muestra datos y notifica acciones del usuario. No contiene lógica de negocio."))
story.append(B("<b>ViewModel:</b> Contiene la lógica, expone el estado con StateFlow. Sobrevive a rotaciones de pantalla."))
story.append(B("<b>Repository:</b> Fuente única de verdad. Abstrae si los datos vienen de Room o de una API externa."))
story.append(B("<b>Room/DAO:</b> Convierte consultas Kotlin en SQL real y retorna Flow reactivos que actualizan la UI automáticamente."))
story.append(HR())

# ─── 5. MODELO DE DATOS ───────────────────────────────────────
story.append(H1("5. Modelo de Datos — Entidad Reto"))
story.append(Code(
"""@Entity(tableName = "retos")
data class Reto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val timestamp: Long = System.currentTimeMillis()
)"""
))
story.append(P('La tabla <b>retos</b> en SQLite tiene 3 columnas: <b>id</b> (clave primaria autoincremental), '
               '<b>descripcion</b> (texto del reto escrito por el usuario), y <b>timestamp</b> (fecha de creación en milisegundos, guardada automáticamente). '
               'La base de datos está en la versión 2 y usa <b>fallbackToDestructiveMigration</b> para recrarse sin errores si el esquema cambia.'))
story.append(HR())

# ─── 6. RETOdao ───────────────────────────────────────────────
story.append(H1("6. Base de Datos — RetoDao (Consultas SQL)"))
story.append(tabla(
    [
        ["Función", "Consulta SQL", "Descripción"],
        ["getAll()", "SELECT * FROM retos ORDER BY id DESC", "Lista todos los retos, los más nuevos primero. Retorna un Flow reactivo."],
        ["getRandomReto()", "SELECT * FROM retos ORDER BY RANDOM() LIMIT 1", "Retorna UN reto aleatorio. Usado para la HU 12."],
        ["insert(reto)", "INSERT", "Guarda un nuevo reto en la tabla."],
        ["update(reto)", "UPDATE", "Modifica la descripción de un reto existente."],
        ["delete(reto)", "DELETE", "Elimina un reto específico de la tabla."],
    ],
    col_widths=[3.5*cm, 6*cm, 7*cm]
))
story.append(HR())

# ─── 7. FUNCIONALIDADES / HUs ─────────────────────────────────
story.append(H1("7. Funcionalidades Implementadas (Historias de Usuario)"))

# HU 6
story.append(H2("HU 6.0 — Agregar y Listar Retos"))
story.append(P('<b>Archivos:</b> RetosFragment.kt, AgregarRetoDialog.kt, RetosAdapter.kt, RetosViewModel.kt'))
story.append(P('Muestra todos los retos en un RecyclerView ordenados del más nuevo al más antiguo. Un FAB naranja abre el diálogo para agregar un reto. '
               'El botón "Guardar" solo se activa cuando el campo de texto no está vacío. '
               'Al guardar, la lista se actualiza inmediatamente gracias al Flow reactivo de Room.'))

story.append(Spacer(1, 0.3*cm))

# HU 8
story.append(H2("HU 8.0 — Editar Reto"))
story.append(P('<b>Archivos:</b> EditarRetoDialog.kt, dialog_editar_reto.xml, RetosViewModel.kt'))
story.append(P('Cada item tiene un ícono de lápiz. Al pulsarlo, se abre un diálogo con la descripción actual del reto en un campo editable. '
               '"Cancelar" cierra sin cambios; "Guardar" actualiza el reto en SQLite. El diálogo no se cierra si se toca fuera (setCanceledOnTouchOutside(false)).'))

story.append(Spacer(1, 0.3*cm))

# HU 9
story.append(H2("HU 9.0 — Eliminar Reto (cuadro de diálogo de confirmación)"))
story.append(P('<b>Archivos:</b> RetosFragment.kt, dialog_delete_reto.xml, RetosViewModel.kt'))
story.append(P('Cada item tiene un ícono de tacho. Al pulsarlo, aparece un cuadro de diálogo con fondo blanco, '
               'el título "¿Desea eliminar el siguiente reto?:" en negro y negrita, y la descripción exacta del reto. '
               'Los botones "NO" y "SI" son textos en color naranja (#FF6F00). '
               '"NO" cierra el diálogo; "SI" llama a viewModel.eliminarReto(reto) que ejecuta DELETE en SQLite '
               'y la lista se actualiza al instante. El diálogo no se cierra al tocar fuera (setCancelable(false)).'))

story.append(Spacer(1, 0.3*cm))

# HU 10
story.append(H2("HU 10.0 — Compartir la Aplicación"))
story.append(P('<b>Archivos:</b> HomeFragment.kt, CustomToolbarView.kt'))
story.append(P('El toolbar tiene un ícono de compartir. Al pulsarlo, se lanza un Intent ACTION_SEND de tipo "text/plain" '
               'que abre el selector nativo de Android para compartir el link de la app con un mensaje predefinido.'))

story.append(Spacer(1, 0.3*cm))

# HU 11
story.append(H2("HU 11.0 — Sonido de Botella Girando"))
story.append(P('<b>Archivos:</b> AudioViewModel.kt, HomeFragment.kt, res/raw/spin_sound.ogg'))
story.append(P('Al girar la botella se reproduce un efecto de sonido (spin_sound.ogg). El AudioViewModel gestiona dos '
               'instancias de MediaPlayer separadas: una para la música de fondo (bgMediaPlayer) y otra para el sonido de giro (spinMediaPlayer), '
               'lo que permite que funcionen de forma independiente.'))

story.append(Spacer(1, 0.3*cm))

# HU 12
story.append(H2("HU 12.0 — Mostrar Reto Aleatorio"))
story.append(P('<b>Archivos:</b> HomeFragment.kt, dialog_random_reto.xml, PokemonRepository.kt'))
story.append(P('Tras la animación de giro aparece un diálogo con fondo oscuro, bordes blancos redondeados y transparencia. '
               'En la parte superior sobresale un círculo (borde blanco, fondo negro) con la imagen de un Pokémon aleatorio. '
               'En el centro, el texto del reto aleatorio en blanco y negrita (traído con dao.getRandomReto()). '
               'Un botón naranja "Cerrar" en la parte inferior permite volver al juego. El diálogo NO se cierra al tocar fuera (setCancelable(false)).'))
story.append(P('<b>Imagen Pokémon:</b> PokemonRepository realiza una petición HTTP GET a la API '
               'https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json, '
               'parsea el JSON, selecciona un Pokémon al azar y retorna la URL de imagen. '
               'La descarga corre en Dispatchers.IO (hilo secundario) para no bloquear la UI.'))
story.append(HR())

# ─── 8. NAVEGACIÓN ────────────────────────────────────────────
story.append(H1("8. Navegación Entre Pantallas"))
story.append(P('La app es <b>Single Activity</b>: una sola MainActivity con un NavHostFragment. '
               'La navegación se gestiona con el <b>Navigation Component</b> de Android Jetpack.'))
story.append(Spacer(1, 0.2*cm))
story.append(tabla(
    [
        ["Origen", "Destino", "Disparador"],
        ["Splash", "Home", "Automático — delay de 5 segundos con animación de botella"],
        ["Home", "Retos", "Botón de retos en el toolbar personalizado"],
        ["Home", "Instrucciones", "Botón de instrucciones en el toolbar"],
        ["Retos", "Home", "Flecha de volver en la toolbar de Retos"],
    ],
    col_widths=[3.5*cm, 3.5*cm, 9.5*cm]
))
story.append(HR())

# ─── 9. AUDIO ─────────────────────────────────────────────────
story.append(H1("9. Sistema de Audio — AudioViewModel"))
story.append(tabla(
    [
        ["Función", "Descripción"],
        ["startBgMusic()", "Inicia la música de fondo en bucle (si el audio está activado)"],
        ["pauseBgMusic()", "Pausa la música al salir de la pantalla (onPause)"],
        ["resumeIfEnabled()", "Reanuda la música si el usuario no la silenció"],
        ["toggleAudio()", "Activa/Desactiva el audio. El ícono del toolbar se actualiza automáticamente."],
        ["playSpinSound()", "Reproduce el sonido de giro de la botella (HU 11.0)"],
        ["stopSpinSound()", "Detiene y libera el MediaPlayer del sonido de giro"],
        ["onCleared()", "Libera ambos MediaPlayers cuando el ViewModel se destruye"],
    ],
    col_widths=[4.5*cm, 12*cm]
))
story.append(HR())

# ─── 10. TOOLBAR ──────────────────────────────────────────────
story.append(H1("10. Toolbar Personalizada — CustomToolbarView"))
story.append(P('Es un componente de vista personalizado que extiende <b>LinearLayout</b>. '
               'Encapsula los 5 botones del toolbar y expone listeners mediante lambdas. '
               'Todos los botones tienen una micro-animación de escala (touch_scale.xml) al ser presionados.'))
story.append(Spacer(1, 0.2*cm))
story.append(tabla(
    [
        ["Botón", "Acción"],
        ["Calificar (estrella)", "Abre la app en Play Store mediante un Intent ACTION_VIEW"],
        ["Audio (parlante)", "Llama a audioViewModel.toggleAudio() y actualiza el ícono"],
        ["Instrucciones (libro)", "Navega a InstruccionesFragment"],
        ["Retos (trofeo)", "Navega a RetosFragment"],
        ["Compartir", "Lanza el Intent de compartir (HU 10.0)"],
    ],
    col_widths=[4.5*cm, 12*cm]
))
story.append(HR())

# ─── 11. COLORES ──────────────────────────────────────────────
story.append(H1("11. Paleta de Colores y Tema Visual"))
story.append(P('El tema base es <b>Theme.Material3.DayNight.NoActionBar</b>, que desactiva la barra de acción nativa '
               'y le da control total al diseño personalizado.'))
story.append(Spacer(1, 0.2*cm))
story.append(tabla(
    [
        ["Nombre", "HEX", "Uso en la app"],
        ["brand_primary / orange", "#FF6600", "Botones principales, acentos, íconos"],
        ["brand_primary_dark / orange_dark", "#CC5200", "Estado presionado del naranja"],
        ["bg_home", "#3D1F0A", "Fondo de la pantalla de juego (marrón/madera)"],
        ["bg_screen", "#111111", "Fondo de pantallas secundarias (gris oscuro)"],
        ["bg_toolbar / black", "#000000", "Fondo del toolbar y Splash"],
        ["text_primary / white", "#FFFFFF", "Texto principal"],
        ["text_secondary", "#B0B0B0", "Texto secundario / mensajes vacíos"],
    ],
    col_widths=[5*cm, 2.5*cm, 9*cm]
))
story.append(HR())

# ─── 12. RESUMEN DE ARCHIVOS ─────────────────────────────────
story.append(H1("12. Resumen de Archivos Clave"))
story.append(tabla(
    [
        ["Archivo .kt", "Responsabilidad"],
        ["MainActivity.kt", "Punto de entrada; solo infla el layout con NavHostFragment"],
        ["AppDatabase.kt", "Singleton de Room; configura la BD 'pb_database' versión 2"],
        ["RetoDao.kt", "CRUD completo + consulta aleatoria sobre la tabla retos"],
        ["Reto.kt", "Modelo de datos: id, descripcion, timestamp"],
        ["RetoRepository.kt", "Intermediario DAO ↔ ViewModel para los retos"],
        ["PokemonRepository.kt", "Consume la API de Pokémon y retorna URL de imagen aleatoria"],
        ["RetosViewModel.kt", "Lógica: insertar, editar, eliminar retos; estado como StateFlow"],
        ["AudioViewModel.kt", "Gestión completa de audio: fondo y sonido de giro"],
        ["HomeFragment.kt", "Pantalla principal; animación de botella; dispara el reto aleatorio"],
        ["RetosFragment.kt", "Lista retos; maneja el diálogo de eliminación (HU 9.0)"],
        ["AgregarRetoDialog.kt", "Diálogo para crear un nuevo reto (HU 6.0)"],
        ["EditarRetoDialog.kt", "Diálogo para editar un reto existente (HU 8.0)"],
        ["RetosAdapter.kt", "Adaptador del RecyclerView; gestiona clics Editar y Eliminar"],
        ["CustomToolbarView.kt", "Vista personalizada del toolbar con 5 acciones"],
        ["SplashFragment.kt", "Pantalla de carga de 5 segundos con animación de botella"],
    ],
    col_widths=[5.5*cm, 11*cm]
))

doc.build(story)
print(f"PDF generado exitosamente: {PDF_PATH}")
