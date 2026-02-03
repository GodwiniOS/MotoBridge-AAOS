# Decoupling Mobility Logic from Embedded Constraints: A "Core-Policy" Architecture for 2-Wheeler SDVs

*A technical case study on integrating rapid-release Mobile Apps into slow-release Automotive Firmware.*

---

**Context**: For EV OEMs (like Ather, Ola, TVS), the dashboard is a Software Defined Vehicle (SDV) node. It runs Android Automotive OS (AAOS) or extensive AOSP forks.
**Problem**: Integrating ride-hailing logic (Uber/Rapido) natively into the cluster is painful because consumer apps iterate weekly, while vehicle firmware iterates quarterly.

This article proposes a **Partition-Aware Architecture** that allows Mobility Logic to run safely on the cluster without tight coupling to the Vehicle HAL (VHAL).

---

## 1. The Conflict: Speed vs. Safety

In a typical embedded Android environment:
*   **The Cluster OS** (System Partition) is critical, safety-certified, and updates slowly (OTA).
*   **The Driver App** (Data Partition) needs new features (surge pricing, heatmap) daily.

If the Driver App directly calls `CarPropertyManager.getIntProperty(VehicleArea.GLOBAL, Speed)`, it becomes tightly coupled to the specific VHAL implementation of that firmware version. If the VHAL ID changes in an OTA, the app crashes.

## 2. The Solution: "MotoBridge" Architecture

We decouple the **Domain Logic** from the **Vehicle Infrastructure** using a clean "Core + Policy" pattern.

### The 3 Layers

#### Layer A: The "Pure" Core (`/product` or App)
This is a pure Kotlin library containing the Ride State Machine.
*   **Dependencies**: Zero. No `android.car`, no `Context`.
*   **Responsibility**: Deciding *what* the driver wants to do (e.g., "Accept Ride").
*   **Portability**: Can run on the driver's phone OR the dashboard.

#### Layer B: The Automotive Injector (`/system_ext`)
This is the Android lifecycle wrapper. It injects the specific vehicle constraints into the Core.
*   **Dependencies**: `android.car`, `CarUxRestrictionsManager`.
*   **Responsibility**: enforcing DOT/safety regulations.

```kotlin
// The Core asks: "Is this action allowed?"
// The Policy answers based on the specific Bike Firmware status
class Ather450xPlatformPolicy(
    private val carUx: CarUxRestrictionsManager
) : PlatformPolicy {
    override fun canInteract(action: UserAction): Boolean {
        val restricted = carUx.currentCarUxRestrictions.isRequiresDistractionOptimization()
        return when (action) {
            UserAction.OTP_INPUT -> !restricted
            UserAction.TEXT_ENTRY -> !restricted
            else -> true
        }
    }
}
```

#### Layer C: The Mock VHAL (Development)
For parallel development, we mock the VHAL signals so app developers don't need a physical scooter (Hardware-in-the-Loop simulation).

---

## 3. Why this matters for SDV Engineers

### A. OTA Resilience
Since the Core Logic never touches the VHAL directly, you can refactor your entire Vehicle Signal specification in the Firmware. As long as the **Safety Policy adapter** is updated, the Driver App (Business Logic) needs **zero changes**.

### B. Distraction Compliance (GAS/ISO)
Google Automotive Services (GAS) requires strict adherence to `CarUxRestrictions`.
By isolating this check into a `PlatformPolicy`, we guarantee that **every single interaction** in the app is checked against the vehicle's motion state.
*   *Speed > 0?* -> Policy returns `false` for restricted actions.
*   *App Behavior* -> Automatically hides Keyboard, rejects OTP entry.

### C. Performance on Low-Spec Silicon
Scooter dashboards often run on constrained chipsets (e.g., Snapdragon 2-series or i.MX 8).
This architecture allows the "Core" to be extremely lightweight (low RAM footprint) while the heavy "UI" layer is managed by the OS. We can throttle the Core's polling rate based on the `IgnitionState` signal from VHAL to prevent battery drain when parked.

---

## Summary

The **MotoBridge** pattern allows OEMs to offer a "Phone-like" app experience on the Dashboard, without compromising the "Tank-like" stability required of Automotive Firmware.

[**Explore the Codebase on GitHub**]
