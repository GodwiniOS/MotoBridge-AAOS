# System Architecture

## Conceptual Diagram

```mermaid
graph TD
    subgraph "Pure Kotlin Core"
        RM[RideManager]
        PP[<<Interface>> PlatformPolicy]
        UA[UserAction]
    end

    subgraph "AAOS Policy Adapter"
        AP[AaosPlatformPolicy]
        CUX[CarUxRestrictionsManager]
        AP ..|> PP
        AP --> CUX
    end

    subgraph "Mobile Policy Adapter"
        MP[MobilePlatformPolicy]
        MP ..|> PP
    end

    subgraph "Driver Apps"
        AAOS_UI[Driver App (AAOS)]
        MOB_UI[Driver App (Mobile)]
    end

    AAOS_UI --> RM
    MOB_UI --> RM
    RM --> PP
    RM --> UA
```

## Data Flow (Policy Enforcement)

1. **User Action**: Driver taps "Accept Ride".
2. **Core Logic**: `RideManager.acceptOffer()` transitions to `WaitingForOtp`.
3. **User Action**: Driver attempts OTP input.
4. **Policy Check**: `PlatformPolicy.canInteract(UserAction.OTP_INPUT)`.
    - **Mobile**: Allowed.
    - **AAOS**: Blocked while moving or under distraction restrictions.

## Module Breakdown

| Module | Responsibility | Dependencies |
| :--- | :--- | :--- |
| `:core` | Ride logic + policy interface | None (Pure Kotlin) |
| `:aaos-policy-adapter` | AAOS policy enforcement | `:core`, `android.car` |
| `:mobile-policy-adapter` | Mobile policy enforcement | `:core` |
| `:driver-app-aaos` | AAOS reference driver app | `:core`, `:aaos-policy-adapter` |
| `:driver-app-mobile` | Mobile reference driver app | `:core`, `:mobile-policy-adapter` |

## Security & Isolation
- The **Core** never reads vehicle signals directly.
- The **AAOS Policy Adapter** is the only component that touches `CarUxRestrictionsManager`.
- This isolates business logic from OEM safety enforcement and VHAL volatility.
