# Capa de datos

`repository/InMemoryStackRepository.kt` implementa el contrato de Stacks del
dominio. `fake/DemoStackData.kt` contiene el usuario, publicaciones y candidatos
ficticios. Las mutaciones publican instantáneas atómicas por `StateFlow`.

El repositorio se crea en `TeamValApp` y vive dentro del ViewModel de la Activity.
Stacks, equipos, scrims y valoraciones viven en memoria: un nuevo proceso inicia
una sesión demo nueva. Perfil y apariencia se conservan con DataStore Preferences.

La economía de etapa 5 usa `LocalEconomyRepository` y `RoomEconomyStorage`:
movimientos, inventario y misiones persistidos en `EconomyDatabase`. Compras y
recompensas son transaccionales. `InMemoryEconomyStorage` permite verificar las
mismas reglas con commit/rollback sin Android. Catálogo y misiones son locales.

Consultar `PROJECT_SPEC.md` y `ROADMAP.md` antes de ampliar esta capa.
