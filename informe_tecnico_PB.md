# Informe Técnico — Aplicación "Pico Botella" (PB)

**Proyecto:** Pico Botella  
**Repositorio:** https://github.com/StiviMoon/PB  
**Plataforma:** Android (Kotlin)  
**Versión:** 1.1.0  
**Lenguaje:** Kotlin  
**Arquitectura:** MVVM (Model-View-ViewModel)  

---

## 1. Descripción General del Proyecto

"Pico Botella" es una aplicación de entretenimiento para Android que simula el clásico juego de "la botella". Los jugadores se ubican alrededor del dispositivo, presionan el botón parpadeante para hacer girar una botella virtual y el jugador señalado debe cumplir el reto que la aplicación muestre de forma aleatoria.

La aplicación permite gestionar una lista personalizada de retos almacenados en la base de datos local del dispositivo (SQLite a través de Room), y también consume una API externa de Pokémon para hacer la experiencia visual más entretenida.

---

## 2. Tecnologías y Dependencias Utilizadas

| Tecnología | Propósito |
|---|---|
| **Kotlin** | Lenguaje principal de desarrollo |
| **Android SDK 36** | SDK objetivo (mínimo SDK 24 — Android 7.0) |
| **MVVM + StateFlow** | Patrón de arquitectura para separar lógica y vista |
| **Room (SQLite)** | Base de datos local para guardar retos |
| **Kotlin Coroutines** | Operaciones en segundo plano sin bloquear la UI |
| **Navigation Component** | Navegación entre pantallas (Fragments) |
| **ViewBinding** | Conexión segura entre XML y código Kotlin |
| **ViewModel + LiveData** | Estado de la UI persistente ante rotaciones |
| **MediaPlayer** | Reproducción de música de fondo y sonido de botella |
| **HttpURLConnection** | Conexión HTTP a la API externa de Pokémon |
| **Material3** | Componentes visuales modernos de Google |

---

## 3. Estructura del Proyecto

```
PB-Github/
└── app/src/main/
    ├── java/com/example/pb/
    │   ├── MainActivity.kt              ← Actividad única (Single Activity)
    │   ├── data/
    │   │   ├── AppDatabase.kt           ← Configuración de Room
    │   │   └── RetoDao.kt               ← Consultas SQL para Reto
    │   ├── model/
    │   │   └── Reto.kt                  ← Entidad de base de datos
    │   ├── repository/
    │   │   ├── RetoRepository.kt        ← Intermediario entre DAO y ViewModel
    │   │   └── PokemonRepository.kt     ← Consumo de API externa
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
        ├── layout/                      ← Diseños de pantallas y diálogos
        ├── drawable/                    ← Íconos y fondos personalizados
        ├── raw/                         ← Archivos de audio (.ogg)
        └── values/                      ← Colores, strings, temas
```

---

## 4. Arquitectura MVVM — Cómo se Conecta Todo

La aplicación sigue el patrón **MVVM** (Modelo - Vista - ViewModel):

```
[Vista / Fragment]  ←observa→  [ViewModel]  ←llama→  [Repository]  ←consulta→  [Room / API]
```

- La **Vista** (Fragment) solo muestra datos y reporta acciones del usuario.
- El **ViewModel** contiene la lógica de negocio y expone el estado mediante `StateFlow`.
- El **Repository** actúa como única fuente de datos, abstrayendo si los datos vienen de Room o de una API.
- **Room** persiste los datos en SQLite de forma reactiva usando `Flow`.

---

## 5. Modelo de Datos — Entidad Reto

```kotlin
@Entity(tableName = "retos")
data class Reto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val timestamp: Long = System.currentTimeMillis()
)
```

La tabla `retos` contiene tres columnas:
- **id**: clave primaria autoincremental.
- **descripcion**: el texto del reto escrito por el usuario.
- **timestamp**: fecha de creación en milisegundos (guardado automáticamente).

La base de datos está en la **versión 2** y usa `fallbackToDestructiveMigration` para que si se cambia el esquema, la base se recrea sin errores.

---

## 6. Base de Datos — RetoDao

El DAO (Data Access Object) define todas las operaciones permitidas sobre la tabla de retos:

| Función | Consulta SQL | Descripción |
|---|---|---|
| `getAll()` | `SELECT * FROM retos ORDER BY id DESC` | Retorna todos los retos, del más nuevo al más antiguo, como un `Flow` reactivo. |
| `getRandomReto()` | `SELECT * FROM retos ORDER BY RANDOM() LIMIT 1` | Retorna UN reto escogido al azar. Usado para la HU 12. |
| `insert(reto)` | INSERT | Inserta un nuevo reto en la tabla. |
| `update(reto)` | UPDATE | Actualiza la descripción de un reto existente. |
| `delete(reto)` | DELETE | Elimina un reto específico de la tabla. |

---

## 7. Funcionalidades Implementadas (Historias de Usuario)

### HU 6.0 — Agregar y Listar Retos
**Archivos involucrados:** `RetosFragment.kt`, `AgregarRetoDialog.kt`, `RetosAdapter.kt`, `RetosViewModel.kt`

**¿Qué hace?**
- Al entrar a la pantalla de Retos, se muestra una lista (`RecyclerView`) con todos los retos guardados en la base de datos, ordenados del más reciente al más antiguo.
- Si no hay retos, se muestra un mensaje de texto indicando que la lista está vacía.
- Un botón flotante (FAB naranja) en la esquina inferior derecha abre el diálogo `AgregarRetoDialog`.
- El diálogo tiene un campo de texto y dos botones: "Cancelar" (cierra sin guardar) y "Guardar" (que solo se activa cuando el campo no esté vacío). Al guardar, el reto aparece inmediatamente en la lista sin necesidad de recargar la pantalla.

**Detalles técnicos:**
- El `RetosViewModel` expone un `StateFlow<RetosUiState>` con tres posibles estados: `Loading`, `Empty`, y `Success(retos)`.
- La lista se actualiza automáticamente porque `RetoDao.getAll()` retorna un `Flow`, que emite automáticamente cada vez que cambia la tabla.

---

### HU 8.0 — Editar Reto
**Archivos involucrados:** `EditarRetoDialog.kt`, `dialog_editar_reto.xml`, `RetosViewModel.kt`

**¿Qué hace?**
- Cada item de la lista tiene un ícono de editar (lápiz naranja). Al pulsarlo, se abre el diálogo `EditarRetoDialog`.
- El diálogo muestra la descripción actual del reto en un campo editable (`EditText`).
- El usuario puede cambiar el texto y pulsar "Guardar" para actualizar el reto en la base de datos, o "Cancelar" para no hacer cambios.
- El diálogo no se cierra si el usuario toca fuera de él (`setCanceledOnTouchOutside(false)`).

**Detalles técnicos:**
- El ID y la descripción del reto se pasan al diálogo mediante `Bundle` (argumentos de Fragment).
- Al guardar, el ViewModel llama a `repository.updateReto(reto)` que ejecuta un `UPDATE` en SQLite.

---

### HU 9.0 — Eliminar Reto (cuadro de diálogo de confirmación)
**Archivos involucrados:** `RetosFragment.kt`, `dialog_delete_reto.xml`, `RetosViewModel.kt`

**¿Qué hace?**
- Cada item de la lista tiene un ícono de eliminar (tacho naranja). Al pulsarlo, aparece un cuadro de diálogo de confirmación.
- El diálogo tiene fondo blanco, título en negro y negrita "¿Desea eliminar el siguiente reto?:", y debajo muestra la descripción exacta del reto que se va a borrar (leída desde la base de datos).
- Dos opciones en color naranja: "NO" cierra el diálogo sin hacer cambios; "SI" elimina el reto de la base de datos y actualiza la lista al instante.
- El diálogo NO desaparece si el usuario toca fuera de él (`setCancelable(false)`).

**Detalles técnicos:**
- La lógica se encapsula en la función `confirmarEliminar(reto: Reto)` del `RetosFragment`.
- Al confirmar, se llama `viewModel.eliminarReto(reto)` que ejecuta `repository.deleteReto(reto)` en una Coroutine, borrando el registro del SQLite.
- Como `getAll()` es un `Flow`, la lista del RecyclerView se actualiza sola.

---

### HU 10.0 — Compartir la Aplicación
**Archivos involucrados:** `HomeFragment.kt`, `CustomToolbarView.kt`

**¿Qué hace?**
- La barra de herramientas superior (toolbar personalizada) tiene un ícono de compartir.
- Al pulsarlo, se abre el selector de apps nativo de Android con un mensaje predefinido (texto + link a Play Store).

**Detalles técnicos:**
- Se usa un `Intent` de tipo `ACTION_SEND` con `type = "text/plain"` para disparar el sistema de compartir nativo.

---

### HU 11.0 — Sonido de Botella Girando
**Archivos involucrados:** `AudioViewModel.kt`, `HomeFragment.kt`, `res/raw/spin_sound.ogg`

**¿Qué hace?**
- Cuando el usuario presiona el botón para girar la botella, se reproduce un efecto de sonido de giro (`spin_sound.ogg`).
- Al finalizar la animación de giro, el sonido se detiene automáticamente.
- Esto se maneja independientemente de la música de fondo.

**Detalles técnicos:**
- El `AudioViewModel` tiene dos instancias separadas de `MediaPlayer`: una para la música de fondo (`bgMediaPlayer`) y otra para el sonido de la botella (`spinMediaPlayer`).
- Los métodos `playSpinSound()` y `stopSpinSound()` controlan el ciclo de vida del sonido de giro.

---

### HU 12.0 — Mostrar Reto Aleatorio
**Archivos involucrados:** `HomeFragment.kt`, `dialog_random_reto.xml`, `PokemonRepository.kt`, `bg_dialog_rounded.xml`, `bg_circle_pokemon.xml`

**¿Qué hace?**
- Tras la animación de giro de la botella, aparece un cuadro de diálogo en el centro de la pantalla.
- El cuadro tiene un diseño oscuro con bordes redondeados blancos y transparencia.
- En la parte superior sobresale un círculo con borde blanco que contiene la imagen de un Pokémon aleatorio obtenido de internet.
- En el centro aparece en blanco y negrita el texto del reto seleccionado al azar desde la base de datos local.
- En la parte inferior hay un botón naranja "Cerrar". Al pulsarlo, el diálogo desaparece y el juego vuelve a estado inicial.
- El diálogo NO se cierra si el usuario toca fuera de él (`setCancelable(false)`).

**Detalles técnicos:**
- **Reto aleatorio:** Se obtiene con `dao.getRandomReto()` que ejecuta `SELECT * FROM retos ORDER BY RANDOM() LIMIT 1`.
- **Imagen Pokémon:** El `PokemonRepository` hace una petición HTTP GET a `https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json`, parsea el JSON, selecciona un Pokémon al azar, y retorna la URL de su imagen. La imagen se descarga en segundo plano con `Dispatchers.IO` y se muestra en el `ImageView` circular sin bloquear el diálogo.
- La aplicación reemplaza `http://` por `https://` en las URLs de imágenes para cumplir con las restricciones de seguridad de Android.

---

## 8. Navegación Entre Pantallas

La aplicación sigue el patrón de **Single Activity**: solo existe una `MainActivity` que contiene un `NavHostFragment`. La navegación se gestiona con el **Navigation Component** de Android Jetpack.

| Origen | Destino | Acción |
|---|---|---|
| Splash | Home | Automático (delay de 5 segundos) |
| Home | Retos | Botón de retos en toolbar |
| Home | Instrucciones | Botón de instrucciones en toolbar |
| Retos | Home | Flecha de "volver" en la toolbar de Retos |

---

## 9. Sistema de Audio

El `AudioViewModel` extiende `AndroidViewModel` para acceder al `Context` de la aplicación y gestiona dos reproductores de audio:

| Función | Descripción |
|---|---|
| `startBgMusic()` | Inicia la música de fondo en bucle si el audio está activado |
| `pauseBgMusic()` | Pausa la música (al salir de la pantalla con `onPause`) |
| `resumeIfEnabled()` | Reanuda la música si el usuario no la silenció |
| `toggleAudio()` | Alterna entre música activada/silenciada. El ícono del botón en el toolbar se actualiza automáticamente |
| `playSpinSound()` | Reproduce el sonido del giro de la botella |
| `stopSpinSound()` | Detiene el sonido del giro |

---

## 10. Toolbar Personalizada

La `CustomToolbarView` es un componente de vista personalizado (`LinearLayout`) que encapsula toda la lógica de la barra de herramientas superior. Los Fragments se suscriben a sus acciones mediante lambdas:

- **Calificar** → Abre Play Store
- **Audio** → Activa/Desactiva la música
- **Instrucciones** → Navega a la pantalla de instrucciones
- **Retos** → Navega a la pantalla de gestión de retos
- **Compartir** → Abre el diálogo nativo de compartir

Todos los botones tienen una animación de escala (`touch_scale.xml`) al ser presionados para dar retroalimentación visual.

---

## 11. Paleta de Colores y Tema Visual

| Nombre | Valor HEX | Uso |
|---|---|---|
| `brand_primary` / `orange` | `#FF6600` | Botones, acentos, íconos principales |
| `brand_primary_dark` / `orange_dark` | `#CC5200` | Estado presionado del naranja |
| `bg_home` | `#3D1F0A` | Fondo de la pantalla de juego (marrón/madera) |
| `bg_screen` | `#111111` | Fondo de pantallas secundarias (gris oscuro) |
| `bg_toolbar` / `black` | `#000000` | Fondo de la toolbar y Splash |
| `text_primary` / `white` | `#FFFFFF` | Texto principal |
| `text_secondary` | `#B0B0B0` | Texto secundario |

El tema base es `Theme.Material3.DayNight.NoActionBar`, lo que desactiva la barra de acción nativa y le da control total al diseño personalizado.

---

## 12. Resumen de Archivos Clave

| Archivo | Responsabilidad |
|---|---|
| `MainActivity.kt` | Punto de entrada de la app; solo infla el layout |
| `AppDatabase.kt` | Singleton de Room; configura la BD `pb_database` v2 |
| `RetoDao.kt` | CRUD completo + consulta aleatoria para la tabla `retos` |
| `Reto.kt` | Modelo de datos (id, descripcion, timestamp) |
| `RetoRepository.kt` | Intermediario DAO ↔ ViewModel para operaciones de retos |
| `PokemonRepository.kt` | Consume la API de Pokémon y retorna URL de imagen aleatoria |
| `RetosViewModel.kt` | Lógica de negocio: insertar, editar, eliminar retos; estado como `StateFlow` |
| `AudioViewModel.kt` | Gestión completa de audio: música de fondo + sonido de giro |
| `HomeFragment.kt` | Pantalla principal; animación de botella; dispara el reto aleatorio |
| `RetosFragment.kt` | Lista los retos; maneja el diálogo de eliminación (HU 9.0) |
| `AgregarRetoDialog.kt` | Diálogo para crear un nuevo reto (HU 6.0) |
| `EditarRetoDialog.kt` | Diálogo para editar un reto existente (HU 8.0) |
| `RetosAdapter.kt` | Adaptador del RecyclerView; gestiona clic en Editar y Eliminar |
| `CustomToolbarView.kt` | Vista personalizada de toolbar con 5 acciones |
| `SplashFragment.kt` | Pantalla de carga de 5 segundos con animación de botella |
