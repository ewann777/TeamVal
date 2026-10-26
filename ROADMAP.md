# TeamVal — Roadmap

## Regla de trabajo

Antes de cada etapa, consultar `PROJECT_SPEC.md` y este documento. Trabajar solo
en la etapa solicitada, verificar su resultado, actualizar la documentación y
detenerse. No iniciar automáticamente la siguiente etapa.

El orden definitivo acordado con el usuario es el siguiente. La etapa 5 está
completada. El bloque A de 5.5 está guardado en `90e3ef9`; el bloque B (Buscar
equipo) y las mejoras posteriores se agrupan en la entrega v0.5.6. El registro
de cierre al final distingue las verificaciones históricas de los resultados
actuales. Cualquier implementación posterior requiere una nueva petición.

## Etapa 1 — Base visual y arquitectura

**Estado: completada; compilación y análisis estático verificados.**

- [x] Revisar la plantilla Android y los requisitos disponibles.
- [x] Documentar especificación, limitaciones de contexto y roadmap.
- [x] Organizar UI, navegación, tema y carpetas de dominio/datos.
- [x] Configurar identidad lavanda/carbón e inicio profesional.
- [x] Implementar navegación con destinos informativos provisionales.
- [x] Compilar APK de depuración y ejecutar Android Lint.
- [x] Registrar resultados y entregar resumen de archivos.

Límite: ninguna funcionalidad de stacks, scrims, tienda o monedas.

## Etapa 2 — Explorador de stacks

**Estado: completada; compilación, Lint y pruebas unitarias/de interfaz verificadas.**

- [x] Explorador con filtros combinados por rango, región y modalidad.
- [x] Tarjetas profesionales, contador y estados vacíos.
- [x] Formulario validado para crear publicaciones y listado propio.
- [x] Solicitudes para unirse con prevención de duplicados y stacks completos.
- [x] Bandejas recibidas/enviadas y aceptación/rechazo de candidatos simulados.
- [x] Repositorio en memoria, datos ficticios y reglas de cupo/propiedad.
- [x] ViewModel, estado observable y componentes reutilizables.
- [x] Integración con Inicio y subgrafo de navegación Stacks.
- [x] Actualizar especificación y pruebas unitarias de reglas de negocio.
- [x] Completar pruebas en emulador y registrar verificación final.

Los requisitos de producción (cuentas, backend, persistencia y reglas definitivas)
siguen pendientes de acuerdo.

## Etapa 3 — Perfiles, reputación y personalización

**Estado: completada; compilación, Lint y pruebas unitarias/de interfaz verificadas.**

- [x] Perfil de jugador con estadísticas de VALORANT simuladas y presentación personal.
- [x] Sistema de reputación con valoraciones por estrellas y métricas de demostración.
- [x] Editor de perfil con validación, vista previa en vivo y descarte de cambios.
- [x] Avatares, marcos, fondos e insignias seleccionables en tiempo real.
- [x] Apariencia global: tema claro/oscuro/sistema, color de acento HEX,
  fondo liso/aurora/cuadrícula y tipografía moderna/editorial/mono.
- [x] Contraste de texto garantizado con cualquier color personalizado, en ambos temas.
- [x] Guardado local de preferencias con DataStore Preferences (supervivencia al reinicio).
- [x] Pestaña Perfil, componentes reutilizables y reflejo del nombre en Stacks.

Límite: estadísticas y valoraciones son datos ficticios locales; sin tienda,
cosméticos adquiribles, monedas ni inventario (etapa 5).

## Etapa 3.5 — Refinamiento visual global

**Estado: completada; compilación, Lint y pruebas unitarias/de interfaz verificadas.**

- [x] Dirección visual premium: superficies de vidrio, degradados de acento y profundidad.
- [x] Tokens compartidos en `ui/theme/Visuals.kt`, derivados del esquema activo: siguen
  el color de acento, el tema claro/oscuro y la tipografía elegidos por el usuario.
- [x] Paneles, tarjetas y secciones con translucidez, borde de luz y sombra suave.
- [x] Botones principales en degradado con halo; chips, insignias, badges e iconos renovados.
- [x] Barra inferior de vidrio con filete luminoso y píldora de acento en la pestaña activa.
- [x] Inicio, Perfil, Personalización y Stacks con cabeceras de acento, orbes de luz y
  jerarquía renovada en botones, contadores y cabeceras de página.
- [x] Tipografía, formas (radios) y espaciados revisados; desenfoque real en el fondo
  Aurora sobre Android 12+, con degradados suaves equivalentes en versiones anteriores.
- [x] Sin cambios de arquitectura, de textos, de funcionalidad ni de reglas de negocio.

Límite: solo refinamiento visual; no añade funcionalidades nuevas ni inicia scrims.

## Etapa 4 — Equipos y scrims

**Estado: completada. Básica y ampliación 4.1 verificadas. Las tres pruebas
pendientes de 4.1 se reejecutaron correctamente el 25/10/2026; se conserva su
registro histórico y el resultado nominal al final.**

- Equipos: creación, composición, roles y detalle de equipo.
- Scrims: propuestas salientes y entrantes, estados, transiciones y resultados.
- Integración con perfiles de la etapa 3 (nombre visible desde el perfil).
- Ampliación 4.1: personalización avanzada de equipos (editor de logo A/B con
  recorte, perfil con vista previa) y reputación entre equipos (valoraciones
  de conducta con antiabusos).

## Etapa 5 — Tienda, monedas, recompensas e inventario

**Estado: completada y verificada el 25/10/2026. Ver resultados exactos al final.**

- Tienda de cosméticos vinculada a la personalización de perfiles y aplicación. ✓
- Monedas gratuitas (TeamVal Coins) y recompensas: saldo, gasto, historial y
  recompensas de un solo uso. ✓
- Artículos premium con precios en pesos chilenos (CLP) informativos y
  bloqueados: **sin pagos reales**. ✓
- Inventario de artículos obtenidos con filtro, estados y equipado. ✓
- Compras y transacciones simuladas durante el prototipo: sin pagos reales. ✓

## Verificación transversal

Estas tareas forman parte de las cinco etapas; no constituyen una etapa adicional.

- Validar flujos completos, accesibilidad, tamaños de pantalla y rendimiento.
- Resolver requisitos pendientes de distribución y servicios externos.
- Preparar la entrega con pruebas y documentación acordes al alcance final.

## Registro de verificación de la etapa 1

- Comando: `.\gradlew.bat :app:assembleDebug :app:lintDebug --console=plain`.
- JDK: runtime integrado en Android Studio (OpenJDK 25.0.3), mediante `JAVA_HOME`
  establecido únicamente para el proceso de compilación.
- Resultado final: `BUILD SUCCESSFUL`.
- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Lint: 0 errores, 3 avisos sobre actualizaciones disponibles de dependencias.
- Informe: `app/build/reports/lint-results-debug.html`.
- Sin dispositivos/emuladores conectados según `adb devices -l`.
- Pendiente de revisión manual: presentación visual, TalkBack, tamaño de texto,
  alternancia de pestañas, restauración al rotar y botón Atrás en dispositivo.

### Archivos creados

En la raíz:
- `PROJECT_SPEC.md`
- `ROADMAP.md`

Bajo `app/src/main/java/com/example/teamval/`:
- `TeamValApp.kt`
- `data/README.md`
- `domain/README.md`
- `navigation/TopLevelDestination.kt`
- `navigation/TeamValNavigationBar.kt`
- `navigation/TeamValNavHost.kt`
- `ui/components/ComingSoonBadge.kt`
- `ui/components/FeatureCard.kt`
- `ui/components/SectionIcon.kt`
- `ui/screens/home/HomeScreen.kt`
- `ui/screens/upcoming/UpcomingScreen.kt`
- `ui/theme/Shape.kt`

Bajo `app/src/main/res/drawable/`:
- `ic_home.xml`
- `ic_team.xml`
- `ic_challenge.xml`
- `ic_store.xml`

### Archivos actualizados

- `app/build.gradle.kts` y `gradle/libs.versions.toml`: Navigation Compose.
- `app/src/main/AndroidManifest.xml`: limpieza de atributos redundantes.
- `app/src/main/java/com/example/teamval/MainActivity.kt`: raíz Compose y barras del sistema.
- `app/src/main/java/com/example/teamval/ui/theme/Color.kt`, `Theme.kt`, `Type.kt`:
  identidad visual y tipografía.
- `app/src/main/res/values/colors.xml`, `themes.xml`, `strings.xml`: colores,
  tema de ventana y textos en español.

**Punto de parada histórico:** etapa 1 entregada; posteriormente se recibió
autorización explícita para la etapa 2 descrita arriba.

## Registro de entrega de la etapa 2

### Cómo probar la demo

1. Inicio → Descubrir Stacks: explorar publicaciones ajenas y combinar filtros.
2. Solicitar unirme: la tarjeta queda pendiente; consultar Solicitudes → Enviadas.
3. Mis publicaciones: gestionar las dos solicitudes iniciales; aceptar/rechazar.
4. Crear publicación: completar título/descripcion, rango, región, modalidad y tamaño.
5. La publicación aparece en Mis publicaciones. Abrir Gestionar solicitudes y
   usar Simular solicitud para añadir candidatos y decidir sobre ellos.
6. Alternar pestañas o recrear la Activity conserva datos y borrador durante la sesión.

### Archivos creados

Bajo `app/src/main/java/com/example/teamval/`:
- `domain/model/StackModels.kt`
- `domain/repository/StackRepository.kt`
- `data/fake/DemoStackData.kt`
- `data/repository/InMemoryStackRepository.kt`
- `ui/screens/stacks/StacksViewModel.kt`
- `ui/screens/stacks/StackExplorerScreen.kt`
- `ui/screens/stacks/CreateStackScreen.kt`
- `ui/screens/stacks/StackRequestsScreen.kt`
- `ui/components/StackLabels.kt`
- `ui/components/SelectionField.kt`
- `ui/components/StackCommon.kt`
- `ui/components/StackFiltersPanel.kt`
- `ui/components/StackPostCard.kt`
- `ui/components/JoinRequestCard.kt`

Recursos y pruebas:
- `app/src/main/res/values/strings_stacks.xml`
- `app/src/test/java/com/example/teamval/data/repository/InMemoryStackRepositoryTest.kt`
- `app/src/androidTest/java/com/example/teamval/StacksFlowTest.kt`

### Archivos actualizados

- `PROJECT_SPEC.md`, `ROADMAP.md` y los README de `data/` y `domain/`.
- `app/build.gradle.kts`, `gradle/libs.versions.toml`: integración Lifecycle Compose/ViewModel.
- `TeamValApp.kt`: composición de dependencias, estado y mensajes.
- `navigation/TeamValNavHost.kt`: subgrafo Stacks, creación y solicitudes.
- `ui/components/FeatureCard.kt`, `ui/screens/home/HomeScreen.kt` y
  `app/src/main/res/values/strings.xml`: acceso a Stacks disponible en Inicio.

### Verificación

- `:app:assembleDebug`: APK generado correctamente en
  `app/build/outputs/apk/debug/app-debug.apk`.
- `:app:lintDebug`: 0 errores y 3 avisos de versiones disponibles de dependencias
  ya presentes en la etapa 1. Informe: `app/build/reports/lint-results-debug.html`.
- `:app:testDebugUnitTest`: 9 pruebas nuevas de reglas de Stacks y la prueba
  heredada, todas correctas. Informe: `app/build/reports/tests/testDebugUnitTest/index.html`.
- `:app:connectedDebugAndroidTest`: 3 pruebas nuevas de flujo Compose y la prueba
  heredada, todas correctas en Pixel 6, Android 17 (API 37).
- Flujos comprobados: combinar/limpiar filtros, estado vacío, recreación de Activity
  con filtros/borrador, validación y creación, aceptar/rechazar candidatos,
  bloqueo de solicitudes duplicadas, consulta de enviadas, navegación y botón Atrás.
- Informe de interfaz: `app/build/reports/androidTests/connected/debug/index.html`.
- Una ejecución se interrumpió por desconexión del emulador; tras reiniciarlo,
  la ejecución completa final terminó con `BUILD SUCCESSFUL` y 0 fallos.
- No se realizó una auditoría de TalkBack ni una matriz de otros dispositivos.

Comandos reproducibles desde PowerShell (pruebas de interfaz con emulador arrancado):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

## Registro de entrega de la etapa 3

### Cómo probar la personalización

1. Pestaña Perfil: ver tarjeta de jugador, biografía, estadísticas simuladas y
   reputación con valoraciones ficticias.
2. Editar perfil: cambiar nombre, presentación, biografía, agente favorito, región,
   avatar, marco, fondo e insignias; la vista previa se actualiza al instante.
3. Apariencia: elegir tema (oscuro/claro/sistema), color de acento o valor HEX,
   fondo de aplicación y tipografía; la muestra y el resto de pantallas cambian en vivo.
4. Guardar conserva los cambios; Volver o el botón Atrás descartan los cambios sin guardar.
5. Reiniciar la aplicación: perfil y apariencia se conservan (DataStore Preferences).
6. Renombrar el perfil actualiza el nombre mostrado en las publicaciones de Stacks.

### Archivos creados

Bajo `app/src/main/java/com/example/teamval/`:
- `domain/model/Personalization.kt`
- `domain/repository/PreferencesRepository.kt`
- `data/repository/LocalPreferencesRepository.kt`
- `data/fake/DemoProfileData.kt`
- `ui/screens/profile/ProfileViewModel.kt`
- `ui/screens/profile/ProfileScreen.kt`
- `ui/screens/profile/EditProfileScreen.kt`
- `ui/screens/profile/AppearanceScreen.kt`
- `ui/components/AppBackdrop.kt`
- `ui/components/ProfileLabels.kt`
- `ui/components/PlayerAvatar.kt`
- `ui/components/ProfileHero.kt`
- `ui/components/CustomizationControls.kt`
- `ui/components/ReputationPanel.kt`

Recursos y pruebas:
- `app/src/main/res/values/strings_profile.xml`
- `app/src/main/res/drawable/ic_profile.xml`
- `app/src/test/java/com/example/teamval/data/repository/LocalPreferencesRepositoryTest.kt`
- `app/src/test/java/com/example/teamval/ui/theme/PersonalizationThemeTest.kt`
- `app/src/androidTest/java/com/example/teamval/PersonalizationFlowTest.kt`

### Archivos actualizados

- `PROJECT_SPEC.md`, `ROADMAP.md` y este registro.
- `gradle/libs.versions.toml` y `app/build.gradle.kts`: DataStore Preferences.
- `ui/theme/Theme.kt` y `ui/theme/Type.kt`: esquema dinámico por color de acento,
  tema claro/oscuro/sistema y familias tipográficas seleccionables.
- `MainActivity.kt` y `TeamValApp.kt`: tema desde la raíz, preferencias en la
  composición, estados de carga/error y ajuste de barras del sistema.
- `navigation/TopLevelDestination.kt`, `TeamValNavigationBar.kt` y `TeamValNavHost.kt`:
  pestaña Perfil y subgrafo de perfil/edición/apariencia.
- `domain/repository/StackRepository.kt`, `data/repository/InMemoryStackRepository.kt`,
  `ui/screens/stacks/StacksViewModel.kt`, `StackExplorerScreen.kt`,
  `CreateStackScreen.kt` y `strings_stacks.xml`: nombre de jugador compartido y
  acción IME de los campos de texto.

### Verificación

- `:app:assembleDebug`: `BUILD SUCCESSFUL`; APK en
  `app/build/outputs/apk/debug/app-debug.apk`.
- `:app:testDebugUnitTest`: 17 pruebas, 0 fallos (6 nuevas de preferencias,
  contraste, validación y reputación, más las heredadas). Informe:
  `app/build/reports/tests/testDebugUnitTest/index.html`.
- `:app:lintDebug`: 0 errores y 4 avisos de versiones disponibles de dependencias
  (3 heredados más DataStore). Informe: `app/build/reports/lint-results-debug.html`.
- `:app:connectedDebugAndroidTest`: 7 pruebas, 0 fallos en Pixel 6 / Android 17
  (API 37): perfil con validación y cosmética persistida, identidad reflejada en
  Stacks, apariencia global tras recrear la Activity, color HEX inválido sin
  guardar y descarte por Atrás, más las pruebas de la etapa 2.
- Ajuste tras fallos iniciales: el cierre del teclado se realiza con la acción IME
  de cada campo y el retroceso de las pruebas con `onBackPressedDispatcher`, ya que
  Espresso perdía el foco de ventana con el teclado abierto.
- Comprobación manual en emulador: pestaña Perfil y pantalla Apariencia renderizan
  correctamente mediante `uiautomator dump`.
- No se realizó una auditoría de TalkBack ni una matriz de otros dispositivos.

Comandos reproducibles desde PowerShell (pruebas de interfaz con emulador arrancado):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

## Registro de entrega de la etapa 3.5

### Qué cambia visualmente

1. Inicio: cabecera de marca con placa de icono en degradado y punto de acento,
   héroe con orbes luminosos, borde de luz y llamada a la acción en degradado.
2. Barra inferior: superficie translúcida con sombra y filete de luz superior;
   la pestaña activa se marca con píldora de acento.
3. Paneles: todas las tarjetas de Stacks, Perfil y editores usan el acabado de
   vidrio (relleno translúcido, borde degradado y sombra).
4. Stacks: cabeceras con barra de acento, pestañas/filtros con estado seleccionado
   iluminado, progreso redondeado y botones principales en degradado.
5. Perfil: héroe con capas de luz y halo en el avatar, métricas en color de acento,
   barras de reputación redondeadas; editores con barra inferior de vidrio.
6. Fondos: orbes suaves en todos los estilos y desenfoque real en Aurora (Android 12+).

La identidad lavanda/carbón sigue siendo el valor por defecto y toda la paleta
se deriva del color de acento elegido en Apariencia.

### Archivos creados

Bajo `app/src/main/java/com/example/teamval/`:
- `ui/theme/Visuals.kt` (tokens de degradado, vidrio, sombras y halo)
- `ui/components/GlassSurface.kt` (`glassSurface`, `HairlineDivider`, `AccentBar`)
- `ui/components/PremiumButton.kt` (`PrimaryButton`)
- `ui/components/TeamValChip.kt` (`AccentFilterChip`)

### Archivos actualizados

- `PROJECT_SPEC.md`, `ROADMAP.md` y este registro.
- `ui/theme/Theme.kt`, `Type.kt`, `Shape.kt`: tokens de vidrio en el tema raíz,
  acento más vivo en modo oscuro, espaciado tipográfico y radios más generosos.
- `ui/components/`: `StackCommon.kt`, `SectionIcon.kt`, `ComingSoonBadge.kt`,
  `FeatureCard.kt`, `CustomizationControls.kt`, `StackPostCard.kt`,
  `JoinRequestCard.kt`, `ProfileHero.kt`, `PlayerAvatar.kt`, `ReputationPanel.kt`,
  `AppBackdrop.kt`.
- `navigation/TeamValNavigationBar.kt`: barra de vidrio y píldora de acento.
- `ui/screens/home/HomeScreen.kt`, `ui/screens/stacks/StackExplorerScreen.kt`,
  `StackRequestsScreen.kt`, `CreateStackScreen.kt`, `ui/screens/profile/ProfileScreen.kt`
  y `AppearanceScreen.kt`: cabeceras, chips y CTA en degradado.

### Verificación

- `:app:assembleDebug`: `BUILD SUCCESSFUL`; APK en
  `app/build/outputs/apk/debug/app-debug.apk`.
- `:app:testDebugUnitTest`: 17 pruebas, 0 fallos (incluye el contraste de colores
  personalizados en ambos temas con la nueva escala de acento).
- `:app:lintDebug`: 0 errores y los mismos 4 avisos de versiones de dependencias.
- `:app:connectedDebugAndroidTest`: 7 pruebas, 0 fallos en Pixel 6 / Android 17:
  perfil, apariencia (incluida la comprobación de luminancia del fondo en tema
  claro y oscuro tras recrear la Activity) y los flujos de Stacks de la etapa 2.
- Comprobación manual en emulador mediante `uiautomator dump`: Inicio, Perfil y
  Stacks renderizan con los nuevos estilos y siguen siendo navegables.
- Sin cambios de textos, `testTag`, arquitectura ni reglas de negocio.

Comandos reproducibles desde PowerShell (pruebas de interfaz con emulador arrancado):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

## Registro de entrega de la etapa 4 y 4.1

### Qué se entrega

**Etapa 4 (básica):**

- Crear equipo (nombre, apodo, región, lema, descripción; creador = capitán) y
  explorador con detalle de equipo; «Simular propuesta» entrante.
- Proponer scrim, estados Propuesta/Confirmada/Completada/Cancelada con
  transiciones válidas y registro de marcador (0–13 por equipo).
- Subgrafo real de navegación `scrims/home` con segmentos Equipos|Scrims; datos
  ficticios en memoria (5 rivales: Eclipse, Aurora, Volt, Neon y Titan).

**Etapa 4.1 (ampliación):**

- `TeamIdentity` (logo, colores, degradado, banner, fondo, marco, insignias,
  lema, descripción, distribución y presentación de integrantes) con borrador en
  `TeamsViewModel`, Guardar/Cancelar/Restablecer y vista previa en vivo.
- Editor de logo Opción A: símbolos y formas dibujados con Canvas, colores,
  degradado, iniciales, fondo, marco, escala; Opción B: Photo Picker
  (`PickVisualMedia`, sin permisos), validación JPG/PNG/WebP ≤10 MB y ≥512 px
  recomendado, recorte rectangular a cuadrado con desplazamiento/zoom
  (`ImageCropView` + `ImageCropMath` puro y testeable) y previsualizaciones
  circular y cuadrada.
- `TeamLogoStorage`: copia optimizada en `filesDir/team_logos` como WebP con
  lado máximo 1024 px conservando la proporción original (el recorte se aplica
  al renderizar).
- Reputación entre equipos: `RatingRepository` con antiabusos (solo tras scrim
  Completada, una valoración por enfrentamiento, capitán, sin autodenuncia, sin
  duplicados, política de comentarios de 10–500 sin enlaces ni términos
  bloqueados, denuncia de comentarios), cinco categorías de 1–5 estrellas
  (Respeto, Seriedad, Puntualidad, Comunicación, Deportividad), tarjeta de
  reputación con media general, promedios por categoría, comentarios recientes,
  insignias e historial de scrims.
- Pantalla de valoración (`scrims/rate/{scrimId}`) con estados de enviada,
  bloqueada y formulario; integración con `ScrimDetail` (botón «Valorar»,
  estado «ya valoraste») y con `TeamDetail` (tarjeta de reputación e
  insignias de identidad).
- Moderación: solo validación local de imagen y de comentario; sin moderación
  automática real (se documenta como pendiente de backend).

### Archivos creados

- `domain/model/TeamIdentity.kt`, `domain/model/RatingModels.kt`,
  `domain/model/ImageFileRules.kt`, `domain/repository/RatingRepository.kt`
- `data/fake/DemoRatingData.kt`, `data/repository/InMemoryRatingRepository.kt`,
  `data/storage/TeamLogoStorage.kt`
- `ui/components/TeamLogoView.kt`, `StarRating.kt`, `ImageCropView.kt`,
  `LogoEditorControls.kt`, `TeamIdentityPreview.kt`, `TeamReputationCard.kt`,
  `IdentityLabels.kt`, `TeamDisplay.kt`
- `ui/screens/teams/LogoEditorScreen.kt`, `ImageCropScreen.kt`,
  `TeamProfileEditorScreen.kt`
- `ui/screens/ratings/RateScrimScreen.kt`, `RateScrimViewModel.kt`
- `res/values/strings_identity.xml`, `res/values/strings_ratings.xml`,
  `res/drawable/ic_star.xml`, `res/drawable/ic_star_outline.xml`
- Pruebas: `test/…/domain/model/Stage41RulesTest.kt`,
  `test/…/data/repository/InMemoryRatingRepositoryTest.kt`,
  `androidTest/…/Stage41FlowTest.kt`, `androidTest/…/TeamLogoStorageTest.kt`

### Archivos actualizados

- `TeamValApp.kt` (repositorio compartido, `RateScrimViewModel`, snackbar
  etiquetado para pruebas), `navigation/TeamValNavHost.kt` (rutas
  `scrims/team/identity`, `…/logo`, `…/logo/crop` y `scrims/rate/{scrimId}`,
  antes de `scrims/team/{teamId}`), `ui/screens/teams/TeamsViewModel.kt`
  (borrador de identidad, `saveLogo`/`revertLogo`/`saveIdentity`),
  `TeamDetailScreen.kt`, `TeamsHubScreen.kt` (tarjeta de rival etiquetada),
  `ui/screens/scrims/ScrimDetailScreen.kt` (acción de valorar),
  `data/fake/DemoTeamData.kt` (scrims históricos e identidad de rivales),
  `res/values/strings_teams.xml`.

### Verificación

- `:app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
  :app:lintDebug` → `BUILD SUCCESSFUL`.
- Pruebas unitarias: **55 pruebas, 0 fallos** (32 de las etapas 1–4 +
  `Stage41RulesTest` con matemáticas de recorte, normas de comentario, reglas
  de valoración y reglas de imagen + 8 de `InMemoryRatingRepositoryTest` con
  antiabusos y el ejemplo exacto de Eclipse Esports: 4,8/5 sobre 10
  valoraciones).
- `:app:lintDebug`: 0 errores; solo los mismos 4 avisos preexistentes de
  versiones de dependencias.
- Pruebas de interfaz (`connectedDebugAndroidTest`, 19 pruebas): **16 en verde**
  (incluidas las 3 de `TeamLogoStorageTest` con proporción 2:1, retrato y
  ausencia de escalado, y `rivalProfileShowsReputationExample` con
  «4,8/5 estrellas», «10 valoraciones», «Respeto · 4,9/5», «Deportividad ·
  5,0/5» y «13 scrims registradas»). **3 pendientes** de reejecución
  (`rateCompletedScrimAndPreventDuplicates`, `ratingFormEnforcesCommunityCommentRules`,
  `identityCustomizationSavesResetsAndCancels`): se identificó que el aviso
  (snackbar) se superpone a las acciones inferiores y bloquea sus toques; se
  etiquetó el snackbar y las pruebas esperan a que desaparezca, pero una
  espera superó los 8 s y queda por diagnosticar.
- **Pendiente de entorno:** el emulador no puede arrancar porque el equipo
  quedó con ~1 GB de RAM libre (el AVD pide 4 GB y WHPX devuelve acceso
  denegado); el usuario decidió continuar la verificación en dispositivo más
  adelante. Ninguna prueba regresó respecto de la etapa 4 (11/11 en verde en la
  última ejecución completa).

Comandos reproducibles desde PowerShell (pruebas de interfaz con emulador
arrancado):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

**Punto de parada histórico, previo a la etapa 5:** etapas 1, 2, 3, 3.5, 4 (básica) y 4.1 entregadas.
Queda pendiente de reejecución en dispositivo la verificación final de la etapa
4.1 (3 pruebas de interfaz) cuando el emulador vuelva a estar disponible. La
próxima etapa es la 5 (Tienda, monedas, recompensas e inventario); esperar una
nueva petición del usuario para iniciarla (recibida posteriormente).

## Registro de recuperación y entrega de la etapa 5

### Auditoría del punto de interrupción — 25/10/2026

Se leyeron `PROJECT_SPEC.md`, este roadmap, todos los archivos nuevos de la etapa
5 y su integración con navegación, perfil, apariencia, identidad de equipo y build.
La especificación declaraba la etapa «verificada» sin un registro que lo respaldara.
El informe existente de `Stage5FlowTest` registraba **6 pruebas: 3 correctas y
3 fallidas** (categorías, monedero y detección de saldo insuficiente).

**Ya implementado:** catálogo de 29 artículos/11 categorías, bono de 500 TC,
compras y paquetes con transacciones Room, inventario, monedero, misiones de demo
y misiones bloqueadas por verificación, cosmética de perfil y apariencia,
pantallas y rutas. La compilación de recuperación y las pruebas unitarias
existentes terminaron correctamente antes de aplicar las correcciones.

**Incompleto o incorrecto al retomar:** validación de propiedad al equipar,
validación transaccional del progreso de misiones demo, rollback del almacenamiento
en memoria, aplicación directa de cosméticos de equipo que omitía Guardar/Cancelar,
validación de capitán en el editor, distribución de botones en pantallas estrechas,
acceso a Tienda aún anunciado como próximo en Inicio y pruebas/documentación final.

### Correcciones sobre los archivos existentes

- Equipado resuelto desde el catálogo y condicionado a la propiedad; operaciones
  serializadas para evitar perder cambios simultáneos y errores de guardado visibles.
- Recompensas demo comprobadas contra el progreso persistido dentro de la misma
  transacción del abono; reclamo único incluso entre repositorios concurrentes.
- Almacenamiento en memoria con commit de snapshot y rollback ante excepciones.
- Cosméticos de equipo abren el editor existente; solo se aplican al Guardar,
  Cancelar conserva la identidad anterior y los guardados exigen capitán.
- Botones de tienda y productos con distribución adaptable; Inicio anuncia la
  tienda disponible. Selectores de pruebas corregidos para listas perezosas y
  textos parciales, sin quitar comprobaciones de saldo ni de estado.
- Fondos y marcos de equipo se reflejan en la ficha guardada y los marcos de
  logo rodean el logo real, compartiendo representación con la vista previa.
- Pruebas añadidas de Room real: reapertura, bono único, conservación de compras y
  recompensas, concurrencia y rollback; flujo de equipo Guardar/Cancelar,
  bloqueo premium y variantes no adquiridas, recreación con cosmético equipado.

### Inventario de archivos inspeccionados de la etapa 5

Rutas relativas a `app/src/main/java/com/example/teamval/`:

- `domain/model/EconomyModels.kt`, `CosmeticBindings.kt`;
  `domain/repository/EconomyRepository.kt`.
- `data/fake/DemoShopCatalog.kt`, `DemoRewardData.kt`;
  `data/local/EconomyDatabase.kt`, `EconomyStorage.kt`;
  `data/repository/LocalEconomyRepository.kt`, `InMemoryEconomyStorage.kt`.
- `ui/screens/shop/EconomyViewModel.kt`, `ShopScreen.kt`, `ShopComponents.kt`,
  `ShopDialogs.kt`, `ProductCard.kt`;
  `ui/screens/inventory/InventoryScreen.kt`, `ui/screens/wallet/WalletScreen.kt`;
  `ui/screens/rewards/RewardsScreen.kt`, `MissionCard.kt`.
- `ui/components/EconomyLabels.kt`, `ProfileDecorations.kt`.

Recursos y pruebas:

- `app/src/main/res/values/strings_shop.xml` y
  `app/src/main/res/drawable/ic_coin.xml`, `ic_gift.xml`, `ic_inventory.xml`.
- `app/src/test/java/com/example/teamval/data/repository/LocalEconomyRepositoryTest.kt`,
  `data/fake/DemoShopCatalogTest.kt`, `domain/model/CosmeticBindingsTest.kt`.
- `app/src/androidTest/java/com/example/teamval/Stage5FlowTest.kt`.
- Nuevo durante la recuperación:
  `app/src/androidTest/java/com/example/teamval/EconomyStorageTest.kt` y
  `EconomyViewModelTest.kt` (propiedad, vínculo canónico, equipado concurrente,
  temas y permisos de capitán).

Integraciones existentes revisadas: `TeamValApp.kt`, `TeamValNavHost.kt`,
`Personalization.kt`, `TeamIdentity.kt`, `LocalPreferencesRepository.kt`,
`HomeScreen.kt`, `ProfileScreen.kt`, `EditProfileScreen.kt`, `TeamsViewModel.kt`,
`TeamProfileEditorScreen.kt`, `ProfileHero.kt`, `PlayerAvatar.kt`,
`TeamIdentityPreview.kt`, `TeamDisplay.kt`, `TeamDetailScreen.kt`,
`ProfileLabels.kt`, `IdentityLabels.kt`,
`CustomizationControls.kt`, recursos de texto y configuración Gradle/KSP/Room.

### Verificación final

Entorno: JDK de Android Studio, Pixel 6 / Android 17 (API 37),
`emulator-5554`, Android Test Orchestrator con `clearPackageData=true`.

- **APK debug y APK de pruebas:** compilados correctamente. Último comando de
  compilación/Lint: `BUILD SUCCESSFUL`.
- **Unitarias:** 96 pruebas, 0 fallos, 0 errores (55 heredadas + 41 de economía,
  catálogo y vínculos). Incluyen precios canónicos, saldo, duplicados, paquetes,
  recompensas únicas, concurrencia, rollback y persistencia de preferencias.
- **Lint:** 0 errores, 8 avisos de versiones de dependencias (Compose BOM,
  Navigation, DataStore, Room compiler/ktx/runtime, Orchestrator y plugin Compose).
- **Instrumentadas:** 33 casos verificados en total, mediante suite completa y
  reejecución focalizada. La última suite completa tuvo **32 correctas y 1 fallo**:
  `test09_profileRewardAndBundleKeepExactBalanceAndOwnership` pulsaba Atrás antes
  de que Navigation terminara de restaurar la pestaña. Se corrigió únicamente
  la sincronización de esa prueba (`assertExists` + `runOnIdle`); la reejecución
  del caso dio **1/1 correcto, BUILD SUCCESSFUL**. No se presenta la suite
  completa como una ejecución única de 33/33.
- Desglose instrumentado: 9 flujos de etapa 5, 2 pruebas de Room real, 3 de
  ViewModels/equipado/permisos, 3 de Stacks, 3 de personalización, 4 de equipos y
  scrims, 5 de etapa 4.1, 3 de almacenamiento de logos y 1 heredada de contexto.
- El flujo final comprueba perfil real → recompensa de 100 TC → saldo 600 →
  paquete de 500 TC → saldo 100 y sus tres cosméticos adquiridos. También se
  verificaron saldo insuficiente, premium bloqueado, variantes sin comprar,
  equipado/desequipado, recreación de Activity y Guardar/Cancelar en equipo.

Comandos ejecutados (PowerShell):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug :app:connectedDebugAndroidTest --console=plain
# Tras corregir solo la sincronización de test09:
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.example.teamval.Stage5FlowTest#test09_profileRewardAndBundleKeepExactBalanceAndOwnership' --console=plain
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --console=plain
```

Artefactos e informes locales (generados bajo `app/build/`):

- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Unitarias: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Lint: `app/build/reports/lint-results-debug.html`.
- Suite completa conservada: `app/build/reports/stage5-recovery/full-suite.xml`.
- Caso corregido: `app/build/reports/stage5-recovery/rerun-test09.xml`.
- `app/build/reports/androidTests/connected/debug/index.html` corresponde a la
  última reejecución focalizada, no a los 33 casos de la suite completa.

### Cierre de las tres pruebas pendientes de la etapa 4.1

Se conservan los nombres y el diagnóstico histórico del registro de etapa 4.1.
Las tres pasaron en ambas ejecuciones completas de esta recuperación. En la
última suite completa (informe `full-suite.xml`, 25/10/2026):

- [x] `rateCompletedScrimAndPreventDuplicates` — correcta (24,683 s).
- [x] `ratingFormEnforcesCommunityCommentRules` — correcta (25,560 s).
- [x] `identityCustomizationSavesResetsAndCancels` — correcta (32,116 s).

Ya no quedan pendientes de reejecución; no se alteraron ni se desactivaron estas
pruebas para obtener el resultado.

### Punto de parada histórico de etapa 5 y límites

**Etapa 5 completada y verificada. No quedan funcionalidades pendientes dentro
del alcance del prototipo. Se detiene el desarrollo aquí.**

- Las misiones de sesiones/valoraciones verificadas y las compras premium siguen
  bloqueadas por diseño. Sin VALORANT Tracker, backend, Riot API ni pagos reales.
- Inventario, monedas, misiones reclamadas y cosméticos personales persisten;
  equipos y cosmética aplicada al equipo conservan la limitación previa de
  sesión en memoria. Comprar un cosmético de equipo sí persiste su propiedad.
- La validación se realizó en Pixel 6/API 37. No se realizó una auditoría manual
  de TalkBack ni una matriz adicional de dispositivos/tamaños de texto.

## Etapa 5.5 — Bloque A refinado: identidad y apariencia

**Estado: completada; compilación, Lint y pruebas unitarias/instrumentadas
verificadas el 25–26/10/2026.**

### Revisión inicial

- Lectura completa de especificación/roadmap e inspección de tema, tipografía,
  preferencias, navegación, componentes compartidos, perfiles y economía.
- Git limpio al comenzar, commit `346846d` (etapa 5). No había fuentes ni cambios
  parciales de la etapa 5.5: se continuó sobre los componentes existentes.

### Entrega

- Marca **TeamVal / TeamVal Coins** unificada en recursos, código y documentación;
  sin cambiar identificadores persistidos ni datos económicos.
- Paleta nueva rojo/carbón azul/marfil; vidrio, sombras y degradados conservados.
- Texto secundario más legible, chips/navegación/píldoras con contraste ajustado,
  fondo Pastel de perfil oscurecido para que el texto blanco sea legible.
- Outfit + Exo 2 locales, con pesos reales y licencias OFL incluidas. Selector
  de fuentes anteriores conservado. Tipografía global desde MaterialTheme.
- Perfil → Apariencia más explícito; cinco presets con muestras de color y
  opción Personalizado, vista previa, guardado/descarte y restauración.
- Preferencias previas conservadas con paleta Legacy por lectura compatible;
  configuraciones nuevas usan VALORANT clásico y Outfit + Exo 2.

### Archivos

Rutas Kotlin bajo `app/src/main/java/com/example/teamval/`:

- Sistema visual: `ui/theme/Color.kt`, `Theme.kt`, `Type.kt`, `Visuals.kt`.
- Apariencia: `domain/model/Personalization.kt`,
  `data/repository/LocalPreferencesRepository.kt`,
  `ui/screens/profile/AppearanceScreen.kt`, `ProfileScreen.kt`,
  `ui/components/ProfileLabels.kt`.
- Componentes: `navigation/TeamValNavigationBar.kt`,
  `ui/components/TeamValChip.kt`, `ProfileHero.kt`, `ProfileDecorations.kt`,
  `TeamLogoView.kt`, `ui/screens/shop/ShopComponents.kt`, `ProductCard.kt`.
- Solo unificación textual adicional: `domain/model/EconomyModels.kt`,
  `TeamIdentity.kt`, `data/fake/DemoShopCatalog.kt`,
  `ui/screens/home/HomeScreen.kt`, `ui/screens/wallet/WalletScreen.kt`,
  `ui/screens/teams/LogoEditorScreen.kt`, `ui/components/LogoEditorControls.kt`.
- Recursos: `res/values/strings_appearance.xml` (nuevo), `colors.xml`, `themes.xml`,
  `strings.xml`, `strings_profile.xml`, `strings_shop.xml`, `strings_identity.xml`,
  `strings_ratings.xml` y `res/drawable/ic_coin.xml`.
- Nuevos: `res/font/outfit_{regular,medium,semibold,bold}.ttf`,
  `res/font/exo2_{regular,medium,semibold,bold}.ttf`,
  `assets/licenses/outfit_ofl.txt`, `exo2_ofl.txt`, `README.md`.
- Pruebas: `LocalPreferencesRepositoryTest.kt`, `PersonalizationThemeTest.kt`,
  `PersonalizationFlowTest.kt`, `Stage5FlowTest.kt` y nueva
  `androidTest/…/AppearanceIdentityTest.kt`.
- `PROJECT_SPEC.md` y este roadmap.

### Verificación

Ejecutada en Pixel 6 / Android 17 (API 37), sin modificar ni desactivar ninguna
prueba existente. Resultados reales:

- `:app:assembleDebug` → `BUILD SUCCESSFUL`; APK de depuración generado en
  `app/build/outputs/apk/debug/`.
- `:app:testDebugUnitTest` → **100 pruebas, 0 fallos, 0 omitidas (100 %)**,
  incluidas las nuevas de contraste/presets/tipografía y de persistencia
  compatible (`LocalPreferencesRepositoryTest` 5/5).
  Informe: `app/build/reports/tests/testDebugUnitTest/index.html`.
- `:app:lintDebug` → **0 errores y 8 avisos** de versiones disponibles de
  dependencias ya presentes. Informe: `app/build/reports/lint-results-debug.html`.
- `:app:connectedDebugAndroidTest` (suite completa) → **35 pruebas, 34 correctas
  y 1 fallo**: `Stage41FlowTest#rateCompletedScrimAndPreventDuplicates`
  (`assertExists` del tag `rating_sent`), prueba de la etapa 4.1 ajena a este
  bloque. Las nuevas `AppearanceIdentityTest` pasaron 2/2 y el resto de suites
  (Personalization, Stage5, Economy, Stacks, Team…) también.
- Reejecución aislada autorizada de esa prueba:
  `.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.example.teamval.Stage41FlowTest#rateCompletedScrimAndPreventDuplicates'`
  → **1 prueba, 0 fallos** (24,6 s, `BUILD SUCCESSFUL`). El fallo anterior fue
  intermitente de temporización; no se tocó el código ni ninguna prueba.
  Informe: `app/build/reports/androidTests/connected/debug/`.

Verificación funcional en el emulador: la app arranca con la nueva paleta
rojo/carbón/marfil y la tipografía Outfit + Exo 2 aplicadas de forma global.

### Pendientes documentales de este bloque

- Commit `90e3ef9` «TeamVal v0.5.5 - Etapa 5.5 Bloque A completado» (50 archivos)
  autorizado, enviado a `origin/main` y con árbol limpio.
- Auditoría manual de TalkBack y matriz adicional de dispositivos/tamaños de
  texto, como en etapas anteriores.

### Próximos bloques (no iniciados)

Selectores de rango, Premier, rango de perfiles y animaciones se mantienen fuera
de esta entrega. Recursos pendientes: logo oficial y emblemas propios aprobados,
integrables en BrandHeader/SectionIcon/drawables existentes. No se inicia otro bloque.

## Etapa 5.5 — Bloque B: Buscar equipo

**Estado: completada; compilación, Lint y pruebas unitarias/instrumentadas
verificadas el 26/10/2026.**

### Revisión inicial

- Lectura de `PROJECT_SPEC.md`, de este roadmap y del código de stacks
  existente: `StackModels.kt`, `StacksViewModel.kt`, `StackExplorerScreen.kt`,
  `StackPostCard.kt`, `StackFiltersPanel.kt`, `StackCommon.kt`, `StackLabels.kt`,
  `CreateStackScreen.kt`, `DemoStackData.kt`, `strings_stacks.xml` y las pruebas
  de stacks. Git limpio al comenzar sobre el commit `90e3ef9` (bloque A enviado).
- Se comprobó cómo se representaban los grupos hoy: solo `capacity` (2..5) y
  `memberIds`, sin categoría, sin cupos explícitos en la tarjeta y sin distinción
  entre «grupo actual» y «jugadores que se buscan».

### Clasificación de grupos y casos ambiguos

El tipo de grupo se deriva del tamaño objetivo `capacity` (ya validado entre 2 y
5):

- **Dúo = 2**, **Trío = 3**, **Stack = 4 o 5**. Caso ambiguo resuelto: un grupo
  de 4 no es dúo ni trío, se agrupa como stack y la tarjeta indica «Este grupo
  cubre 4 de 5 jugadores y queda 1 fuera para completar el equipo».
- Tres cifras sin solape en cada tarjeta: **integrantes actuales**
  (`memberIds.size`), **cupos disponibles** (`capacity - memberIds.size`) y
  **jugadores que faltan para 5** (`5 - capacity`), de modo que siempre se cumple
  `integrantes + cupos + faltantes = 5`.
- Ejemplo: un dúo con 1 integrante ofrece 1 cupo y aún hacen falta 3 jugadores
  fuera del dúo para tener 5; un stack de 5 con 3 integrantes ofrece 2 cupos y
  no falta nadie fuera; un stack de 4 completo sigue sin cubrir cinco.

### Entrega

- Sección renombrada a **Buscar equipo** en barra inferior, cabecera, tarjeta de
  inicio y CTA, conservando ruta, tags y etiquetas internas.
- Tres pestañas horizontales **Dúo / Trío / Stack** en `stickyHeader` con fondo
  sólido y filete: siempre visibles sobre las publicaciones y sin abrir el panel
  de filtros. Sin pestaña activa se ve todo (comportamiento previo); pulsar la
  activa vuelve a la vista completa y muestra una pista breve.
- `StackSearch` combina categoría + filtros existentes (rango, región,
  modalidad) + «Mis publicaciones» + publicaciones ajenas. `category` vive en
  `StacksLocalState`, fuera de `StackFilters`, para no alterar `isActive` ni el
  panel; sobrevive a la recreación de la actividad.
- Tarjeta con badge de categoría, «Integrantes: X de Y», cinco plazas visuales
  (ocupadas / libres / fuera del grupo) con leyenda accesible y cupos
  disponibles. Rango, región y modo ya existentes se conservan. La aportación al
  equipo de 5 va en un pie propio y acotado al grupo (ver «Corrección de
  textos»).
- Crear publicación: «N jugadores · Dúo/Trío/Stack» en el desplegable de tamaño
  y resumen en vivo de plazas, cupos por cubrir y aportación al equipo de 5.
- Estados vacíos diferenciados (filtros / categoría / propias / genérico) con la
  acción «Ver todos los grupos» cuando la categoría vacía la lista. Al crear un
  post se activa su pestaña y «Mis publicaciones» para que sea visible.
- Renombrado también `home_action` a «Encontrar compañeros» para evitar
  etiquetas duplicadas con el nuevo nombre de la sección.

### Archivos

Rutas Kotlin bajo `app/src/main/java/com/example/teamval/`:

- Dominio: `domain/model/StackModels.kt` (`StackCategory`, `StackPost` con
  `currentMembers`/`availableSlots`/`category`/`playersMissingForFullTeam` y
  `StackSearch`).
- UI: `ui/screens/stacks/StackExplorerScreen.kt`, `StacksViewModel.kt`,
  `CreateStackScreen.kt`, `ui/components/StackPostCard.kt`, `StackLabels.kt` y
  `navigation/TeamValNavHost.kt` (paso de `onCategoryChange`).
- Recursos: `res/values/strings.xml` (nombre de sección y CTA),
  `res/values/strings_stacks.xml` (pestañas, integrantes, cupos, faltantes,
  estados vacíos y resumen de creación).
- Pruebas: nueva `test/…/domain/model/StackSearchTest.kt` (3 pruebas),
  `StacksFlowTest.kt` (nueva `categoryTabsSwitchGroupsWithoutOpeningFilters` y
  adaptación de los textos renombrados) y `PersonalizationFlowTest.kt` (navegación
  por tag `nav_stacks` en lugar del texto renombrado).
- `PROJECT_SPEC.md` (sección 13) y este roadmap.

### Corrección de textos (punto 5 del encargo)

La redacción original «Faltan N jugadores para un equipo de 5» aparecía en la
misma fila que «1 cupo disponible» y podía leerse como que la publicación
buscaba N jugadores. Se acotó siempre al grupo y se separó visualmente:

- `stack_missing_for_five` y `stack_covers_five` → `stack_team_coverage`
  (plural según los que quedan fuera) y `stack_team_complete`: «Este grupo
  cubre 2 de 5 jugadores y quedan 3 fuera para completar el equipo» / «Este
  grupo ya cubre los 5 jugadores del equipo».
- La frase baja a su propio pie (`bodySmall`, gris) fuera de la fila de cupos,
  de modo que integrantes y cupos quedan como únicos datos de la publicación.
- Leyenda accesible de las cinco plazas: «Plazas de un equipo de 5: a ocupadas
  por integrantes, b libres por cupos y c fuera de este grupo».
- `create_capacity_summary` pasa a plural y queda en dos frases con ámbito
  explícito: «Tú ocupas 1 de N plazas; quedan C por cubrir. Este grupo cubre N
  de 5 jugadores y quedan M fuera para completar el equipo».
- `StacksFlowTest` actualizado con los textos nuevos y acotados a cada
  publicación por su `testTag` (`post-demo-duo`, `post-demo-night`), ya que dos
  publicaciones de la misma categoría comparten tamaño y por tanto la misma
  frase.

### Verificación

Ejecutada el 26/10/2026 sobre Windows con `.\gradlew.bat`
(`JAVA_HOME` = JBR de Android Studio), emulador Pixel_6 API 37:

- `.\gradlew.bat :app:assembleDebug` → **BUILD SUCCESSFUL**.
- `.\gradlew.bat :app:testDebugUnitTest` → **BUILD SUCCESSFUL**,
  **103 pruebas, 0 fallos, 0 omitidas** (2.417 s). Informe:
  `app/build/reports/tests/testDebugUnitTest/index.html`.
- `.\gradlew.bat :app:lintDebug` → **BUILD SUCCESSFUL**, **0 errores y 8
  avisos**; los 8 avisos son los preexistentes de dependencias/objetivos
  (ninguno de este bloque). Informe: `app/build/reports/lint-results-debug.html`.
- `.\gradlew.bat :app:connectedDebugAndroidTest` → **BUILD SUCCESSFUL (8 min
  33 s)**, **36 pruebas, 0 fallos, 0 errores, 0 omitidas** en 10 suites:
  `AppearanceIdentityTest` (2), `EconomyStorageTest` (2), `EconomyViewModelTest`
  (3), `ExampleInstrumentedTest` (1), `PersonalizationFlowTest` (3),
  `StacksFlowTest` (4), `Stage41FlowTest` (5), `Stage5FlowTest` (9),
  `TeamFlowTest` (4), `TeamLogoStorageTest` (3). Resultado en
  `app/build/outputs/androidTest-results/connected/debug/`.
- La única anomalía observada durante el bloque fue un fallo intermitente de
  `Stage41FlowTest#ratingFormEnforcesCommunityCommentRules` (snackbar que
  intercepta el toque), ya conocido de etapas anteriores: al repetirlo aislado
  pasó, y en las dos ejecuciones completas definitivas la batería salió 36/36.
- Revisión visual en el emulador (1080×2400): portada → CTA «Encontrar
  compañeros» → **Buscar equipo**; pestañas **Dúo → 2 publicaciones**,
  **Trío → 1 publicación**, **Stack → 2 publicaciones**, **sin pestaña → 5
  publicaciones**; la barra de pestañas queda fija sobre las publicaciones al
  hacer scroll y no abre el panel de filtros; panel de filtros operativo con
  «Ocultar filtros», los tres filtros previos (rango, región, modalidad) y
  «Limpiar filtros», y combinación pestaña + filtro comprobada (Dúo + LATAM Sur
  → 1 publicación); tarjeta de dúo con «Integrantes: 1 de 2», cinco plazas y «1
  cupo disponible» + pie «Este grupo cubre 2 de 5 jugadores y quedan 3 fuera…»;
  tarjeta de stack con «Integrantes: 3 de 5», «2 cupos disponibles» y «Este
  grupo ya cubre los 5 jugadores del equipo»; creación con «2 jugadores · Dúo»
  en el desplegable y el resumen de plazas/cupos en vivo.

## Etapa 5.5 — Mejora de UX: Perfil y Apariencia

**Estado: completada; compilación, Lint y pruebas verificadas el 26/10/2026.**

### Revisión inicial

- Lectura de `PROJECT_SPEC.md`, de este roadmap y de los archivos indicados:
  `ProfileScreen.kt`, `AppearanceScreen.kt`, `Personalization.kt`, `Theme.kt`,
  `Visuals.kt` y el repositorio de preferencias (`PreferencesRepository.kt` y
  `LocalPreferencesRepository.kt`), más `AppBackdrop.kt`,
  `CustomizationControls.kt`, `ProfileViewModel.kt` y las pruebas de apariencia.
- **Causa real del problema de los fondos** (no eran «ajustes» sino dibujos
  equivocados en `AppBackdrop`):
  - `Plain` dibujaba **los mismos orbes de color** que `Aurora`, con alfa 0.12
    (oscuro) y 0.08 (claro): «Liso» no era liso y se veía como una aurora.
  - `Aurora` dibujaba dos círculos de color plano y solo aplicaba `Modifier.blur`
    a su **borde** (API 31+): el interior ya era uniforme, de modo que tras el
    difuminado se parecía a «Liso».
  - `Grid` dibujaba una rejilla con alfa 0.07 (invisible a simple vista) más un
    círculo de resplandor del acento: «Cuadrícula» se leía como una aurora
    oscura.
  - No había miniaturas: la opción era un chip de solo texto, así que no podía
    representar fielmente lo que iba a aparecer.

### 1. Sección Perfil

- Desaparece el acceso directo **«Mi inventario»** de `ProfileScreen` (y su
  parámetro `onOpenInventory` del nav host). El inventario **no se elimina**:
  sigue disponible desde la tienda (`InventoryRoute`), con sus datos, su ruta y
  su funcionalidad intactos; la cadena `profile_inventory` deja de existir porque
  ya no tiene uso.
- El acceso pasa a llamarse **«Apariencia de la aplicación»**
  (`appearance_access_title`) con la descripción **«Colores, tema y fondo de
  TeamVal»** (`appearance_access_hint`).
- **«Editar perfil»** permanece independiente y operativo.

### 2. Pantalla Apariencia: una sola sección de color y estilo

- Se fusionan «Tu estilo, de un vistazo» y «Color de acento» (y se retira la
  sección de vista previa con su botón «Aplicar apariencia», que duplicaba el
  «Guardar» de la barra inferior) en **una única sección «Colores y estilo»**:
  vista previa, presets existentes, tema de aplicación, chips de acento y campo
  HEX.
- La vista previa es un panel propio con «VISTA PREVIA EN VIVO», el título de
  muestra en el color de acento y la nota tipográfica; además la preferencia sin
  guardar se aplica a toda la aplicación, de modo que el fondo real también se
  previsualiza al instante.
- El botón **«Restablecer valores predeterminados»** se conserva (antes decía
  «Restablecer lavanda y carbón», texto desactualizado desde el bloque A).
- Las preferencias guardadas no se pierden: solo cambia la composición.

### 3. Fondos: Liso, Aurora y Estrellas

- `AppBackground` queda **`Plain`, `Aurora`, `Stars`**; se elimina la opción
  **Cuadrícula**. Migración segura y de solo lectura en
  `LocalPreferencesRepository.backgroundOrDefault`: el valor guardado `Grid` se
  lee como `Stars` (sigue siendo un fondo decorado válido) y cualquier valor
  desconocido vuelve a `Plain`; no se escribe ni se resetea nada guardado.
- `AppBackgroundView` es la **única implementación** del fondo, compartida por la
  aplicación (`AppBackdrop`) y por las miniaturas de selección, así que una
  miniatura no puede mentir:
  - **Liso**: color uniforme del tema, sin orbes, rejillas ni degradados.
  - **Aurora**: cuatro resplandores radiales con degradado suave hasta
    transparente, derivados del acento activo (tono principal, ±45° y
    complementario), visibles en ambos temas y rebajados en claro para no
    perjudicar la lectura. Sin animación, para no costar batería.
  - **Estrellas**: hasta 1500 estrellas pequeñas con distribución estable (semilla
    fija, dibujadas una sola vez) y sutiles diferencias de tamaño y brillo, sobre
    el fondo adaptado al tema (oscuro en oscuro; en claro, estrellas de tinta
    sobre el color claro) para no romper el contraste del texto directo.
- La pantalla de apariencia muestra las tres opciones como **tarjetas con
  miniatura** (108×64 dp), borde y marca de selección, con tags
  `background_<Nombre>` y `background_thumb_<Nombre>`.
- El preset **«Azul / carbón»** y el cosmético «Aqualux» de la tienda pasan su
  fondo de `Grid` a `Stars` (`CosmeticBindings`): es el único punto de la tienda
  tocado, y solo por el renombrado del enum.

### 4. Tipografía

- La interfaz **no ofrece selector de fuentes**: se retiran la sección, sus chips
  y sus cadenas (`appearance_font`, `font_modern`, `font_editorial`,
  `font_mono`, `font_gaming`, `font_sample`).
- `teamValTypography` define el estilo único **«Moderno»: Outfit para cuerpo y
  Exo 2 para títulos** (`AppFont.Modern` y el valor histórico `AppFont.Gaming`
  comparten familia) y conserva la correspondencia heredada de `Editorial`/`Mono`
  para no romper preferencias antiguas.
- El valor por defecto de `AppAppearance.font` pasa a `AppFont.Modern`; el enum,
  los TTF y las licencias de `assets/licenses/` **no se borran** (compatibilidad y
  requisitos de la SIL OFL, cuya nota se muestra además en la vista previa).
- Decisión: **no se reescribe el valor guardado de fuente**. Normalizarlo en la
  lectura rompería la comparación de `VisualThemePresets.matches` y haría que el
  cosmético «Aqualux» de la tienda apareciera como no equipado; además la interfaz
  ya no permite elegir otra cosa, así que «solo un estilo» se cumple en pantalla
  sin tocar datos ni tienda.

### 5. Guardado y confirmación

- El guardado sigue siendo persistente (DataStore). El ViewModel distingue
  ahora entre mensaje de éxito y de error (`messageIsError`): al guardar la
  apariencia se muestra **«¡Apariencia actualizada!»** (`appearance_updated`) y
  al guardar el perfil **«Cambios guardados en este dispositivo.»**.
- La confirmación es una tira con borde y color de acento que entra con una
  animación breve (`AnimatedVisibility`) y **solo aparece tras guardar
  correctamente**; cualquier modificación posterior la retira. Los errores de
  validación o de guardado se pintan en color de error para no confundirse con
  éxito.

### Archivos

Rutas bajo `app/src/main/java/com/example/teamval/` salvo donde se indica:

- Dominio: `domain/model/Personalization.kt` (`AppBackground.Stars`, fondo del
  preset BlueCharcoal, documentación de los enums) y
  `domain/model/CosmeticBindings.kt`.
- Datos: `data/repository/LocalPreferencesRepository.kt` (migración `Grid`→`Stars`).
- Tema: `ui/theme/Type.kt` (estilo Moderno = Outfit + Exo 2) y `ui/theme/Color.kt`
  (`hueShift`, tonos vecinos de la aurora).
- UI: `ui/components/AppBackdrop.kt` (reescrito: `AppBackgroundView`, aurora y
  campo de estrellas), `ui/components/CustomizationControls.kt` (tira de
  confirmación animada), `ui/components/ProfileLabels.kt`,
  `ui/screens/profile/ProfileScreen.kt`, `AppearanceScreen.kt`,
  `EditProfileScreen.kt`, `ProfileViewModel.kt` y `navigation/TeamValNavHost.kt`.
- Recursos: `res/values/strings_profile.xml` (acceso, confirmación, colores y
  estilo, Estrellas; retiro de cadenas sin uso), `res/values/strings_appearance.xml`
  (descripción del acceso y pista de presets) y `res/values/strings_shop.xml`.
- Pruebas: `LocalPreferencesRepositoryTest` (migración y estilo por defecto),
  `PersonalizationThemeTest` (tipografía Moderno y nueva de `hueShift`),
  `PersonalizationFlowTest` (acceso renombrado, fondo por tag, confirmación nueva
  y ausencia del selector de fuentes), `AppearanceIdentityTest` (confirmación
  nueva) y la nueva `AppearanceBackgroundsTest` (2 pruebas sobre píxeles).
- `PROJECT_SPEC.md` (sección 14) y este roadmap.

### Verificación

Ejecutada el 26/10/2026 con `.\gradlew.bat` sobre el emulador Pixel_6 (API 37):

- `:app:assembleDebug` → **BUILD SUCCESSFUL** (compila sin errores).
- `:app:testDebugUnitTest` → **104 pruebas, 0 fallos, 0 omitidas** en 14 suites
  (incluye la nueva de `hueShift` y las de migración `Grid`→`Stars` y estilo
  por defecto «Moderno»).
- `:app:lintDebug` → **0 errores, 8 avisos** (los 8 son preexistentes de
  dependencias: 7 `GradleDependency` y 1 `NewerVersionAvailable`).
- `:app:connectedDebugAndroidTest` → **BUILD SUCCESSFUL (6 min 57 s)**,
  **38 pruebas, 0 fallos, 0 errores, 0 omitidas** en 11 suites:
  `AppearanceBackgroundsTest` (2, nuevas), `AppearanceIdentityTest` (2),
  `EconomyStorageTest` (2), `EconomyViewModelTest` (3), `ExampleInstrumentedTest`
  (1), `PersonalizationFlowTest` (3), `StacksFlowTest` (4), `Stage41FlowTest` (5),
  `Stage5FlowTest` (9), `TeamFlowTest` (4), `TeamLogoStorageTest` (3).

#### Comprobación de que los tres fondos se distinguen de verdad

- **Automática**: `AppearanceBackgroundsTest` recorta el centro de cada miniatura
  y mide píxeles: Liso con varianza < 0,0005 (color plano), Aurora con varianza
  claramente mayor y sin destellos puntuales (degradado suave), Estrellas con
  más de 5× los destellos de Liso/Aurora (puntos pequeños) y diferencia media
  entre cada pareja de miniaturas; además verifica que la vista previa no escribe
  hasta «Guardar» y que la selección persiste tras recrear la actividad.
- **Manual, sobre capturas reales del emulador** (mismo código que las
  miniaturas):
  - Liso: luminancia de fondo **0,0092 constante** (mín = máx) en toda la zona
    muestreada: sin orbes ni degradados.
  - Aurora: fondo **0,06–0,15** con transiciones suaves (p. ej. tira superior
    0,057 → 0,087) y ningún salto brusco: se ve como degradado luminoso.
  - Estrellas: base **0,009** con puntitos de hasta **0,93**: campo granulado
    netamente distinto de la aurora.
- **Persistencia**: tras «Guardar», selección y preferencia sobreviven a
  recrear la actividad (prueba automatizada) y a reiniciar la aplicación.
- **Lectura del texto sobre el fondo**: el texto directo (`#C4CDD5`,
  luminancia 0,60) queda en **≥11:1** sobre Liso y Estrellas y en torno a
  **5:1** sobre la aurora, bajando hasta ≈3,3:1 en la zona más brillante del
  resplandor. La aurora se mantiene intencionadamente «luminosa» por encargo;
  si se prefiere más suave, basta con rebajar `strength` en
  `AppBackdrop.drawAurora`.
- **Revisión visual en el emulador**: Perfil sin «Mi inventario» y con el acceso
  «Apariencia de la aplicación» + «Colores, tema y fondo de TeamVal»; pantalla de
  apariencia con la sección «Colores y estilo», «Fondo de la aplicación» con las
  tres miniaturas fieles y la tira «¡Apariencia actualizada!» tras guardar;
  Inicio con los tres fondos comprobados uno a uno.
- Nota del entorno: el emulador pierde la conexión `adb` con cierta
  frecuencia (se reinició durante la ejecución) y muestra el diálogo del sistema
  «Android App Compatibility / 16 KB» al arrancar; no afecta a la aplicación.

## Etapa 5.6 — Correcciones visuales: tema, Estrellas y pestañas

### Revisión inicial

Investigación previa a tocar código, sobre la app instalada en el emulador
(Pixel_6, API 37) y sobre el árbol de fuentes:

- **Tema**: `AppAppearance.mode` llega a `TeamValTheme`, que decide el modo con
  `when (appearance.mode)` (`Dark` → true, `Light` → false, `System` →
  `isSystemInDarkTheme()`) y construye el esquema con
  `teamValColorScheme(appearance, dark)`. `TeamValApp` aplica la preferencia
  **sin guardar** solo mientras la pestaña actual es `AppearanceRoute`
  (`preview = appearanceDraft.takeIf { ... }`), es decir, la vista previa ya
  era en vivo y «Guardar» escribía en DataStore.
- **Medición del problema**: con el mismo flujo se midieron las luminancias
  medias de la pantalla: Oscuro **56**, Claro **232**, Sistema con el
  dispositivo oscuro **41** y Sistema con el dispositivo claro **235**. El
  cambio sí se aplicaba, pero el selector de tema quedaba **por debajo del
  pliegue** (había que desplazar para verlo) y la entrada y salida de la
  pantalla no reseteaban el borrador: al volver desde otra pestaña podía
  persistir un borrador sin guardar y el mensaje «¡Apariencia actualizada!»
  de una guardada anterior.
- **Estrellas**: `drawStarField` calculaba el número de puntos a partir de la
  retícula con un mínimo forzado de 70 y un tope de 1500, los colocaba con
  `Random` puro (racimos) y usaba el mismo tinte en ambos temas
  (`#1E2B3A` en claro) con opacidad hasta 1,0: en tema claro eran **puntos
  negros** y en general un campo de ruido.
- **Pestañas**: el `stickyHeader` pintaba `Modifier.background(colors.background)`
  (color plano del tema) y tapaba el fondo real: con Aurora o Estrellas
  detrás se veía como un **rectángulo casi negro**. Además eran tres chips
  pequeños y se mostraba el texto permanente «Pulsa la pestaña activa…».
- **Colores fijos**: no hay colores hardcodeados en títulos, subtítulos,
  tarjetas, botones ni navegación inferior. Los únicos valores fijos son
  ilustraciones de marca (tarjeta de perfil, avatares, decoraciones, marcos de
  logo) que llevan texto blanco sobre degradados oscuros propios.

### 1. Selección de tema (punto 1 del encargo)

- El grupo «Tema de aplicación» sube al principio de «Colores y estilo», justo
  después de la vista previa: se ve sin desplazar nada más abrir la pantalla,
  y los presets de abajo ya no pueden cambiar el tema de forma inesperada
  sin que se note arriba.
- `composable(AppearanceRoute)` ahora hace `LaunchedEffect { editAppearance() }`
  al entrar y `DisposableEffect { onDispose { discardAppearance() } }` al
  salir: toda entrada parte de las preferencias guardadas y **toda salida**
  (Volver, pestaña o gesto atrás) descarta lo no guardado, incluido el
  mensaje de confirmación antiguo.
- Sin cambios en `Theme.kt`: Oscuro y Claro siguen derivando del mismo
  `teamValColorScheme` y Sistema sigue a `isSystemInDarkTheme()`.

### 2. Fondo Estrellas (punto 2 del encargo)

- Retícula de 15 dp con desplazamiento aleatorio por celda y descarte del 67 %
  de las celdas: **~530 estrellas** por pantalla completa (antes hasta 1500),
  sin racimos porque nunca caben dos en la misma celda.
- Radio de 0,65 a 1,7 dp (≈1,5 a 4,5 px) y solo el 18 % son «brillantes», con
  halo suave de radio ×2,8 y opacidad ×0,25.
- **Modo claro**: tinta azul pizarra `#33465C` con opacidad 0,12–0,36: tenues
  y elegantes, nunca puntos negros.
- **Modo oscuro**: marfil `#FFF6E6`, azul claro `#DCEBFF` y blanco, opacidad
  0,28–0,92.
- Siguen siendo deterministas (semilla `0x57415253`), estáticas y se dibujan
  una sola vez. Las tarjetas y campos siguen ocultándolas porque el vidrio
  cubre con 0,92–0,96 de opacidad.

### 3. Pestañas Dúo / Trío / Stack (punto 3 del encargo)

**Primer intento: insuficiente.** Se sustituyó el plano
`Modifier.background(colors.background)` por
`colors.surfaceContainerHigh.copy(alpha = 0.97f)`. El bloque dejó de ser casi
negro y pasó a ser azul grisáceo, pero **seguía siendo un rectángulo visible**
delante del fondo: no resolvía el problema, solo lo recoloreaba.

**Corrección definitiva: se elimina el fondo por completo.**

- `stickyHeader(key = "categories")` → `item(key = "categories")`.
- Se elimina `.background(colors.surfaceContainerHigh.copy(alpha = 0.97f))`.
- Se elimina el `HairlineDivider` que remataba el contenedor y su import,
  que quedaba huérfano.

Motivo de dejar de ser fija: un `stickyHeader` queda anclado y, sin un fondo
opaco, las publicaciones se verían **por detrás** de los botones al
desplazar; ese fondo opaco es precisamente el rectángulo que hay que quitar.
Al integrar las pestañas como un `item` normal, la lista coloca cada
elemento debajo del anterior y nunca hay solape, así que no hace falta cubrir
nada y el hueco que rodea a los botones muestra exactamente el fondo real de
la aplicación (Liso, Aurora o Estrellas), sin capas intermedias.

Se conserva íntegro lo demás: los tres botones individuales (`weight(1f)`,
46 dp de alto, esquinas de 18 dp, el activo con `colors.primary` y el texto
`colors.onPrimary`, los inactivos con `colors.surface`, `colors.onSurface` y
filete `colors.outlineVariant`), la deselección al pulsar la pestaña activa,
el botón «Ver todos los grupos» del estado vacío, los testTags `category_bar`
y `category_<Categoría>`, y toda la lógica de búsqueda y filtros.

**Adaptación de la prueba** (requisito modificado, no debilitamiento):
`StacksFlowTest.categoryTabsSwitchGroupsWithoutOpeningFilters` desplazaba
hondo en la lista («Stack completo») y después pulsaba una pestaña con
`performScrollTo()`. Al dejar de ser un elemento fijo, la pestaña ya no está
compuesta a ese desplazamiento y el árbol de semántica no la contiene, por lo
que la acción fallaba con «could not find any node». Se añade el auxiliar
`scrollToCategoryTabs()` (`performScrollToIndex(0)`) que vuelve al inicio
del listado antes de cada interacción con las pestañas. **Ninguna aserción
cambia**: siguen verificándose el cambio de grupo, el recuento de
publicaciones, que no se abre el panel de filtros, que la selección sobrevive
a la recreación de la actividad y que pulsar la pestaña activa deselecciona.

### 4. Legibilidad y consistencia (punto 4 del encargo)

- Revisadas en Oscuro y Claro: Inicio, Buscar equipo, Scrims, Tienda y
  Perfil, más la navegación inferior. Títulos, subtítulos, tarjetas, botones
  y barra inferior usan `MaterialTheme.colorScheme` / `LocalTeamValVisuals`;
  no se ha introducido ningún color fijo.
- El único cambio de contraste deliberado son las estrellas en modo claro
  (opacidad máxima 0,36), pensadas para no competir con el texto.

### Archivos

- `app/src/main/java/com/example/teamval/ui/components/AppBackdrop.kt`
  (`drawStarField` rehecha).
- `app/src/main/java/com/example/teamval/ui/screens/stacks/StackExplorerScreen.kt`
  (pestañas como `item` sin fondo ni filete; se retiró el texto permanente).
- `app/src/main/java/com/example/teamval/ui/screens/profile/AppearanceScreen.kt`
  (grupo de tema al principio).
- `app/src/main/java/com/example/teamval/navigation/TeamValNavHost.kt`
  (entrada/salida limpias de Apariencia).
- `app/src/main/res/values/strings_stacks.xml` (eliminado `stack_category_hint`).
- `app/src/androidTest/java/com/example/teamval/AppearanceBackgroundsTest.kt`
  (métricas de píxeles acordes al nuevo campo escaso).
- `app/src/androidTest/java/com/example/teamval/StacksFlowTest.kt`
  (auxiliar de desplazamiento hacia las pestañas).

### Verificación

#### Eliminación del rectángulo: medición sobre captura real

Con el emulador en **Buscar equipo** y fondo **Aurora** (el caso más exigente,
por ser un degradado continuo), se muestreó una columna vertical por el hueco
exacto entre los botones Dúo y Trío (x = 372) recorriendo toda la altura de
la fila (y = 831 a 944):

| y | RGB en el hueco |
| --- | --- |
| 700 | 87, 69, 69 |
| 831 | 87, 85, 77 |
| 890 | 86, 88, 78 |
| 944 | 86, 95, 79 |
| 1010 | 85, 100, 83 |

El degradado cambia de forma **suave y continua** a través de la fila de
botones, sin escalón ni banda, mientras que en el centro del botón Dúo
(x = 214) se lee su relleno `21, 33, 45`. Los tres botones tienen ancho
idéntico (304 px).

Con **Claro + Estrellas**, la densidad de píxeles de estrella en el mismo
hueco (9,6 %) queda dentro del rango natural de las zonas libres adyacentes
(12,1 % por encima y 7,4 % por debajo): el campo de estrellas continúa por
detrás de los botones, sin hueco en blanco ni rectángulo.

Se comprobó también el desplazamiento: las pestañas se integran en el listado
y salen de pantalla sin solapes ni artefactos, y al pulsar Dúo se filtra a
«2 publicaciones» sin abrir el panel de filtros.

## Cierre de versión v0.5.6 — 26/10/2026

### Revisión de los avances

- Base Git comprobada: `main` y `origin/main` en `90e3ef9` antes de esta entrega.
  Buscar equipo, Perfil/Apariencia y las correcciones visuales estaban pendientes
  de commit; la mención inicial de que B ya estaba enviado era incorrecta.
- Revisados los 33 archivos pendientes (incluidas las dos pruebas nuevas), sus
  integraciones, preferencias compatibles, recursos y cambios de aserciones.
  No se encontraron errores críticos ni implementaciones a medias. No se eliminó,
  desactivó ni debilitó ninguna prueba durante este cierre.
- Aprobación visual del usuario: eliminación definitiva del rectángulo detrás de
  Dúo/Trío/Stack. Se conserva el diseño aprobado, con pestañas dentro del listado.
- `git diff --check` correcto. Revisión de archivos y búsqueda de credenciales sin
  hallazgos: no se incorporan claves, contraseñas, `local.properties`, almacenes de
  firma, capturas, logs ni temporales. Los informes permanecen en `app/build/`, ignorado.

### Compilación, unitarias y Lint de este cierre

Con el JBR de Android Studio:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --console=plain
```

- **BUILD SUCCESSFUL** (48 s); APK debug y APK de instrumentación correctos.
- **104 pruebas unitarias, 0 fallos, 0 errores, 0 omitidas**, en 14 suites.
- **Lint: 0 errores, 8 avisos** de dependencias (7 `GradleDependency`,
  1 `NewerVersionAvailable`), preexistentes. El JDK también avisa del uso
  obsoleto de `sun.misc.Unsafe` por protobuf/DataStore; no impidió las pruebas.

### Auditoría de instrumentación anterior

Los 36/36 y 38/38 documentados arriba son registros históricos, no el resultado
de una batería completa sobre la última corrección visual.

- La salida conservada de la ejecución general posterior registra **36 casos,
  3 fallos**: `Stage5FlowTest#test04_inventoryShowsStatesAndEquipsItems` y
  `#test07_premiumIsPreviewOnlyAndUnownedVariantsStayLocked` por caída del proceso
  de instrumentación; `#test09_profileRewardAndBundleKeepExactBalanceAndOwnership`
  por no encontrar «Reclamada». No se contabilizan como éxito los casos ausentes.
- La reejecución anterior de **Stage5FlowTest pasó 9/9**, sin fallos ni omitidos
  (XML con fecha `2026-10-26T06:53:47`, salida 0). Esos tres casos ya estaban
  resueltos por reejecución; no se vuelven a ejecutar en este cierre.
- El último XML disponible antes del cierre, `2026-10-26T07:00:12`, contenía
  **AppearanceBackgroundsTest: 1 correcto y 1 fallo**: pasó
  `theThreeBackgroundsDifferAndTheirThumbnailsAreFaithful`; cayó el proceso en
  `starsSelectionPersistsAfterRecreatingTheActivity`.
- Los logcat muestran además abortos nativos del servicio del emulador
  `android.hardware.uwb-service`. Esto acredita inestabilidad del entorno, pero
  no demuestra por sí solo la causa de cada caída de instrumentación.
- Evidencias previas preservadas en `app/build/reports/v056-review/`:
  `previous-suite-summary.txt`, `previous-stage5-rerun.txt` y
  `previous-backgrounds.xml`. Los XML generales originales fueron sobrescritos
  por las ejecuciones focalizadas; no se inventa un informe completo ausente.

Se reejecuta únicamente el caso pendiente, una vez, en Pixel_6/API 37 y con el
Orchestrator existente (sin modificar pruebas ni configuración):

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.example.teamval.AppearanceBackgroundsTest#starsSelectionPersistsAfterRecreatingTheActivity' --console=plain
```

- **Resultado: BUILD SUCCESSFUL (32 s), 1/1 correcto**, 0 fallos, errores u
  omitidos; caso de 18,184 s, XML `2026-10-26T07:09:42` conservado en
  `app/build/reports/v056-review/rerun-stars-persistence.xml`.
- No quedan casos fallidos identificados pendientes de reejecución. **No se
  declara aprobada una batería completa actual de 38/38**: la ejecución general
  tuvo fallos y se recuperaron casos mediante ejecuciones separadas. Una nueva
  ejecución integral estable queda pendiente; no se repite ahora por instrucción
  del usuario de ejecutar únicamente lo pendiente y evitar ciclos de reintentos.
- Revisión satisfactoria para guardar los avances aprobados como
  `TeamVal v0.5.6 - Buscar equipo y mejoras visuales`.

### Próxima etapa: solo plan

Propuesta detallada en `PROJECT_SPEC.md`, sección 16: seis marcos originales,
precios orientativos de 200–800 TC, previsualización sobre el perfil real sin
escrituras ni cobros, compra y equipado con los repositorios existentes, y
Energía Suprema animado con alternativa estática. Catálogo, precios y diseños
pendientes de aprobación. No se inicia implementación ni se modifica la tienda
para esta propuesta; sin pagos reales ni recursos protegidos de Riot.
