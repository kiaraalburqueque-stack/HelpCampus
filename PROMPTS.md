# Prompts utilizados

Fase 2 (rama `mejora-ia`). IA usada: **Claude** (asistente de IA de Anthropic). El docente autorizó usarla durante la semana de olimpiadas.
Regla de todas las mejoras: respetar MVVM. La View no usa DAO ni Repository, el ViewModel solo conoce la interface `AyudaRepository`, y cualquier función nueva se declara primero en la interface.

## Prompt 1 — Tiempo promedio de resolución (+ satisfacción)
**Texto enviado a la IA:**
> Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un AyudaRepository (interface). Agrega en el panel de soporte el tiempo promedio de resolución de los tickets cerrados (fechaCierre - fecha) y, para que sea más útil, también la satisfacción promedio (promedio de la calificación). Las consultas van en TicketDao, las funciones en AyudaRepository y su implementación, y los valores se exponen como StateFlow en PanelViewModel. La View solo los muestra. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** dos consultas `AVG(...)` en `TicketDao` que devuelven `Flow<Double?>`, dos funciones nuevas en la interface y en `AyudaRepositoryRoom`, dos `StateFlow` en `PanelViewModel`, la función pura `duracionCorta()` y una tarjeta "Indicadores de calidad" en `PanelScreen`.

**Qué tuve que corregir yo:** revisé que el ViewModel exponga el número (`Long?`) y no el texto ya formateado, para que el formato quede del lado de la vista. También revisé que, si no hay tickets cerrados, `AVG` devuelve `null` y la pantalla muestre "Sin datos" en vez de fallar.

**¿Respeta MVVM?:** Sí. La consulta está en el DAO, el contrato en la interface, la conversión a `StateFlow` en el ViewModel y la vista solo dibuja.

## Prompt 2 — Contador de SLA por prioridad
**Texto enviado a la IA:**
> En la misma app, cada ticket debe mostrar el tiempo restante según su prioridad (Alta 4 h, Media 24 h, Baja 72 h). Quiero que se actualice solo cada minuto y que tenga una barra que cambie de verde a ámbar a rojo. La regla del SLA va en la capa ViewModel como función pura, no en el Composable. Muéstralo en Mis tickets y en la Bandeja.

**Qué generó la IA:** `Sla.kt`, con `horasSla()`, `calcularSla()` y un `relojPorMinuto()` hecho con `flow { while(true) { emit(...); delay(60_000) } }`. Agregó un `StateFlow<Map<Int, EstadoSla>>` en los dos ViewModels, hecho con `combine(tickets, reloj)`, y el componente `IndicadorSla`, que anima el color y el progreso.

**Qué tuve que corregir yo:**
- La IA pasó el pie de la tarjeta como `slas[id]?.let { sla -> { IndicadorSla(sla) } }`. Así Kotlin no siempre reconoce la lambda como `@Composable`, así que lo cambié a `if (sla != null) { { IndicadorSla(sla) } } else null`.
- En `BandejaScreen` usaba `sla` sin declararlo (`val sla = slas[t.ticket.id]`), lo que no compilaba. Lo agregué.

**¿Respeta MVVM?:** Sí. El cálculo del SLA está en la capa ViewModel y la tarjeta solo recibe el resultado.

## Prompt 3 — Búsqueda avanzada en la bandeja
**Texto enviado a la IA:**
> En la bandeja de soporte quiero filtrar por prioridad (varias a la vez), por categoría y por texto al mismo tiempo, además del chip de estado. El texto debe buscar en el título, el número, el nombre del cliente, la ubicación y la descripción. Todo el filtrado va en BandejaViewModel con combine, sin tocar el DAO, y debe conservar el orden por prioridad. Agrega un botón con un globito que muestre cuántos filtros están activos y un botón "Limpiar filtros".

**Qué generó la IA:** una clase `CriteriosBusqueda` que reúne todos los filtros y la regla `acepta()`, `combine(tickets, criterios)` en `BandejaViewModel` y un panel desplegable con `AnimatedVisibility`, `FilterChip` y un `BadgedBox`.

**Qué tuve que corregir yo:** comprobé que `filter()` no cambia el orden que entrega el DAO (ALTA → MEDIA → BAJA). También pedí que "Limpiar filtros" conserve el chip de estado que ya estaba elegido.

**¿Respeta MVVM?:** Sí. El DAO no cambió y todo el filtrado vive en el ViewModel.

## Prompt 4 — Sugerencia inteligente de categoría y prioridad (mejora propia)
**Texto enviado a la IA:**
> Mientras el estudiante escribe el título y la descripción de un ticket, quiero sugerirle la categoría y la prioridad buscando palabras clave (por ejemplo "wifi" lleva a Red / Wi-Fi, y "urgente" o "todo el laboratorio" llevan a Alta). Que funcione sin internet, que explique qué palabras detectó y que el estudiante decida con un botón "Aplicar". La lógica va en la capa ViewModel.

**Qué generó la IA:** el objeto `AsistenteTicket.analizar()`, una función pura con palabras clave por ícono de categoría, un `StateFlow<Sugerencia?>` en `NuevoTicketViewModel` hecho con `combine(form, categorias)`, `aplicarSugerencia()` y una tarjeta animada en `NuevoTicketScreen`.

**Qué tuve que corregir yo:** pedí comparar sin tildes (con `Normalizer`) y por palabra completa (con `Regex`), para que "contraseña" coincida con "contrasena" y "red" no coincida a medias dentro de otra palabra. Además, la sugerencia solo aparece si propone algo distinto de lo que el estudiante ya eligió.

**¿Respeta MVVM?:** Sí. La lógica no usa Android y solo recibe la lista de categorías activas que ya entrega el Repository.

## Prompt 5 — Animaciones y estados vacíos
**Texto enviado a la IA:**
> Agrega animaciones sin cambiar la lógica: transiciones entre pantallas, ítems de lista que se acomodan solos, contadores que "cuentan" hasta su valor, la dona y las barras que crecen al aparecer, el stepper que cambia de color con transición y las estrellas que rebotan al cerrar un ticket. Para las listas vacías, dibuja con Canvas una ilustración simple y animada.

**Qué generó la IA:** `enterTransition` y `exitTransition` en el `NavHost`, `Modifier.animateItem()`, `animateIntAsState`, `Animatable` en la dona, `animateFloatAsState` en las barras, `animateColorAsState` en el stepper, `spring` en las estrellas y un `EstadoVacio` dibujado con Canvas que usa `rememberInfiniteTransition`.

**Qué tuve que corregir yo:** según la versión de Compose, `animateItem()` puede pedir `@OptIn(ExperimentalFoundationApi::class)`, así que lo agregué en las pantallas que lo usan.

**¿Respeta MVVM?:** Sí. Solo cambió la capa View.

## Prompt 6 — Registro de estudiante
**Texto enviado a la IA:**
> En el login, el enlace "¿No tienes cuenta? Regístrate" no hace nada. Crea una pantalla de registro de estudiante con nombre, correo, contraseña y confirmación. Las validaciones van en un RegistroViewModel: nombre de al menos 3 letras, correo con formato válido y único, contraseña de al menos 4 caracteres y confirmación igual. Los errores se muestran debajo de cada campo. Agrega las funciones primero en AyudaRepository.

**Qué generó la IA:** `existeCorreo()` y `registrarEstudiante()` en la interface y en `AyudaRepositoryRoom`, `insertar()` y `contarPorCorreo()` en `UsuarioDao`, un `RegistroViewModel` con `FormularioRegistro`, la pantalla `RegistroScreen`, la ruta `REGISTRO` y una función `entrarComo()` en `SesionViewModel` que iniciaba sesión automáticamente al terminar el registro.

**Qué tuve que corregir yo:**
- La IA hacía que, al registrarse, se entrara directo a la app. Una app real no funciona así: primero te registras y luego inicias sesión. Quité `entrarComo()`. Ahora el registro vuelve al Login con un aviso verde de "Cuenta creada", la pestaña Estudiante elegida y el correo ya escrito. El correo se pasa con el `SavedStateHandle` de la entrada de navegación.
- El registro guardaba el correo en minúsculas, pero el login lo comparaba tal como se escribía. Así, "Ana@Campus.test" no podía entrar. Cambié el login para que también use minúsculas.
- Además, la validación del formato del correo usa `Regex` en vez de `android.util.Patterns`, para que el ViewModel no dependa de Android.

**¿Respeta MVVM?:** Sí. La pantalla no valida nada: el ViewModel aplica las reglas y el Repository hace la consulta del correo único y la inserción. El rol siempre es CLIENTE: las cuentas de Soporte no se crean desde la app.

## Reflexión
- **Qué mejoró la IA y qué no:** me ayudó a escribir rápido el código repetitivo (consultas, `StateFlow`, componentes). Pero no garantiza que compile: hay que revisar cada archivo.
- **Qué error cometió:** dejó una variable sin declarar y usó una forma de lambda que Compose puede no reconocer como `@Composable`. Los encontré leyendo el código antes de compilar.
- **Qué aprendí sobre MVVM:** las reglas (SLA, filtros, sugerencias) son funciones puras en la capa ViewModel, así que se pueden explicar y probar sin pantallas.
- **Qué aprendí sobre Room:** `AVG`, `LEFT JOIN` y `COUNT` resuelven en SQL lo que de otro modo serían bucles. Además, con `Flow` todo se actualiza solo.
- **Qué aprendí sobre las reglas de negocio:** deben vivir en un solo lugar. Por eso la interface del Repository es el contrato que no se rompe.
