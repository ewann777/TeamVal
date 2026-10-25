# Teamval — Especificación del proyecto

## 1. Fuente y estado de los requisitos

Este documento es la referencia del proyecto junto con `ROADMAP.md`. Ambos deben
consultarse antes de cada etapa y actualizarse al terminarla. Cada etapa requiere
una petición explícita del usuario; no se avanza automáticamente.

**Limitación de contexto:** el usuario menciona un texto anterior con todos los
requisitos, pero ese texto no está disponible en esta conversación ni en el
proyecto inicial. Esta especificación recoge todos los requisitos disponibles;
no sustituye los requisitos originales pendientes de recibir.

**Actualización de etapa 2:** el usuario ha autorizado expresamente el buscador
de compañeros de VALORANT con stacks, filtros, publicaciones, solicitudes y datos
ficticios. Este alcance sustituye la propuesta anterior de etapa 2 del roadmap;
no es necesario esperar el texto original para implementar este módulo demo.

**Planificación definitiva:** tras completar la etapa 2, el usuario ha fijado
el orden de las cinco etapas y confirmado los requisitos futuros de personalización
y economía del prototipo. Esta revisión fue documental y no autorizaba iniciar
la etapa 3; esa autorización llegó en una petición posterior y la etapa 3 ya está
completada (sección 9).

### Requisitos confirmados

- Aplicación Android llamada Teamval.
- Desarrollo progresivo, con arquitectura profesional, Kotlin y Jetpack Compose.
- Documentación central en `PROJECT_SPEC.md` y planificación en `ROADMAP.md`.
- Organización en `ui/components`, `ui/screens`, `data`, `domain`, `navigation`
  y `ui/theme` (carpeta de tema).
- Identidad visual lavanda y carbón.
- Etapa 1: pantalla de inicio moderna, navegación principal y compilación verificada.
- Stacks, scrims, tienda y monedas son áreas futuras mencionadas por el usuario.
  Sus funciones no se implementan en la etapa 1.
- Al terminar cada etapa: detenerse, resumir cambios y enumerar archivos.
- Etapa 2: explorador de stacks de VALORANT, filtros combinados por rango, región
  y modalidad, creación de publicaciones, solicitudes para unirse y decisiones
  de aceptación/rechazo sobre candidatos simulados. Repositorios y datos ficticios.
- Etapa 3: perfiles, reputación y personalización de perfiles y aplicación;
  completada con datos y cosmética de demostración (sección 9).
- Etapa 3.5: refinamiento visual global premium — vidrio, degradados de acento y
  profundidad — sin cambiar arquitectura, textos ni funcionalidad (sección 9.5).
- Etapa 4: equipos y scrims — autorizada junto con su ampliación (etapa 4.1):
  composición y roles de equipos, programación y estados de scrims, resultados y,
  además, personalización avanzada de equipos y reputación competitiva entre
  equipos con valoraciones tras scrims confirmadas (secciones 10 y 10.1).
- Etapa 5: tienda de cosméticos, monedas gratuitas, recompensas e inventario;
  artículos premium económicos en pesos chilenos (CLP), sin pagos reales durante
  el prototipo.

### Orden definitivo de desarrollo

1. Base visual y arquitectura — completada.
2. Explorador de stacks — completada.
3. Perfiles, reputación y personalización — completada.
3.5. Refinamiento visual global — completada.
4. Equipos y scrims — completada (funcionalidad básica).
4.1. Personalización avanzada de equipos y reputación competitiva — completada;
    las tres pruebas pendientes se verificaron el 25/10/2026 (registro conservado).
5. Tienda, monedas, recompensas e inventario — completada y verificada;
   resultados de recuperación en `ROADMAP.md`.

El usuario ha autorizado la etapa 4 y su ampliación en la misma petición, indicando
que, si el conjunto era demasiado grande, se entregara primero la funcionalidad
básica de equipos y scrims y la ampliación se desarrollara como etapa 4.1 sin
deshacer trabajo existente. Ese es el criterio seguido.

`ROADMAP.md` detalla este mismo orden. Cada etapa necesita una petición explícita
para comenzar; la documentación de requisitos futuros no activa su desarrollo.

### Detalles pendientes de confirmar

- Público objetivo, plataformas y regiones definitivas. El juego confirmado es VALORANT.
- Cuentas, autenticación, campos de perfiles, reglas de reputación, roles y permisos.
- Reglas definitivas de stacks en producción y restricciones reales de emparejamiento;
  las reglas de la demo de etapa 2 se detallan en la sección 8.
- Reglas de equipos y scrims: composición, participantes, programación y resultados.
- Opciones concretas de personalización, catálogo cosmético, inventario y precios en CLP.
- Obtención y gasto de monedas gratuitas, reglas de recompensas y transacciones
  simuladas. Los pagos reales quedan excluidos del prototipo.
- Backend, persistencia, sincronización, notificaciones, chat y servicios externos.
- Requisitos de privacidad, distribución y accesibilidad adicionales; detalles
  de monetización posteriores al prototipo.

Los módulos indicados están confirmados; sus detalles pendientes no se consideran
reglas de negocio aprobadas.

## 2. Alcance de la etapa 1

### Inicio

- Marca, bienvenida y bloque destacado con identidad propia.
- Accesos a las secciones futuras, identificadas como «Próximamente».
- Contenido local, sin cuentas, estadísticas simuladas ni llamadas de red.
- Diseño desplazable, ancho de lectura limitado y respeto de barras del sistema.
- Textos en recursos Android, controles Material 3 y descripciones accesibles.

### Navegación

Decisión provisional de interfaz, pendiente de contrastar con el texto original:
barra inferior con Inicio, Stacks, Scrims y Tienda.

- Navigation Compose y un único `NavHost`.
- Inicio como destino inicial.
- Selección visible, prevención de destinos duplicados y conservación del estado
  de cada sección al alternar pestañas.
- Botón Atrás administrado por Navigation; desde una sección se vuelve a Inicio.
- Stacks, Scrims y Tienda muestran únicamente una pantalla informativa reutilizada.
- No existen formularios, servicios ni lógica de esos módulos; tampoco saldo
  ni operaciones de monedas.

## 3. Diseño visual

- Material 3, tema oscuro de marca consistente con independencia del fondo del dispositivo.
- Lavanda principal: `#C4B5FD`; carbón de fondo: `#17171C`.
- Superficie elevada: `#222229`; contenedor lavanda: `#39304F`.
- Texto principal: `#F5F1FA`; texto secundario: `#C7C2D0`.
- Tarjetas redondeadas, jerarquía tipográfica y espaciado coherentes.
- Color dinámico desactivado para conservar la identidad visual.
- Lavanda y carbón constituyen el tema predeterminado. La etapa 3 añadió
  opciones de temas, colores y tipografías seleccionables, disponibles en
  la pestaña Perfil → Apariencia.
- Etapa 3.5 (acabado premium global): superficies translúcidas tipo vidrio con
  borde de luz y sombra suave, degradados de acento en botones e iconos,
  orbes de luz y cabeceras con barra de acento. La inspiración es el diseño
  premium moderno, el glassmorphism y las interfaces sociales gamer, con
  identidad propia y sin recursos gráficos de terceros.
- Los tokens de este acabado viven en `ui/theme/Visuals.kt` y se derivan del
  esquema Material activo: siguen el acento, el tema y la tipografía elegidos.
- Las etiquetas acompañan a los iconos de navegación; las acciones no dependen
  exclusivamente del color. El contenido se adapta al ancho y permite desplazamiento.

## 4. Arquitectura

Un único módulo `app` en esta etapa. Paquete base existente:
`com.example.teamval`. No se cambia el identificador de la aplicación.

```text
app/src/main/java/com/example/teamval/
├── MainActivity.kt             # Entrada Android y barras del sistema
├── TeamValApp.kt               # Scaffold y composición raíz
├── navigation/                # Destinos, grafo y navegación superior
├── ui/
│   ├── components/            # Componentes visuales reutilizables
│   ├── screens/
│   │   ├── home/              # Inicio
│   │   ├── stacks/            # Explorador, creación, solicitudes y ViewModel
│   │   └── upcoming/          # Aviso genérico de próximas secciones
│   └── theme/                 # Colores, tipografía, formas y tema Material
├── domain/                    # Modelos, validación y contrato de StackRepository
└── data/                      # Repositorio en memoria y datos demo
```

### Reglas de evolución

- Las pantallas reciben eventos mediante callbacks; no acceden directamente a datos.
- `domain` contendrá lógica Kotlin sin dependencias de UI o Android.
- `data` implementará los contratos de repositorio definidos en `domain` cuando
  se confirmen los casos de uso. La UI no dependerá de implementaciones concretas.
- Incorporar ViewModels y estado observable cuando haya estado de negocio real.
  No añadir capas vacías, infraestructura remota ni dependencias innecesarias.
- Centralizar versiones en `gradle/libs.versions.toml` y textos en recursos
  `res/values/strings*.xml` (incluido `strings_stacks.xml`).
- Mantener componentes pequeños y responsabilidades delimitadas.

## 5. Base técnica

- Kotlin, Jetpack Compose, Material 3 y AndroidX Navigation Compose.
- Gradle Wrapper y catálogo de versiones existentes.
- SDK mínimo 24; compile/target SDK 37, heredados del proyecto inicial.
- Android Gradle Plugin 9.4.1 con soporte Kotlin integrado.
- Compilador Compose Kotlin 2.2.10; Compose BOM 2026.02.01.
- Sin permisos de red, SDKs comerciales ni persistencia en esta etapa.

## 6. Criterios de aceptación de la etapa 1 (histórico)

- [x] Documentación de requisitos disponibles y pendientes, y roadmap por etapas.
- [x] Estructura solicitada, tema lavanda/carbón e inicio Compose.
- [x] Navegación entre los cuatro destinos y pantallas informativas provisionales.
- [x] Ausencia de lógica de stacks, scrims, tienda y monedas.
- [x] `:app:assembleDebug` y `:app:lintDebug` completados correctamente.

La comprobación visual, de TalkBack y del botón Atrás en dispositivo se registra
por separado de la compilación: compilar no equivale a probar la interacción.

## 7. Compilación local

En PowerShell, desde la raíz, con un JDK compatible con Gradle:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:lintDebug --console=plain
```

La ruta corresponde al JDK de Android Studio instalado en este equipo. La variable
`JAVA_HOME` encontrada inicialmente apuntaba a una ruta de ejemplo inexistente;
el ajuste anterior afecta únicamente al proceso de PowerShell actual.

APK esperado: `app/build/outputs/apk/debug/app-debug.apk`.

Resultado de la etapa 1: `BUILD SUCCESSFUL`. Lint: 0 errores y 3 avisos de
versiones más recientes disponibles (Compose BOM, Navigation Compose y compilador
Compose). No hay dispositivos conectados; la revisión visual y de interacción
en emulador/dispositivo queda pendiente. Ver el registro de `ROADMAP.md`.

## 8. Etapa 2 — Compañeros de VALORANT

### Pantallas y flujos

- Inicio enlaza al explorador real de Stacks y deja de anunciarlo como próximo.
- Explorador con pestañas «Explorar» (publicaciones ajenas) y «Mis publicaciones».
- Panel de filtros desplegable: rango, región y modalidad, combinados con AND.
  Cada filtro permite «Todos» y hay una acción para limpiar los tres.
- Contador de resultados y estados vacíos con acciones para continuar.
- Tarjetas con anfitrión, título, descripción, rango, región, modalidad, ocupación
  y acción de solicitud o de gestión según quién sea el propietario.
- Formulario para crear una publicación; al publicar vuelve a «Mis publicaciones»
  y limpia filtros para que la publicación recién creada sea visible.
- Solicitudes recibidas y enviadas, además de gestión filtrada por publicación.
- «Simular solicitud» añade un candidato ficticio a una publicación propia.
- Aceptar y rechazar actualizan los estados y las plazas en todas las pantallas.
- Mensajes de confirmación y error mediante snackbar.

### Datos y reglas de la demo

- Usuario local: `Nova#TEAM`, rango Oro; sin autenticación ni API de Riot.
- Seis publicaciones iniciales: cinco ajenas (una completa) y una propia con dos
  solicitudes recibidas pendientes. Cinco candidatos ficticios disponibles.
- Rangos por categoría: Sin rango, Hierro, Bronce, Plata, Oro, Platino, Diamante,
  Ascendente, Inmortal y Radiante. Sin divisiones I/II/III en esta etapa.
- Regiones demo: LATAM Norte, LATAM Sur, Brasil, Norteamérica, Europa,
  Asia-Pacífico y Corea.
- Modalidades: Competitivo, Normal, Swiftplay, Spike Rush y Deathmatch por equipos.
- Los filtros son preferencias exactas de la publicación, no restricciones de
  elegibilidad del jugador. No se simulan reglas oficiales de matchmaking.
- Título de 5–60 caracteres y descripción de 10–300; se recortan espacios extremos.
- Capacidad de 2–5 jugadores, incluido el anfitrión, que ocupa la primera plaza.
- Pedir unirse crea una solicitud pendiente sin ocupar plaza.
- No se puede solicitar acceso al propio stack, a uno completo, si ya se es
  miembro o si existe una solicitud previa para esa publicación.
- Solo el propietario decide sobre solicitudes pendientes. Aceptar añade al
  candidato al equipo de forma atómica y nunca supera el cupo. Rechazar no consume plaza.
- Las decisiones son terminales. Si el stack se completa, las otras solicitudes
  siguen pendientes y pueden rechazarse, pero ya no aceptarse.
- Cada candidato demo se usa una vez por publicación; al agotarse se muestra
  un mensaje. No se generan candidatos para stacks ajenos o completos.
- Las solicitudes enviadas a anfitriones ficticios permanecen pendientes;
  la práctica de aceptar/rechazar está disponible en las publicaciones propias.

### Estado y arquitectura

- `StackRepository` en `domain/repository` expone una instantánea observable y
  operaciones que devuelven resultados tipados.
- Modelos y validación en `domain/model`, sin dependencias Android/Compose.
- `InMemoryStackRepository` en `data/repository`, datos iniciales en `data/fake`.
  Las mutaciones sincronizadas publican posts y solicitudes juntos mediante `StateFlow`.
- `StacksViewModel` recibe el contrato del repositorio, expone estado observable,
  filtros, formulario, selección de pestañas y mensajes.
- `TeamValApp` es el punto de composición de dependencias. El ViewModel está
  asociado a la Activity, por lo que conserva los datos al navegar y recrearla.
- La UI recoge el estado con `collectAsStateWithLifecycle`; las pantallas reciben
  datos y callbacks. Los componentes compartidos están en `ui/components/`.
- Subgrafo `stacks`: `stacks/explore`, `stacks/create`,
  `stacks/requests?postId={postId}`. El botón Atrás vuelve al explorador y la
  barra principal reconoce los destinos hijos de Stacks.
- Persistencia solo en memoria: reiniciar el proceso restablece datos, filtros y
  borrador. No hay base de datos, backend ni sincronización.
- Scrims, tienda y monedas siguen fuera de esta etapa.

### Verificación de la etapa 2

- Compilación debug correcta y Android Lint con 0 errores (3 avisos de actualización).
- 9 pruebas unitarias de validación, filtros, duplicados, permisos, decisiones,
  capacidad y agotamiento de candidatos, todas correctas.
- 3 pruebas Compose correctas en Pixel 6 / Android 17: filtros combinados y vacío,
  recreación de Activity, validación y creación, aceptación/rechazo, envío y
  navegación entre pestañas. Las pruebas heredadas también pasaron.
- Resultados y archivos de esta entrega en `ROADMAP.md`.

## 9. Perfiles, reputación y personalización — Etapa 3

**Estado: completada; requisitos implementados con datos de demostración.**

- Perfiles de jugadores con información y presentación personalizables.
- Sistema de reputación asociado a los perfiles. Los criterios, cálculo y formas
  de valoración se definirán antes de su implementación.
- Avatares: imagen o representación visual del jugador.
- Marcos: elementos decorativos para el avatar o la presentación del perfil.
- Fondos: opciones visuales para el fondo del perfil.
- Insignias: distintivos visibles en el perfil; catálogo y reglas de obtención pendientes.
- Temas de aplicación: estilos visuales seleccionables.
- Colores: opciones de personalización cromática, conservando lavanda/carbón como
  configuración predeterminada.
- Tipografías: opciones de estilo tipográfico para personalizar la presentación.
- La personalización debe mantener legibilidad y coherencia entre pantallas.

La etapa 3 contempla seleccionar y aplicar opciones de personalización. La
adquisición mediante tienda, monedas o recompensas y la gestión del inventario
se integrarán en la etapa 5. Quedan pendientes el catálogo inicial de opciones,
los campos editables y las reglas concretas de reputación.

### Reglas de la demo de etapa 3

- Perfil editable: nombre (3–24 caracteres), presentación (máx. 80), biografía
  (máx. 300), agente favorito y región. Avatar, marco, fondo e insignias son
  decorativos y de selección libre.
- Reputación: promedio de valoraciones por estrellas más métricas de comunicación,
  trabajo en equipo y actitud; todas las reseñas son ficticias y locales.
- Estadísticas de VALORANT: datos simulados sin conexión con Riot Games.
- Vista previa en vivo en ambos editores; Guardar persiste y Volver o el botón
  Atrás descarta los cambios sin guardar.
- Apariencia global: tema oscuro/claro/sistema, color de acento en HEX (6 dígitos),
  fondo liso/aurora/cuadrícula y tipografía moderna/editorial/mono. El contraste
  del texto se ajusta automáticamente al acento elegido; lavanda/carbón es el
  valor por defecto y Restablecer lo recupera.
- Guardado local con DataStore Preferences; las preferencias sobreviven al
  reinicio de la aplicación. No hay cuentas ni sincronización.

## 9.5. Refinamiento visual global — Etapa 3.5

**Estado: completada; verificación de compilación, Lint y pruebas correcta.**

Objetivo: elevar la percepción de calidad de toda la aplicación (Inicio, Perfil,
Personalización y Stacks) con una estética premium moderna, de vidrio y acentos
vivos, manteniendo lavanda/carbón como identidad base.

- Superficies de vidrio: relleno translúcido, borde degradado y sombra suave en
  paneles, tarjetas y secciones (`Modifier.glassSurface`).
- Degradados de acento en botones principales, placas de icono y barras de acento.
- Barra inferior de vidrio con filete luminoso y píldora de acento en la pestaña activa.
- Orbes de luz en héroe de Inicio, tarjeta de perfil y fondo global; desenfoque real
  con `Modifier.blur` en el estilo Aurora sobre Android 12+, con degradados suaves
  equivalentes en versiones anteriores (SDK mínimo 24).
- Chips, insignias y badges en píldora con borde de acento; progreso redondeado.
- Tipografía con espaciado refinado, formas con radios más generosos y sombras
  coherentes mediante los tokens de `ui/theme/Visuals.kt`.

### Reglas de compatibilidad

- Sin cambios de arquitectura, navegación, textos, `testTag` ni reglas de negocio:
  la etapa 3.5 es exclusivamente de presentación.
- Todos los tokens se calculan a partir del esquema Material activo, por lo que el
  acento HEX, el tema claro/oscuro, el fondo y la tipografía elegidos en
  Apariencia siguen aplicándose a los nuevos estilos.
- El contraste de texto con cualquier color personalizado sigue garantizado por
  las pruebas unitarias de la etapa 3; el degradado se aleja siempre del color
  del texto para no debilitarlo.

### Verificación de la etapa 3.5

- `:app:assembleDebug`, `:app:testDebugUnitTest` (17 pruebas) y `:app:lintDebug`
  (0 errores) correctos.
- `:app:connectedDebugAndroidTest`: 7 pruebas, 0 fallos en Pixel 6 / Android 17,
  incluida la comprobación de luminancia del fondo en tema claro y oscuro.
- Recorrido manual en emulador (Inicio → Perfil → Stacks) con `uiautomator dump`.
- Detalle de archivos y comandos en `ROADMAP.md`.

## 10. Equipos y scrims — Etapa 4

**Estado: completada; funcionalidad básica verificada. La ampliación sigue en 10.1.**

El usuario autorizó esta etapa y su ampliación en la misma petición, con indicación
de entregar primero la funcionalidad básica de equipos y scrims y de desarrollar la
ampliación como etapa 4.1 sin deshacer trabajo existente.

### Funcionalidad básica implementada

- Crear equipo: nombre, apodo (abreviatura), región, lema y descripción; el creador
  queda como capitán. Solo se puede formar parte de un equipo a la vez.
- Explorador de equipos: listado con plantillas y detalle de equipo (integrantes,
  roles, datos y acceso a scrims).
- Proponer scrim contra un equipo rival: modalidad, horario y notas.
- Estados de scrim: Propuesta, Confirmada, Completada y Cancelada, con transiciones
  válidas (aceptar, rechazar/cancelar y registrar resultado con marcador).
- Propuesta entrante ficticia mediante «Simular propuesta», en la misma línea que
  «Simular solicitud» de Stacks.
- Integración con perfiles: el nombre visible en equipos y scrims procede del
  perfil de la etapa 3.

### Reglas de la demo de etapa 4

- Datos ficticios locales en memoria; reiniciar el proceso restablece todo.
  No hay cuentas, backend, notificaciones ni partidas reales.
- Nombre de equipo de 3–24 caracteres, apodo de 2–5 letras o dígitos, lema de hasta
  80 y descripción de 10–300; se recortan espacios extremos.
- Los equipos rivales iniciales tienen plantillas completas y son jugables en la demo.
- Una scrim exige existir en un equipo, un rival distinto del propio y horario de
  5–60 caracteres.
- Transiciones: Propuesta → Confirmada (aceptación) o Cancelada; Confirmada →
  Completada con marcador; Cancelada y Completada son estados finales.
- Solo el equipo destinatario acepta o rechaza una propuesta; solo las dos partes
  pueden completarla, y ninguna equipo valorada... (reglas de valoración en 10.1).
- El marcador es 0–13 por equipo y solo se registra al completar.

### Verificación de la etapa 4

Resultados registrados (detalle en `ROADMAP.md`):

- `:app:assembleDebug` y `:app:lintDebug`: compilación correcta, 0 errores de
  lint (solo los 4 avisos preexistentes de dependencias).
- Pruebas unitarias: 32 pruebas, 0 fallos en la ejecución de la etapa 4.
- Pruebas de interfaz: 11 pruebas, 0 fallos en Pixel 6 / Android 17
  (creación de equipo con validaciones, propuesta enviada, no duplicable y
  superviviente a cambios de pestaña, filtros con estado vacío, simulación de
  propuesta entrante con aceptación y registro de resultado, y rechazo).

## 10.1. Ampliación — Personalización avanzada y reputación competitiva (Etapa 4.1)

**Estado: completada. La verificación inicialmente pendiente por falta de emulador
se completó el 25/10/2026 durante la recuperación de la etapa 5. Se conserva el
diagnóstico histórico más abajo y el cierre nominal en `ROADMAP.md`.**

### A. Personalización avanzada de equipos

- Identidad visual propia por equipo: logo, colores principal y secundario,
  degradados, banner, fondo de perfil, marcos, estilo visual, insignias
  decorativas, lema, descripción, distribución de secciones y presentación de
  integrantes (cuadrícula, lista o tarjetas).
- **Editor de logos — Opción A (crear dentro de Teamval):** símbolos y formas
  originales dibujados en la propia app, selección de colores, degradado lineal o
  radial, iniciales o abreviatura, fondo, estilo de marco (ninguno, sólido, brillo,
  doble), formato circular o cuadrado, tamaño del símbolo, vista previa en vivo y
  guardado.
- **Editor de logos — Opción B (subir imagen):** Android Photo Picker
  (`PickVisualMedia`), sin permisos de almacenamiento; acepta JPG, PNG y WebP;
  recomienda 1:1 y 512×512 o superior; recorta proporcionalmente imágenes
  rectangulares con desplazamiento y zoom dentro del marco (sin deformar);
  previsualización circular y cuadrada; cambio posterior del logo; copia optimizada
  en almacenamiento privado de la aplicación.
- Validaciones de formato, tamaño y decodificación con mensajes de error; la
  moderación automática real **no** está implementada: solo reglas locales de
  demostración y un modelo preparado para incorporar moderación, denuncias y
  revisión de contenido cuando exista backend.
- Editor de perfil de equipo con vista previa en tiempo real, Guardar, Cancelar y
  Restablecer, siguiendo el patrón de borrador de la etapa 3.

### B. Reputación y valoraciones entre equipos

- Se valora el comportamiento, profesionalismo y respeto, **no** el nivel de juego.
- Cinco categorías con 1–5 estrellas: Respeto, Seriedad, Puntualidad, Comunicación
  y Deportividad.
- Reseña breve opcional sujeta a normas comunitarias de demostración (longitud,
  sin enlaces y lista básica de términos inapropiados).
- Perfil de equipo con valoración general, cantidad de valoraciones, promedio por
  categoría, comentarios recientes, historial de scrims confirmadas y completadas,
  e insignias de reputación.
- Prevención de abusos: solo se valora tras una scrim Completada (y por tanto
  Confirmada), una sola valoración por equipo y enfrentamiento, el capitán
  representa a su equipo, prohibido valorar al propio equipo, sin duplicados,
  denuncia de comentarios inapropiados, sin recompensas de monedas por valoraciones
  positivas y sin penalización por perder partidas. Diseño preparado para
  moderación y apelaciones futuras.
- Pantalla de valoración con nombre y logo del rival, cinco categorías de estrellas,
  campo de comentario, envío, confirmación y estado de «valoración enviada»; se
  integra con el historial de scrims.
- Durante la demo los datos son ficticios y locales; no existe moderación real ni
  pagos, y la etapa 5 no se inicia con esta ampliación.

### Implementación y resultados de la etapa 4.1

Arquitectura (la lógica no vive en las pantallas):

- Dominio puro y testeable: `TeamIdentity`, `RatingModels` (`RatingRules`,
  `CommentPolicy` de 10–500 sin enlaces ni términos bloqueados), `ImageFileRules`
  (JPG/PNG/WebP, 10 MB máx., 512 px recomendado) y `ImageCropMath` para el
  recorte proporcional.
- Repositorios separados: `TeamRepository`/`InMemoryTeamRepository` (equipos y
  scrims) y `RatingRepository`/`InMemoryRatingRepository` (valoraciones,
  validadas contra el estado vivo de equipos y scrims); ambos compartidos desde
  `TeamValApp`.
- Componentes reutilizables: `TeamLogoView` (logos dibujados con Canvas en
  coordenadas normalizadas e imágenes recortadas sin deformar), `StarRating`
  (`StarRatingRow` editable y `StarsDisplay` de solo lectura), `ImageCropView`
  (desplazamiento y zoom), `LogoEditorControls`, `TeamIdentityPreview`,
  `TeamReputationCard` y `TeamDisplay`.
- Almacenamiento de imagen: `TeamLogoStorage` en `filesDir/team_logos`, WebP con
  lado máximo 1024 proporcional (nunca amplía) y borrado seguro; el recorte
  rectangular se guarda como metadatos (`ImageCrop`) y se aplica al pintar.
- Rutas nuevas declaradas antes de `scrims/team/{teamId}` para evitar su
  captura; edición de identidad basada en borrador con vista previa en vivo;
  el guardado del logo solo persiste el logo para no pisar cambios pendientes
  del perfil.

Resultados históricos de verificación de la entrega original:

- Compilación de debug + APK de pruebas: `BUILD SUCCESSFUL`.
- Pruebas unitarias: **55, 0 fallos** — matemáticas de recorte (6), normas de
  comentario (3), reglas de valoración (2), reglas de imagen (4) y 8 de
  `InMemoryRatingRepositoryTest` (bloqueos de `canRate`, valoración solo de
  scrims completadas, una por enfrentamiento, puntuaciones/comentarios
  inválidos, denuncia única, media exacta de Eclipse Esports 4,8/5 con sus
  insignias y aislamiento del estado de equipos).
- Lint: 0 errores (mismos 4 avisos de dependencias de siempre).
- Pruebas de interfaz (19 en total): 16 en verde, entre ellas
  `TeamLogoStorageTest` (2000×1000 → 1024×512 con proporción 2:1, retrato 900×1600
  conservado y 512×512 sin ampliar) y `rivalProfileShowsReputationExample`
  («4,8/5 estrellas», «10 valoraciones», promedios «Respeto · 4,9/5»,
  «Comunicación · 4,6/5», «Deportividad · 5,0/5» e «13 scrims registradas»).
  **3 pendientes** de reejecución (flujo de valoración con envío y duplicado,
  validación del comentario en pantalla y guardado/restablecer/cancelar de la
  identidad): se detectó que el aviso emergente se superpone a las acciones
  inferiores y se etiquetó para que las pruebas esperen a que desaparezca, pero
  una espera agotó los 8 s y queda por diagnosticar en dispositivo.
- Entorno: el emulador no puede arrancar con el equipo actual (≈1 GB de RAM
  libre; el AVD pide 4 GB y WHPX devuelve acceso denegado). El usuario optó por
  retomar la verificación en dispositivo más adelante; no hay regresiones
  conocidas de la etapa 4 (11/11 pruebas de interfaz en verde en su última
  ejecución completa).

## 11. Tienda, monedas, recompensas e inventario — Etapa 5

**Estado: completada y verificada el 25/10/2026. Ver registro de recuperación y
entrega de la etapa 5 en `ROADMAP.md` para comandos, informes y resultados reales.**

### 11.1 Economía: Timbal Coins

- Moneda virtual original **Timbal Coins (TC)** con icono propio
  (`ic_coin.xml`, vectorial, estilo trazo blanco coherente con el resto de la
  barra de iconos). **No tiene valor monetario real, no es transferible y no
  puede convertirse en dinero**; el monedero lo indica de forma permanente.
- El saldo se muestra en el **inicio** (píldora `home_balance`) y en la
  **tienda** (tarjeta de saldo `shop_balance`), además del monedero.
- Cada movimiento tiene **id único, importe (positivo/negativo), tipo
  (bono/recompensa/compra), fecha y descripción** (`CoinTransaction`). El saldo,
  lo obtenido y lo gastado se **derivan** como sumas del historial (fuente única
  de verdad): no puede haber saldo negativo ni movimientos duplicados.
- Reglas de escritura: todas las operaciones pasan por `LocalEconomyRepository`,
  que serializa con un `Mutex` y ejecutan en transacción (`Room db.withTransaction`
   o snapshot con commit/rollback bajo `Mutex` en memoria): **compra atómica** (saldo + inventario
  + historial se aplican juntos o no se aplica ninguno) y **doble toque
  inofensivo** (la segunda compra simultánea devuelve `AlreadyOwned` sin cobrar).
- El artículo comprado siempre se resuelve **desde el catálogo** (`purchase`
  ignora el objeto recibido y busca el id en el catálogo): la UI no puede
  inyectar precios; id desconocido → `NotFound`.
- Bono inicial de **500 TC** idempotente (solo si no hay movimientos).
- Precios premium en **CLP** solo informativos: sin pagos, sin simular pagos
  completados; el botón dice "Próximamente" y solo permite vista previa.
- **Protecciones de diseño**: sin cajas de botín, sin apuestas, sin ventajas
  competitivas; las rarezas (Común/Raro/Épico/Legendario) son solo estética.
- Seguridad: la lógica local es **suficiente para la demo pero no para dinero
  real**; una versión online deberá validar economía en un backend seguro.

### 11.2 Recompensas y misiones (gratuitas)

- `m_profile` Completar el perfil → **100 TC** (una sola vez; requiere que el
  perfil real sea distinto del predeterminado).
- `m_sessions` Tres sesiones verificadas → 50 TC y `m_ratings` Cinco
  valoraciones verificadas → 100 TC: **`VerificationPending`, nunca reclamables**
  hasta que existan sistemas de verificación reales (no se simula su cumplimiento).
- `m_scrims` 3 scrims jugados → 50 TC, `m_share` 75 TC, `m_event` actividad
  especial → 150 TC: misiones **ficticias de demostración** con botón
  **"Simular progreso"** (progreso acotado al objetivo, reclamo único).
- Nunca se premian valoraciones positivas ni se permite manipular reputación.
- Estados visibles: disponibles / completadas / progreso / reclamadas /
  pendientes de verificación. `MissionRules` (puras, con tests) controlan
  elegibilidad; el progreso demo se revalida en la transacción y
  `MissionStatusEntity` (PK `missionId`) conserva el reclamo único.

### 11.3 Tienda (ShopScreen)

- **11 categorías**: marcos de avatar, fondos de perfil, banners, insignias,
  stickers, mascotas, tarjetas de jugador, decoraciones de equipos, marcos de
  logo de equipos, temas visuales y paquetes cosméticos (29 artículos: 25 con
  precio en TC y 4 premium).
- Ficha de producto: ilustración vectorial propia (`CosmeticArt`), nombre,
  descripción, categoría, rareza, vista previa, estado
  (Bloqueado/Disponible/Adquirido/Equipado) y acción (Comprar/Equipar/
  Desequipar/Próximamente). Ejemplos: Marco lavanda 100, Fondo pastel 150,
  Insignia de estrella 80, Tarjeta especial 250, Mascota Bruno 400, Paquete
  inicio 500; premium $990 / $1.490 / $1.990 / $2.990 CLP.
- Confirmación de compra con precio, diálogo de detalles, fila de destacados,
  categorías deslizables, animación de desbloqueo y avisos (snackbar).
- Los artículos **premium y los paquetes no se equipan** (`binding = null`);
  los cosméticos individuales en TC apuntan a variantes de perfil/equipo o a
  presets de apariencia; las opciones gratuitas de etapas anteriores se conservan.

### 11.4 Inventario, monedero y recompensas

- **InventarioScreen**: artículos con filtro por categoría, resumen
  (saldo + nº de artículos), estados, detalles, comprar/equipar/desequipar,
  nota de artículos bloqueados y acceso al editor de equipo para cosméticos de
  equipo. Los permanentes solo se adquieren una vez (`OwnedItemEntity.itemId` PK).
- **WalletScreen**: saldo, obtenidas, gastadas, historial de movimientos con
  tipo/fecha/descripción, recompensas recibidas y descargo de legal.
- **RewardsScreen**: secciones Disponibles / Pendientes de verificación /
  Reclamadas, tarjetas con progreso, reclamar y simular progreso.

### 11.5 Integración con las etapas 3 y 4.1 (sin sistema paralelo)

- Un único **`CosmeticBinding(target, field, variant)`** apunta al mismo campo
  que ya usan los editores: equipar escribe `frame/background/banner/sticker/
  pet/card/badges` del perfil, `accentHex/background/font` de la apariencia
  (preset `VisualThemePresets`) o `frame/background/badges` de `TeamIdentity`.
  **Desequipar restaura el valor base** (Halo/Nebula/None/Base/Ring/Dusk).
- El estado "equipado" se **deriva** comparando el estado actual con la variante;
  no hay tabla de equipados duplicada.
- Los editores existentes muestran las variantes de la tienda **bloqueadas**
  (sufijo " · Tienda") si no se poseen: `EditProfileScreen(lockedVariants)` y
  `TeamProfileEditorScreen(ownedTeamCosmetics, lockedTeamVariants)`.
- El cosmético de equipo se aplica desde la sección "Cosméticos de equipo" del
  **editor de identidad del equipo**, con el flujo Guardar/Cancelar existente y
  **permiso de capitán** (`TeamsViewModel` valida antes de `updateTeamIdentity`);
  nunca modifica el perfil personal.
- Tienda, inventario y detalles abren ese mismo editor para artículos de equipo;
  no aplican cambios de equipo al margen de Guardar/Cancelar. Equipar perfil o
  apariencia exige que el artículo exista en catálogo y esté adquirido.
- Los cosméticos personal visibles en el perfil (marco, fondo, banner, sticker,
  mascota, tarjeta) se pintan en `PlayerAvatar`/`ProfileHero`; los de equipo en
  `TeamIdentityPreview` y en la ficha real, usando `TeamIdentityPanel` y
  `TeamIdentityLogo` compartidos.

### 11.6 Arquitectura y ficheros

- Carpetas: `ui/screens/shop|inventory|wallet|rewards`, `ui/components`,
  `domain/model`, `domain/repository`, `data/repository`, `data/local`, `data/fake`.
- Modelos tipados + repositorios + un único `EconomyViewModel` con `StateFlow`
  (combine de transacciones/inventario/misiones/preferencias/equipo); **cero
  lógica de compra en Composables**.
- Persistencia: **Room** (`EconomyDatabase`: `coin_transactions`,
  `owned_items`, `mission_status`; KSP 2.2.10-2.0.2, Room 2.7.1) + **DataStore**
  para preferencias (perfil/apariencia). Fallback documentado: si KSP/Room
  hubiera fallado, JSON en DataStore (no hizo falta).
- Config de build nuevas: `ksp`, `room` y `orchestrator` en
  `gradle/libs.versions.toml`; plugin KSP en el build raíz; dependencias Room/KSP
  y `androidTestUtil(orchestrator)` en `app/build.gradle.kts`;
  `android.disallowKotlinSourceSets=false` en `gradle.properties` (permite que
  KSP registre sus fuentes con el Kotlin integrado de AGP 9).

### 11.7 Cómo probar la tienda

1. Arrancar la app → la píldora de saldo del inicio muestra 500 TC → tocarla
   abre el Monedero (historial con "Bono inicial").
2. Pestaña **Tienda**: saldo, destacados, categorías; abrir detalles de un
   producto y comprar "Marco lavanda" (100) → confirmación → "Adquirido".
3. **Inventario**: comprar "Fondo pastel", **Equipar** y **Desequipar**;
   filtrar por categoría; los premium aparecen "Bloqueado / Próximamente".
4. Comprar hasta agotar saldo → la siguiente compra muestra "No tienes saldo
   suficiente…" y el artículo queda "Disponible".
5. Botón **Recompensas** en Tienda: reclamar "Completar el perfil" (tras
   editar el perfil), "Simular progreso" según el objetivo (3, 5 o 1) y
   reclamar una sola vez; las de verificación nunca muestran "Reclamar".
6. **Perfil → Mi inventario**; en el editor de perfil, las variantes de la
   tienda sin comprar aparecen bloqueadas; si eres capitán de un equipo, el
   editor de identidad del equipo aplica cosméticos de equipo al Guardar.

### 11.8 Limitaciones conocidas

- Sin pagos reales, sin backend, sin VALORANT Tracker ni Riot API (fuera de
  alcance por instrucción); los precios CLP son informativos.
- El progreso de las misiones de verificación depende de sistemas futuros:
  quedan en "Pendiente" por diseño.
- Los cosméticos de equipo viven en la identidad en memoria de sesión (datos
  demo): se reinician al cerrar la app (limitación previa de la etapa 4).
- La lógica económica local no es una garantía de seguridad para dinero real.
- La verificación de instrumentación usa el Orchestrator con
  `clearPackageData` (cada prueba parte con datos limpios); sin Orchestrator
  hay que ejecutar los casos individualmente y borrar los datos antes de cada
  uno (`adb shell pm clear com.example.teamval`).

### 11.9 Verificación de la recuperación

- Compilación debug y APK de pruebas correctos; Lint: 0 errores, 8 avisos de
  actualización de dependencias.
- 96 pruebas unitarias correctas.
- 33 casos instrumentados verificados en Pixel 6/API 37: última suite completa
  con 32 correctos y un fallo de sincronización de la prueba nueva de
  perfil/recompensa/paquete; corregida únicamente esa prueba, reejecución
  focalizada 1/1 correcta. Detalles e informes conservados en `ROADMAP.md`.
- Las tres pruebas de interfaz pendientes de etapa 4.1 pasaron en las dos
  ejecuciones completas; sus nombres y resultados permanecen documentados.
