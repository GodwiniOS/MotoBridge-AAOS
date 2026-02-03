# MotoBridge-AAOS 🚦

**MotoBridge-AAOS** is an open-source automotive platform that lets mobility apps run on AAOS dashboards **safely**, **compliantly**, and **without OEM-specific forks**.

**Not** an app store. **Not** a projection system. **Not** a Google Play replacement. **Not** production software.

---

## 📖 Story: why this exists

A bike taxi driver rides all day with a phone mounted on the handlebar. The app is essential for work, but every tap is a safety risk. Two-wheelers are always moving and OEMs are legally responsible for what appears on the vehicle screen. So OEM dashboards block arbitrary apps, and drivers fall back to unsafe phone usage.

**MotoBridge-AAOS is the bridge**: an OEM-first contract + enforcement layer that lets the *same* app adapt to different OEM safety constraints **without rewriting business logic**.

---

## 🧭 Quick navigation
- Problem
- Goal
- Solution
- Architecture
- Policy enforcement flow
- Repository structure
- Specs and contracts
- Governance portal
- Roadmap
- Documentation
- Who this is for
- Mission

---

## ❗ Problem
Bike taxi drivers are forced to use phone-mounted apps while riding. Although vehicles now ship with AAOS dashboards, most Android apps cannot run safely or compliantly on OEM platforms.

## 🎯 Goal
Enable mobility apps to run safely on AAOS dashboards without bypassing OEM safety rules or rewriting apps per vehicle.

## ✅ Solution
MotoBridge separates **business logic** from **vehicle safety enforcement** using a policy-driven architecture. Apps ask whether an action is allowed; the platform decides based on OEM rules and vehicle state.

---

## 🧱 Architecture (policy-first)

```
App Logic (Core)
   |
   | -> PlatformPolicy (ask permission)
   |
AAOS Policy Adapter
   |
CarUxRestrictions / OEM Safety Services
```

### Rule of thumb
If the vehicle is moving and the OEM policy restricts text input or OTP, **the platform blocks it**. The UI does not decide. The policy layer does.

<details>
<summary><strong>Architecture details</strong></summary>

- **Core**: Pure Kotlin; no Android dependencies; never touches VHAL.
- **AAOS adapter**: Enforces OEM safety rules via `CarUxRestrictionsManager`.
- **Mobile adapter**: Allows interaction (for mobile simulation).
- **Driver apps**: Same business logic, different policy adapters.

</details>

---

## 🛡️ Policy enforcement flow (simple)

1) Core logic wants to perform an action (OTP input, text entry, manual action).
2) Core calls `PlatformPolicy.canInteract(UserAction.X)`.
3) AAOS policy adapter checks `CarUxRestrictionsManager` (or OEM safety services).
4) If restricted, the core fails fast and the UI disables the action.

<details>
<summary><strong>Core interface snapshot</strong></summary>

```kotlin
interface PlatformPolicy {
    fun canInteract(action: UserAction): Boolean
}
```

</details>

---

## 🗂️ Repository structure

- `core` (folder: `motobridge-core`)
  - Pure Kotlin core logic and `PlatformPolicy`
- `aaos-policy-adapter`
  - AAOS enforcement using `CarUxRestrictionsManager`
- `mobile-policy-adapter`
  - Mobile policy adapter (always allow)
- `driver-app-aaos` (folder: `aaos-app`)
  - AAOS reference driver app
- `driver-app-mobile` (folder: `driver-sim-app`)
  - Mobile reference driver app
- `specs`
  - OEM safety contracts (YAML/JSON)
- `governance-portal`
  - Web tool to validate app metadata vs OEM profiles
- `vehicle`
  - Mock vehicle signals for simulation (not used by core)

---

## 📜 Specs and contracts (OEM safety contract)

Specs are versioned, human-readable OEM safety contracts. Example:

```yaml
spec_version: 1.0

oem:
  name: sample-oem
  restrictions:
    when_moving:
      block:
        - OTP_INPUT
        - TEXT_ENTRY
        - MANUAL_ACTION
  conditions:
    ignition_required: true
```

App metadata example:

```yaml
spec_version: 1.0
app:
  package: com.example.driver
  actions:
    - OTP_INPUT
    - TEXT_ENTRY
```

---

## 🧪 Governance portal (tooling, not a store)

The portal validates app metadata against OEM safety contracts and outputs:
- SAFE
- RESTRICTED
- REJECTED

Open `governance-portal/index.html` to use the static tool.

<details>
<summary><strong>Portal rules (summary)</strong></summary>

- If an app declares actions blocked while moving, result is **RESTRICTED**.
- If the OEM profile is always-moving and the app requires blocked actions, result is **REJECTED**.

</details>

---

## 🗺️ Roadmap

- [x] Core policy architecture ✅
- [x] AAOS + Mobile reference apps ✅
- [x] OEM safety contracts (YAML/JSON) ✅
- [x] Runtime policy enforcement ✅
- [ ] Vehicle signal abstraction
- [ ] Policy versioning
- [ ] CI compliance checks
- [ ] OEM simulator profiles

---

## 📚 Documentation

- `docs/ARCHITECTURE.md` — architecture diagram + module breakdown
- `docs/MEDIUM_ARTICLE.md` — background + rationale
- `DEMO_SCRIPT.md` — step-by-step demo flow

---

## 👥 Who This Is For
OEM platform teams, Tier-1 suppliers, mobility app developers, and engineers working on SDV systems.

---

## 🎯 Mission
Build safe, trusted mobility experiences on AAOS dashboards so drivers don't need unsafe phone setups.
