# AyudaCampus — mesa de ayuda TI del campus

App Android hecha con **Kotlin + Jetpack Compose + Material 3**, **Room 2.6.1**, **DataStore** y **Navigation Compose**, siguiendo el patrón **MVVM + Repository (interface)**.

Tiene dos perfiles en el mismo dispositivo, que comparten la misma base de datos Room:

- **Estudiante** (cian): reporta incidencias, conversa con Soporte, califica la atención y cierra o reabre sus tickets.
- **Soporte TI** (violeta): ve el panel con gráficos, atiende la bandeja ordenada por prioridad, cambia estados y prioridades, responde y administra las categorías.

> Modo práctica (local): todos los datos son ficticios (Ley 29733).

## Cuentas de prueba

| Perfil | Correo | Contraseña |
|---|---|---|
| Estudiante | kiara@campus.test | 1234 |
| Soporte TI | soporte@campus.test | admin |

## Arquitectura MVVM + Repository

```
VIEW (Composable)
   | eventos (clics)          ▲ collectAsState()
   ▼                          |
VIEWMODEL (StateFlow + reglas de negocio)
   | llama                    ▲ Flow / resultados
   ▼                          |
AyudaRepository (interface)
   | la implementa
   ▼
AyudaRepositoryRoom ──► DAO ──► SQLite (ayudacampus.db)

DataStore (solo preferencias) ──► ViewModel
```

| Capa | Conoce a | NO debe |
|---|---|---|
| View (ui/) | Solo a su ViewModel | Usar DAO, Repository ni reglas de negocio |
| ViewModel | Solo al Repository (interface) | Conocer Composables ni el DAO |
| Repository | Los DAOs (en la implementación) | Conocer pantallas ni ViewModels |
| DAO / Room | SQL y entidades | Conocer ViewModels |

**Regla de oro:** si es una lectura que debe actualizar la pantalla, se usa `Flow` (sin `suspend`). Si es una escritura o una acción única, se usa `suspend`.

## Base de datos (4 tablas)

| Tabla | Relación |
|---|---|
| usuarios | — |
| categorias | — |
| tickets | N tickets → 1 cliente; N tickets → 1 categoría |
| comentarios | N comentarios → 1 ticket (`onDelete = CASCADE`) |

`TicketConDetalle` junta un ticket con sus comentarios, su categoría y su cliente usando `@Embedded` + `@Relation`. Las consultas que lo devuelven llevan `@Transaction`.

## Estructura de paquetes

```
com.tecsup.ayudacampus
├── MainActivity.kt
├── AyudaApp.kt                      crea la base, el repository y las preferencias
├── data
│   ├── local
│   │   ├── entity      Categoria · Usuario · Ticket · Comentario
│   │   ├── dao         CategoriaDao · UsuarioDao · TicketDao
│   │   ├── relation    TicketConDetalle
│   │   ├── AyudaDatabase.kt         singleton de Room
│   │   └── DatosIniciales.kt        datos de prueba (solo la primera vez)
│   ├── repository
│   │   ├── AyudaRepository.kt       interface (el contrato)
│   │   └── AyudaRepositoryRoom.kt   implementación con Room
│   └── prefs
│       └── PreferenciasStore.kt     DataStore "ajustes"
├── model               Rol · EstadoTicket · Prioridad · ConteoCategoria
├── viewmodel
│   ├── AyudaViewModelFactory.kt
│   ├── SesionViewModel.kt · MisTicketsViewModel.kt · NuevoTicketViewModel.kt
│   ├── TicketDetalleViewModel.kt · PanelViewModel.kt · BandejaViewModel.kt
│   ├── AtenderViewModel.kt · CategoriasViewModel.kt
│   └── FiltroEstado.kt
└── ui
    ├── theme           Color · Type · Theme (AyudaTheme según el rol)
    ├── components      AppTopBar · AyudaButton · AyudaTextField · Chips · TicketCard
    │                   BurbujaComentario · StepperTicket · Estrellas · ...
    │   └── graficos    DatoGrafico · GraficoDona · GraficoBarras (Canvas)
    ├── navigation      Rutas · NavAyuda (un grafo por perfil)
    ├── cliente         Splash · Login · MisTickets · NuevoTicket · DetalleTicket · Perfil
    └── soporte         Panel · Bandeja · AtenderTicket · Categorias · FormularioCategoria · Ajustes
```

## Reglas de negocio (en los ViewModels)

| Regla | Dónde |
|---|---|
| El estudiante solo ve sus tickets; Soporte ve todos | MisTicketsViewModel / BandejaViewModel |
| Título ≥ 5, descripción ≥ 10, categoría activa; prioridad MEDIA y estado ABIERTO por defecto | NuevoTicketViewModel |
| Soporte solo elige ABIERTO, EN_PROCESO o RESUELTO; nunca CERRADO | AtenderViewModel |
| Cerrar solo si está RESUELTO y con calificación de 1 a 5 | TicketDetalleViewModel |
| Reabrir solo si está RESUELTO (borra la calificación y la fecha de cierre) | TicketDetalleViewModel |
| No se comenta con texto vacío ni en un ticket CERRADO | TicketDetalleViewModel / AtenderViewModel |
| Bandeja: ALTA → MEDIA → BAJA y, dentro de cada una, la más reciente primero | TicketDao (ORDER BY CASE) |
| Alerta: tickets ALTA y ABIERTO con más de 1 hora | PanelViewModel |
| No se elimina una categoría que ya tiene tickets | CategoriasViewModel |

## Capturas

| Estudiante | | |
|---|---|---|
| ![Login](docs/capturas/cliente-login.png) | ![Mis tickets](docs/capturas/cliente-mis-tickets.png) | ![Detalle](docs/capturas/cliente-detalle.png) |

| Soporte TI | | |
|---|---|---|
| ![Panel](docs/capturas/soporte-panel.png) | ![Bandeja](docs/capturas/soporte-bandeja.png) | ![Atender](docs/capturas/soporte-atender.png) |

## Para pensar

| Pregunta | Respuesta |
|---|---|
| ¿Por qué las reglas de estado están en el ViewModel y no en el DAO ni en el Composable? | Porque son lógica de negocio: el DAO solo guarda datos y el Composable solo dibuja. En el ViewModel se prueban sin la UI y siguen valiendo si cambia la pantalla o el origen de datos. |
| ¿Por qué crearTicket es suspend y observarTicket es Flow? | crearTicket es una acción que se hace una vez y termina. observarTicket debe avisar cada vez que el ticket cambia, por ejemplo cuando Soporte responde. |
| ¿Qué pasaría si un ticket pudiera cerrarse sin calificación? | Se perdería la medida de satisfacción, y un ticket podría cerrarse sin que el estudiante confirme que se resolvió su problema. |
| ¿Qué debería cambiar si mañana los tickets viven en un servidor? | Solo crear otra implementación de AyudaRepository (por ejemplo, con Retrofit) y usarla en AyudaApp. Los ViewModels y las pantallas no cambian. |
