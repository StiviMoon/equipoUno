#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.platypus import (SimpleDocTemplate, Paragraph, Spacer, Table,
                                 TableStyle, HRFlowable, Preformatted, PageBreak)
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY

PDF_PATH = "informe_defensa_final_PB.pdf"

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
RED     = colors.HexColor("#C62828")

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
qa_style     = s("QA", fontSize=9,  textColor=RED,    spaceAfter=3, leading=14, fontName="Helvetica-Bold")

def HR(): return HRFlowable(width="100%", thickness=1.5, color=ORANGE, spaceAfter=8, spaceBefore=4)
def HR2(): return HRFlowable(width="100%", thickness=0.5, color=GRAY, spaceAfter=6, spaceBefore=4)
def H1(t): return Paragraph(t, h1_style)
def H2(t): return Paragraph(t, h2_style)
def H3(t): return Paragraph(t, h3_style)
def P(t):  return Paragraph(t, body_style)
def B(t):  return Paragraph(f"• {t}", bullet_style)
def Code(t): return Preformatted(t, code_style)
def Crit(n, t): return Paragraph(f"▶ Criterio {n}: {t}", crit_style)
def QA(q): return Paragraph(f"Pregunta del Profesor: {q}", qa_style)

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
story.append(Spacer(1, 1*cm))
story.append(Paragraph("INFORME DE DEFENSA FINAL", title_style))
story.append(Paragraph("Proyecto: Pico Botella (PB) — Android / Kotlin", title_style))
story.append(Spacer(1, 0.4*cm))
story.append(Paragraph("Preparación para la Evaluación del Miniproyecto 1", sub_style))
story.append(Paragraph("Repositorio: https://github.com/StiviMoon/PB", sub_style))
story.append(HR())
story.append(P("Este documento consolida el resumen técnico general de la aplicación, el detalle exhaustivo "
               "de las Historias de Usuario asignadas, la evidencia de participación en GitHub y una "
               "guía de posibles preguntas y respuestas para la defensa ante el profesor."))

# ══════════════════════════════════════════════════════════
# 1. RESUMEN TÉCNICO GENERAL
# ══════════════════════════════════════════════════════════
story.append(H1("1. Resumen Técnico General de la App"))
story.append(P("La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel), utilizando SQLite "
               "mediante Room y consumiendo una API externa de Pokémon."))
story.append(tabla(
    [
        ["Tecnología", "Propósito en el Proyecto"],
        ["Kotlin", "Lenguaje de programación nativo"],
        ["MVVM", "Patrón de arquitectura para separar la lógica de la vista"],
        ["Room (SQLite)", "Base de datos local para guardar y eliminar retos"],
        ["Kotlin Coroutines", "Ejecutar operaciones pesadas sin congelar la UI"],
        ["StateFlow", "Manejo de estado reactivo (la UI se actualiza sola al borrar)"],
        ["Navigation", "Navegación entre Splash, Home, Retos e Instrucciones"],
    ],
    col_widths=[4.5*cm, 12*cm]
))

# ══════════════════════════════════════════════════════════
# 2. EVIDENCIA DE TRABAJO (GITHUB & JIRA)
# ══════════════════════════════════════════════════════════
story.append(H1("2. Evidencia de Participación Individual (RA-3)"))
story.append(P("A continuación se detallan los commits principales que demuestran la contribución "
               "directa al repositorio (Usuarios en Git: Kevinseya / kevinseya17):"))

story.append(tabla(
    [
        ["Hash", "Mensaje del Commit", "Qué decirle al Profesor"],
        ["eb4049b", "Merge pull request #7 from kevinseya17/feature/eliminar-mostrar-reto", "Profesor, aquí integré mi rama de trabajo al proyecto principal mediante un Pull Request."],
        ["8a77e9a", "Resolve merge conflicts and integrate upstream changes", "Este commit fue importante porque resolví los conflictos al unir mi código con el de mis compañeros."],
        ["db72d80", "Merge HU-6, HU-7 con HU-9 adaptado a nueva BD", "Aquí adapté mi código de Eliminar Reto para que funcionara con la tabla Room final del equipo."],
        ["d143261", "fix(home): Mostrar texto del reto al azar", "Aquí implementé la lógica para mostrar el texto aleatorio de la HU 12 (Mostrar Reto)."],
        ["952f25b", "feat(retos): Implementar funcion de eliminar reto", "Este fue mi primer gran commit donde desarrollé el cuadro de diálogo y lógica de la HU 9.0."],
    ],
    col_widths=[2*cm, 6.5*cm, 8*cm]
))
story.append(Spacer(1, 0.2*cm))
story.append(P("<b>Gestión en Jira:</b> Las historias asignadas a mi nombre fueron la <b>HU 9.0</b> (Eliminar reto) "
               "y la <b>HU 12</b> (Mostrar reto aleatorio), las cuales fueron movidas a la columna 'Done / Hecho' durante el sprint."))

story.append(PageBreak())

# ══════════════════════════════════════════════════════════
# 3. EXPLICACIÓN DETALLADA DE LAS HUs ASIGNADAS
# ══════════════════════════════════════════════════════════
story.append(H1("3. Detalle de Contribución: HU 9.0 y HU 12"))

# HU 9.0
story.append(H2("HU 9.0 — Eliminar Reto (Cuadro de confirmación)"))
story.append(P("Muestra un diálogo de confirmación cuando el usuario quiere borrar un reto de la lista."))
story.append(B("<b>Diseño (RA-2):</b> Se creó `dialog_delete_reto.xml` con fondo blanco (usando `bg_dialog_white_rounded.xml`), "
               "título negro en negrita, la descripción dinámica de la BD, y dos botones de texto 'NO' y 'SI' en color naranja (#FF6F00)."))
story.append(B("<b>Bloqueo del Diálogo:</b> Se utilizó `dialog.setCancelable(false)` en Kotlin para que el diálogo NO "
               "se cierre si el usuario toca por fuera o presiona el botón de atrás, cumpliendo el criterio estricto."))
story.append(B("<b>Lógica de Eliminación (RA-1):</b> Al presionar 'SI', se llama a `viewModel.eliminarReto(reto)` que ejecuta "
               "una Corrutina (`viewModelScope.launch`) llamando al `delete` del DAO de Room. Gracias al `Flow`, la lista se actualiza sola."))

# HU 12
story.append(H2("HU 12 — Mostrar Reto Aleatorio"))
story.append(P("Tras girar la botella, muestra un diálogo con un reto aleatorio de la base de datos y un Pokémon al azar."))
story.append(B("<b>Diseño Avanzado (RA-2):</b> Se usó `ConstraintLayout` para lograr que el círculo del Pokémon y el botón naranja 'Cerrar' "
               "sobresalgan del fondo negro semitransparente. Esto se logró anclando el constraint top y bottom de la vista al mismo borde."))
story.append(B("<b>Consulta Aleatoria (RA-1):</b> Se agregó al DAO la consulta SQL: `SELECT * FROM retos ORDER BY RANDOM() LIMIT 1`."))
story.append(B("<b>Consumo de API (RA-1):</b> Se creó `PokemonRepository.kt` usando `HttpURLConnection` para descargar el JSON de la "
               "Pokédex desde GitHub. La petición se ejecuta en `Dispatchers.IO` para no bloquear el hilo principal."))

story.append(HR())

# ══════════════════════════════════════════════════════════
# 4. PREGUNTAS DEL PROFESOR Y CÓMO RESPONDER
# ══════════════════════════════════════════════════════════
story.append(H1("4. Q&A: Posibles Preguntas del Profesor (Simulación de Defensa)"))

story.append(QA("¿Por qué usaron MVVM y no MVC en este proyecto de Android?"))
story.append(P("<b>Respuesta:</b> Usamos MVVM para separar la lógica de negocio de la interfaz visual. Al usar `ViewModel`, los datos "
               "sobreviven a los cambios de configuración (como cuando se gira el teléfono). En MVC, el Activity/Fragment se sobrecarga "
               "de código y los datos se pierden al rotar la pantalla."))

story.append(HR2())
story.append(QA("Muéstrame cómo hicieron para que la app no se congele al hacer consultas a la Base de Datos o a la API de Pokémon."))
story.append(P("<b>Respuesta:</b> Utilizamos Kotlin Coroutines. Para las llamadas a Room usamos `viewModelScope.launch`, y para la "
               "API de Pokémon usamos explícitamente `withContext(Dispatchers.IO)`. Esto asegura que las operaciones pesadas de lectura, "
               "escritura y red ocurran en hilos secundarios, dejando el Main Thread libre para que la UI sea fluida."))

story.append(HR2())
story.append(QA("¿Por qué eligieron usar Room en lugar de SQLite OpenHelper estándar?"))
story.append(P("<b>Respuesta:</b> Room es la recomendación oficial de Google (Jetpack). Nos proporciona verificación de consultas SQL "
               "en tiempo de compilación (evita errores en ejecución). Además, Room se integra perfectamente con `Flow` y Corrutinas, "
               "lo que nos permite tener una base de datos reactiva que avisa a la UI cuando hay cambios (como en la HU 9 al eliminar)."))

story.append(HR2())
story.append(QA("En la HU 12, el botón de Cerrar y el ícono del Pokémon se ven por fuera de la caja negra. ¿Cómo programaste eso en la interfaz?"))
story.append(P("<b>Respuesta:</b> Utilicé las restricciones de `ConstraintLayout`. Para que el botón sobresalga por abajo, vinculé "
               "su `layout_constraintTop_toBottomOf` y su `layout_constraintBottom_toBottomOf` ambos al borde inferior del contenedor oscuro. "
               "Eso hace que el centro del botón se alinee exactamente con el borde del diálogo."))

story.append(HR2())
story.append(QA("Veo que usaron `StateFlow` en el ViewModel de retos. ¿Qué ventaja tiene sobre LiveData?"))
story.append(P("<b>Respuesta:</b> `StateFlow` está construido sobre Corrutinas nativas de Kotlin, lo que lo hace más moderno y no "
               "depende de la plataforma Android. Siempre tiene un estado inicial, lo cual es ideal para manejar estados como Loading, Empty y Success en la lista de retos."))

story.append(HR2())
story.append(QA("¿Cómo lograste en la HU 9.0 que el diálogo no se cerrara si el usuario tocaba la pantalla por fuera?"))
story.append(P("<b>Respuesta:</b> Instancié un objeto `android.app.Dialog` y llamé a la propiedad `dialog.setCancelable(false)`. "
               "Esto bloquea los toques exteriores y también el uso del botón físico/gesto de 'atrás' del celular, obligando "
               "al usuario a elegir entre los botones 'SI' o 'NO' tal como pedía el requerimiento de la Historia de Usuario."))

story.append(HR2())
story.append(QA("¿Cómo descargaste la imagen del Pokémon de internet? ¿Usaste alguna librería?"))
story.append(P("<b>Respuesta:</b> No utilizamos librerías como Retrofit o Glide para mantener el proyecto con las librerías base permitidas. "
               "Utilicé `HttpURLConnection` nativo de Java/Kotlin. Hago un GET a la URL del JSON, extraigo un Pokémon al azar usando `JSONObject`, "
               "y luego abro otra conexión para descargar la imagen directamente a un `Bitmap` que finalmente coloco en el `ImageView`."))

doc.build(story)
print(f"PDF generado: {PDF_PATH}")
