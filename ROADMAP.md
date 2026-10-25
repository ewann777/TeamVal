# Teamval — Roadmap

## Regla de trabajo

Antes de cada etapa, consultar `PROJECT_SPEC.md` y este documento. Trabajar solo
en la etapa solicitada, verificar su resultado, actualizar la documentación y
detenerse. No iniciar automáticamente la siguiente etapa.

El orden definitivo acordado con el usuario es el siguiente. La etapa 5 se retomó
desde los archivos existentes y está completada y verificada. El desarrollo se
detiene aquí; cualquier trabajo posterior requiere una nueva petición.

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
- Monedas gratuitas (Timbal Coins) y recompensas: saldo, gasto, historial y
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

### Punto de parada actual y límites

**Etapa 5 completada y verificada. No quedan funcionalidades pendientes dentro
del alcance del prototipo. Se detiene el desarrollo aquí.**

- Las misiones de sesiones/valoraciones verificadas y las compras premium siguen
  bloqueadas por diseño. Sin VALORANT Tracker, backend, Riot API ni pagos reales.
- Inventario, monedas, misiones reclamadas y cosméticos personales persisten;
  equipos y cosmética aplicada al equipo conservan la limitación previa de
  sesión en memoria. Comprar un cosmético de equipo sí persiste su propiedad.
- La validación se realizó en Pixel 6/API 37. No se realizó una auditoría manual
  de TalkBack ni una matriz adicional de dispositivos/tamaños de texto.
