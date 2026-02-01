# System Architecture

## Conceptual Diagram

```mermaid
graph TD
    subgraph "Pure Kotlin Core"
        RM[RideManager]
        RS[RideStateMachine]
        PP[<<Interface>> PlatformPolicy]
    end

    subgraph "AAOS App"
        AAOS_UI[dashboardActivity]
        AP[AaosPlatformPolicy]
        HAL[Vehicle HAL / CarService]
        
        AAOS_UI --> RM
        AP ..|> PP
        AP --> HAL
    end

    subgraph "Mobile App"
        MOB_UI[MainActivity]
        MP[MobilePlatformPolicy]
        
        MOB_UI --> RM
        MP ..|> PP
    end

    RM --> RS
    RM --> PP
```

## Data Flow (Optimized)

1. **User Action**: Driver taps "Accept Ride".
2. **Core Logic**: `RideManager.acceptOffer()` is called.
3. **State Transition**: State moves to `WaitingForOtp`.
4. **User Action**: User attempts to type OTP.
5. **Policy Check**: `RideManager` calls `policy.canInteract()`.
    - **Mobile**: Returns `true`. Transaction proceeds.
    - **AAOS**: Returns `false` (if moving). Transaction rejected or queued.

## Module Breakdown

| Module | Responsibility | Dependencies |
| :--- | :--- | :--- |
| `:motobridge-core` | Business Logic, State Machine | None (Pure Kotlin) |
| `:aaos-app` | Automotive UI, Safety Enforcement | `:motobridge-core`, `:vehicle` |
| `:driver-sim-app` | Simulation UI, Standard Android | `:motobridge-core` |
| `:vehicle` | Mock HAL, Signal Simulation | `android.car` (stubs) |

## Security & Isolation
- The **Core** has no access to vehicle signals directly. It only knows about abstract "Policies".
- The **AAOS App** is the only component with permission to read vehicle data (`CAR_INFO`).
- This ensures that even if the business logic has a bug, it cannot accidentally send command signals to the vehicle, as the Core has no referenced to the HAL.
