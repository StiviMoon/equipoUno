# Reporte de Contribución: Implementación de Módulo de Retos

**Desarrollador:** (Tu nombre)
**Historias de Usuario Asignadas:** HU 12 (Mostrar reto aleatorio) y HU 9.0 (Cuadro de diálogo eliminar reto)

Este documento detalla el trabajo que realicé en el proyecto grupal, explicando paso a paso qué hice y cómo estructuré el código para cumplir estrictamente con cada uno de los Criterios de Aceptación (CA) solicitados para mis tareas.

---

## 1. Historia de Usuario 12: Mostrar Reto Aleatorio

**Objetivo Asignado:** Mostrar un cuadro de diálogo al jugador con un reto aleatorio y la imagen de un Pokémon, asegurando un diseño y comportamiento muy específicos.

### ¿Qué hice y cómo cumplí los criterios?

*   **CA 1 (Diseño del cuadro):** Construí el diseño visual utilizando el archivo XML `dialog_random_reto.xml`. Para lograr el estilo solicitado, creé un fondo negro degradado con transparencia (`bg_dialog_rounded.xml`) y apliqué bordes redondeados de color blanco.
*   **CA 2 (Círculo con Pokémon aleatorio):** En la parte superior del diálogo, construí una vista circular con borde blanco y fondo negro (`bg_circle_pokemon.xml`). Para cumplir con el requerimiento de la imagen aleatoria, programé en `HomeFragment.kt` y `PokemonRepository.kt` una conexión HTTP a la API indicada (`pokedex.json`). El código descarga el JSON, selecciona un Pokémon al azar en segundo plano y muestra su imagen dentro de este círculo.
*   **CA 3 (Texto del reto desde la BD):** Agregué un componente de texto configurado en color blanco y negrita (bold). Implementé la lógica en Kotlin para conectarme a la base de datos local (Room) mediante `dao.getRandomReto()`, lo que extrae un reto al azar y carga automáticamente su descripción en el cuadro de diálogo.
*   **CA 4 (Botón Naranja "Cerrar"):** Ubiqué un botón en la parte inferior central del diálogo. Le apliqué un color naranja mediante el recurso `btn_orange_rounded.xml`. Ajusté las restricciones (constraints) y los márgenes para lograr el efecto exacto de la imagen: la parte superior del botón queda por dentro del diálogo y la inferior sobresale por fuera.
*   **CA 5 y 6 (Comportamiento de cierre):** Programé el evento del clic del botón "Cerrar" para que oculte el diálogo (`dialog.dismiss()`) y reactive el progreso normal del juego. Para garantizar que el cuadro **no desaparezca** al hacer clic por fuera, configuré explícitamente la propiedad `dialog.setCancelable(false)` en la inicialización del cuadro.

---

## 2. Historia de Usuario 9.0: Cuadro de Diálogo Eliminar Reto

**Objetivo Asignado:** Crear un cuadro de diálogo de confirmación para que el jugador pueda decidir de forma segura si elimina o no un reto específico de su lista.

### ¿Qué hice y cómo cumplí los criterios?

*   **CA 1 (Fondo blanco):** Creé el diseño `dialog_delete_reto.xml` asignándole un fondo de color completamente blanco y esquinas redondeadas (`bg_dialog_white_rounded.xml`).
*   **CA 2 (Título superior):** Coloqué un texto en la parte superior centrado, de color negro y en formato negrita (bold), con la frase exacta exigida: *"¿Desea eliminar el siguiente reto?:"*.
*   **CA 3 (Descripción del reto):** Implementé la lógica en `RetosFragment.kt` para que, cuando el usuario seleccione borrar un reto, el fragmento tome la descripción dinámica desde la base de datos y la envíe al cuadro de diálogo para ser mostrada en pantalla debajo del título.
*   **CA 4 (Opción "NO"):** Creé un texto interactivo con la palabra "NO" en color naranja (`#FF6F00`). Le programé un evento de clic que, al ser presionado, simplemente cierra el cuadro de diálogo (`dialog.dismiss()`) y devuelve al jugador a la ventana de "Agregar y listar retos" intacta.
*   **CA 5 (Opción "SI"):** Añadí el texto interactivo "SI", también en color naranja. Al hacer clic sobre él, el código se comunica con el `RetosViewModel` para ejecutar la función `eliminarReto(reto)`. Esto desencadena una instrucción `DELETE` en la base de datos local (SQLite/Room). Al finalizar el borrado, el diálogo se cierra y la lista de retos en pantalla se refresca inmediatamente sin mostrar ya ese reto.
*   **CA 6 (Bloqueo de cierre externo):** Tal como lo hice en la otra HU, bloqueé el cierre accidental del cuadro utilizando `dialog.setCancelable(false)`. Con esto, aseguré que el diálogo solo pueda desaparecer de la pantalla si el usuario toma una decisión consciente (pulsando "NO" o "SI").
