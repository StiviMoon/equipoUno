# Juego Pico Botella — Guía de Sustentación

## Estructura del proyecto

```
app/src/main/java/com/example/pb/
├── model/
│   └── Reto.kt                      ← Entidad SQLite (tabla)
├── data/
│   ├── RetoDao.kt                   ← Queries SQL (Room)
│   └── AppDatabase.kt               ← Singleton base de datos
├── repository/
│   ├── RetoRepository.kt            ← Intermediario DAO ↔ ViewModel
│   └── PokemonRepository.kt         ← Genera URL aleatoria de Pokémon
├── viewmodel/
│   ├── RetosViewModel.kt            ← Lógica CRUD retos + estado UI
│   └── AudioViewModel.kt            ← Estado y control de audio global
└── ui/
    ├── splash/SplashFragment.kt     ← HU1, HU2 C7
    ├── home/HomeFragment.kt         ← HU2, HU11, HU12
    ├── instrucciones/
    │   └── InstruccionesFragment.kt ← HU5
    ├── retos/
    │   ├── RetosFragment.kt         ← HU6
    │   ├── AgregarRetoDialog.kt     ← HU7
    │   ├── EditarRetoDialog.kt      ← HU8
    │   └── RetosAdapter.kt          ← RecyclerView adapter
    └── toolbar/
        └── CustomToolbarView.kt     ← HU3

app/src/main/res/
├── layout/                          ← XMLs de cada pantalla y diálogo
├── navigation/nav_graph.xml         ← Grafo de navegación
├── raw/
│   ├── bg_music.mp3                 ← Música de fondo (HU2 C7)
│   └── spin_sound.ogg               ← Sonido giro botella (HU11 C2)
└── drawable/
    ├── bg_wood.xml                  ← Fondo madera (HU2 C1)
    └── bg_home_gradient.xml         ← Fondo gradiente (Splash original)
```

---

## 1. Arquitectura MVVM con Repository

**Flujo de datos:**
```
Fragment → ViewModel → Repository → DAO → Room (SQLite)
```

### Model — `model/Reto.kt`
Define la tabla `retos` en SQLite. Room lee las anotaciones y crea la BD automáticamente.
```kotlin
@Entity(tableName = "retos")
data class Reto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val timestamp: Long = System.currentTimeMillis()
)
```
- `@Entity` → esta clase ES una tabla
- `@PrimaryKey(autoGenerate = true)` → Room asigna el ID solo

### DAO — `data/RetoDao.kt`
Define las operaciones SQL. Room genera el código por debajo.
```kotlin
@Query("SELECT * FROM retos ORDER BY id DESC")   // nuevos arriba — HU6 C6
fun getAll(): Flow<List<Reto>>

@Query("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1")  // aleatorio — HU12 C3
suspend fun getRandomReto(): Reto?

@Insert   suspend fun insert(reto: Reto)
@Update   suspend fun update(reto: Reto)
@Delete   suspend fun delete(reto: Reto)
```

### Database — `data/AppDatabase.kt`
Singleton: solo existe UNA instancia de la BD en toda la app.
```kotlin
@Database(entities = [Reto::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun retoDao(): RetoDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase { ... }
    }
}
```
- `@Volatile` garantiza visibilidad entre hilos
- `synchronized` evita que dos hilos creen dos instancias

### Repository — `repository/RetoRepository.kt`
El ViewModel no toca el DAO directamente. Pasa por el Repository.
```kotlin
class RetoRepository(private val dao: RetoDao) {
    fun getAllRetos(): Flow<List<Reto>> = dao.getAll()
    suspend fun insertReto(reto: Reto) = dao.insert(reto)
    suspend fun updateReto(reto: Reto) = dao.update(reto)
    suspend fun deleteReto(reto: Reto) = dao.delete(reto)
}
```
**Por qué existe**: si mañana se agrega una API remota, solo se toca el Repository, no el ViewModel ni el Fragment.

### ViewModel — `viewmodel/RetosViewModel.kt`
Sobrevive rotaciones de pantalla. Contiene el estado de la UI.
```kotlin
class RetosViewModel(private val repository: RetoRepository) : ViewModel() {
    val uiState: StateFlow<RetosUiState>   // Loading | Empty | Success

    fun insertarReto(descripcion: String) {
        viewModelScope.launch {            // corrutina ligada al ViewModel
            repository.insertReto(Reto(descripcion = descripcion))
        }
    }
}
```
- `viewModelScope.launch` → si el usuario sale, la corrutina se cancela sola
- `StateFlow` → los Fragments observan cambios reactivamente

### Fragment — `ui/retos/RetosFragment.kt`
Solo observa el ViewModel. No sabe nada de BD.
```kotlin
val viewModel: RetosViewModel by viewModels { ... }

lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            when (state) {
                is RetosUiState.Success -> adapter.submitList(state.retos)
                is RetosUiState.Empty   -> binding.tvEmpty.visibility = View.VISIBLE
                is RetosUiState.Loading -> { }
            }
        }
    }
}
```

---

## 2. Corrutinas

Las corrutinas permiten hacer operaciones lentas (BD, red) sin congelar la pantalla.

### Dónde se usan

| Archivo | Tipo | Para qué |
|---------|------|----------|
| `RetosViewModel.kt` | `viewModelScope.launch` | CRUD en BD sin bloquear UI |
| `RetosFragment.kt` | `lifecycleScope.launch` + `repeatOnLifecycle` | Observar Flow de retos |
| `HomeFragment.kt` | `lifecycleScope.launch` + `async` | Giro + countdown + BD + red en paralelo |
| `RetoDao.kt` | `suspend fun` | Marcan que solo se llaman desde corrutina |
| `AppDatabase.kt` | `Dispatchers.IO` | Hilo de background para BD |

### Ejemplo clave — `HomeFragment.spinBottle()`
```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    // async lanza en paralelo mientras la botella gira (5.7s disponibles)
    val retoDeferred = async(Dispatchers.IO) { dao.getRandomReto() }
    val pokemonUrl   = PokemonRepository().getRandomPokemonImageUrl()

    audioViewModel.playSpinSound()
    launchSpinAnimation()
    delay(2500L)              // espera sin bloquear UI
    audioViewModel.stopSpinSound()

    // countdown
    for (i in 3 downTo 0) {
        binding.tvCountdown.text = i.toString()
        delay(800L)
    }

    mostrarRetoAleatorio(
        retoTexto  = retoDeferred.await()?.descripcion ?: "No hay retos.",
        pokemonUrl = pokemonUrl
    )
}
```
- `async` → lanza la consulta BD en paralelo mientras la botella gira
- `delay()` → pausa la corrutina sin bloquear el hilo
- `await()` → espera el resultado cuando ya se necesita

---

## 3. Fragments — pantallas de la app

Cada pantalla es un Fragment dentro de una sola Activity (`MainActivity`).

| Fragment | HU | Qué hace |
|----------|----|----------|
| `SplashFragment` | HU1, HU2 C7 | Muestra splash 5s, inicia música de fondo |
| `HomeFragment` | HU2, HU11, HU12 | Gira botella, countdown, muestra reto |
| `InstruccionesFragment` | HU5 | Reglas del juego, pausa audio |
| `RetosFragment` | HU6 | Lista retos con RecyclerView |
| `AgregarRetoDialog` | HU7 | DialogFragment para crear reto |
| `EditarRetoDialog` | HU8 | DialogFragment para editar reto |

### Audio compartido entre Fragments
Todos usan `activityViewModels()` para acceder al mismo `AudioViewModel`:
```kotlin
private val audioViewModel: AudioViewModel by activityViewModels()
```
El ViewModel vive en la Activity — por eso el audio no se reinicia al navegar.

### Ciclo de vida del audio

| Evento | Acción |
|--------|--------|
| App abre (Splash) | `startBgMusic()` |
| Entra a Instrucciones o Retos | `pauseBgMusic()` |
| Vuelve a Home | `resumeIfEnabled()` |
| Presiona girar botella | `pauseTemporarily()` |
| Cierra dialog de reto | `resumeIfEnabled()` |
| Toggle botón audio | `toggleAudio()` |

---

## 4. Navigation

**Archivo**: `res/navigation/nav_graph.xml`

```
[SplashFragment]
      │ 5 segundos
      ▼
[HomeFragment] ──────────── popUpTo Splash (inclusive)
      │                     → atrás en Home = salir app (HU1 C5)
      ├──→ [InstruccionesFragment]
      └──→ [RetosFragment]
```

### Cómo se navega en código

```kotlin
// Ir a otra pantalla
findNavController().navigate(R.id.action_home_to_instrucciones)
findNavController().navigate(R.id.action_home_to_retos)

// Volver atrás
findNavController().navigateUp()
```

### NavHostFragment
`MainActivity` solo tiene esto — toda la navegación la maneja el NavController:
```xml
<androidx.fragment.app.FragmentContainerView
    app:navGraph="@navigation/nav_graph"
    app:defaultNavHost="true" />
```

---

## 5. Base de datos SQLite con Room

Room es una librería que envuelve SQLite y elimina el código repetitivo.

### Sin Room (SQLite puro):
```java
db.execSQL("CREATE TABLE retos (id INTEGER PRIMARY KEY, descripcion TEXT)");
Cursor c = db.rawQuery("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1", null);
```

### Con Room:
```kotlin
@Query("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1")
suspend fun getRandomReto(): Reto?
```
Room genera todo el código de abajo automáticamente en tiempo de compilación.

### Flujo completo de insertar un reto

```
Usuario escribe texto en AgregarRetoDialog
    → btnGuardar.setOnClickListener
    → viewModel.insertarReto(descripcion)          [Fragment]
    → viewModelScope.launch { repository.insertReto(...) }  [ViewModel]
    → dao.insert(Reto(descripcion = descripcion))  [Repository → DAO]
    → Room escribe en SQLite "pb_database"         [Room]
    → Flow<List<Reto>> emite nueva lista           [DAO → Repository]
    → uiState actualiza a Success(retos)           [ViewModel]
    → adapter.submitList(retos)                    [Fragment]
    → RecyclerView muestra el nuevo reto arriba    [UI]
```

---

## 6. Historias de Usuario — criterios cumplidos

### HU2 — Ventana Home Principal
- **C1** (fondo madera): `android:background="@drawable/bg_wood"` en `fragment_home.xml`
- **C5** (contador sobre botella): `tvCountdown` en XML, se muestra POST-giro en `spinBottle()`
- **C7** (audio de fondo): `AudioViewModel.startBgMusic()` llamado en `SplashFragment.onViewCreated()`

### HU5 — Instrucciones
- **C1** (pausar audio): `audioViewModel.pauseBgMusic()` en `InstruccionesFragment.onViewCreated()`
- **C3** (toolbar + flecha + resume): toolbar nativa con `setNavigationOnClickListener { audioViewModel.resumeIfEnabled(); navigateUp() }`

### HU6 — Agregar y listar retos
- **C1** (pausar audio): `audioViewModel.pauseBgMusic()` en `RetosFragment.onViewCreated()`
- **C3** (toolbar + resume al volver): `toolbarRetos.setNavigationOnClickListener { audioViewModel.resumeIfEnabled(); navigateUp() }`
- **C6** (nuevos retos arriba): `ORDER BY id DESC` en `RetoDao.getAll()`

### HU7 — Dialog agregar reto
- **C5** (botón Guardar inhabilitado): XML `android:enabled="false"` + TextWatcher en `AgregarRetoDialog.setupTextWatcher()` que cambia color gris/naranja según si hay texto

### HU11 — Giro de botella
- **C5** (countdown post-giro): countdown corre DESPUÉS de `delay(2500L)` en `spinBottle()`
- **C7** (botón desaparece): `binding.btnPress.visibility = View.INVISIBLE` al inicio, `View.VISIBLE` en `resetButton()`
- **C8** (audio pausado durante partida): `pauseTemporarily()` al girar, `resumeIfEnabled()` al cerrar dialog

### HU12 — Mostrar reto aleatorio
- **C2** (imagen Pokémon aleatoria): `PokemonRepository` genera número 1–151, Coil carga la imagen con `imageView.load(url)`
- **C3** (reto aleatorio de BD): `dao.getRandomReto()` con `ORDER BY RANDOM() LIMIT 1`
- **C6** (no cierra al tocar afuera): `dialog.setCancelable(false)`

---

## Puntos clave para la sustentación

1. **¿Por qué MVVM?** — Separación de responsabilidades. El Fragment no sabe de BD, el DAO no sabe de UI. Si cambias la BD, el Fragment no se toca.

2. **¿Por qué Repository si solo hay una fuente de datos?** — Buena práctica. Si se agrega una API remota, solo se modifica el Repository.

3. **¿Por qué corrutinas y no threads?** — Corrutinas son livianas, se cancelan automáticamente con el ciclo de vida, y `suspend` hace el código legible como si fuera síncrono.

4. **¿Por qué un solo Activity?** — Patrón Single Activity. Navigation Component maneja todo el stack de Fragments. Más eficiente que múltiples Activities.

5. **¿Cómo el audio persiste entre pantallas?** — `AudioViewModel` usa `activityViewModels()` — vive en la Activity, no en el Fragment. Todos los Fragments comparten la misma instancia.
