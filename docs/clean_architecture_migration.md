# Clean Architecture Restructuring

## Layers

- Presentation: `ui/*` and adapters
- Application: `application/usecase/*`
- Domain: `domain/model/*`, `domain/repository/*`
- Infrastructure: `data/*` (sources, databases), compat `compat/*`

## Contracts

- `VocabularyRepositoryContract` defines use-case facing repository methods

## Use Cases

- `GetWordsByLevelUseCase` aggregates words by level via the contract

## Compatibility

- `VocabularyRepository` continues to expose previous methods and now implements the domain contract
- `VocabularyRepositoryV1` facade provides versioned entry point delegating to existing repository

## Migration

- UI should depend on use cases instead of repositories.
- Repositories implement domain contracts to decouple from infrastructure.

## Testing

- Unit tests verify use case logic and performance
- Instrumented tests verify compatibility facade matches existing behavior

## Performance

- No degradation; repository parallelization and DB improvements maintained
