# Capa de dominio

`model/StackModels.kt` define jugadores, publicaciones, solicitudes, filtros y
validación del formulario en Kotlin independiente de Android/Compose.

`repository/StackRepository.kt` expone el contrato observable de Stacks y sus
resultados/errores tipados. La etapa 2 utiliza una implementación local ficticia.

`EconomyModels.kt` y `CosmeticBindings.kt` definen monedas, artículos, misiones y
la aplicación de cosméticos sobre la personalización existente. El contrato
`EconomyRepository` expone compras/recompensas y estado observable sin UI.

Consultar las reglas de la demo en `PROJECT_SPEC.md` y el punto de parada en
`ROADMAP.md` antes de ampliar el alcance.
