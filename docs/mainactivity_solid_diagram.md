# MainActivity SOLID Refactor

## Class Diagram (PlantUML)

```
@startuml
skinparam classAttributeIconSize 0

class MainActivity {
  - Navigator navigator
  - BottomLineIndicator bottomLineIndicator
  - MainClickHandler clickHandler
  - MainViewBinder binder
  - SyncService syncService
}

interface Navigator
class FragmentNavigator
Navigator <|.. FragmentNavigator
FragmentNavigator --> BottomLineIndicator

interface BottomLineIndicator
class DefaultBottomLineIndicator

class MainViewBinder
class MainClickHandler

package domain.sync {
  interface AuthProvider
  interface DatabaseApi
  interface NetworkChecker
  interface FavLearnedStateProvider
}

package application.service {
  interface SyncService
  class DatabaseFavLearnedStateProvider
}

package utility.sync {
  class FirebaseAuthProvider
  class FirebaseDatabaseApi
  class DefaultNetworkChecker
  class FirebaseSyncService
}

MainActivity --> Navigator
MainActivity --> BottomLineIndicator
MainActivity --> MainClickHandler
MainActivity --> MainViewBinder
MainActivity --> SyncService
FirebaseSyncService --> AuthProvider
FirebaseSyncService --> DatabaseApi
FirebaseSyncService --> NetworkChecker
FirebaseSyncService --> FavLearnedStateProvider
DatabaseFavLearnedStateProvider --> IELTSWordDatabase
DatabaseFavLearnedStateProvider --> TOEFLWordDatabase
DatabaseFavLearnedStateProvider --> SATWordDatabase
DatabaseFavLearnedStateProvider --> GREWordDatabase
@enduml
```

## Architectural Decisions

- SRP: Split UI duties into `MainViewBinder` (binds views), `MainClickHandler` (event dispatch), `FragmentNavigator` (navigation), and `DefaultBottomLineIndicator` (tab highlighting). Sync responsibility moved to `FirebaseSyncService`.
- OCP: Introduced interfaces (`Navigator`, `BottomLineIndicator`, `SyncService`, `AuthProvider`, `DatabaseApi`, `NetworkChecker`, `FavLearnedStateProvider`) so new behaviors can be added without modifying `MainActivity`.
- LSP: `MainActivity` continues to implement `View.OnClickListener`; overridden behaviors are delegated to `MainClickHandler`, preserving substitutability for subclasses.
- ISP: Clients depend only on the methods they use; e.g., `Navigator` exposes `navigate(...)` only, `BottomLineIndicator` exposes `setActive(...)` only.
- DIP: `MainActivity` depends on abstractions; runtime implementations are injected via constructors at composition time.

## Trade-offs

- Added small number of classes to reduce `MainActivity` complexity, increasing testability and maintainability.
- Navigation remains UI-specific and is therefore placed in `ui.navigation` rather than domain; this balances purity with practicality.
- Sync service uses threading internally; if future needs require structured scheduling, integrate with `WorkManager` while keeping the `SyncService` interface intact.

