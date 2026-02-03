# MotoBridge Demo Script

Use this script to present the project to stakeholders or in a video.

## Phase 1: The "Mobile" Baseline
*Goal: Show unrestricted app usage.*

1.  **Launch `driver-app-mobile`** on a Phone Emulator.
2.  **Narrator**: "Here is our standard Driver App running on a tablet/phone."
3.  **Action**: Tap `Wait for Offer...` to simulate an offer.
4.  **Action**: Tap `Accept Ride`.
5.  **Action**: Tap `Auto-Fill OTP` (Notice it works immediately).
6.  **Narrator**: "On a phone, I can accept rides and type freely. But on a bike, this is dangerous."

## Phase 2: The "Automotive" Safe Mode
*Goal: Show policy enforcement in action.*

1.  **Launch `driver-app-aaos`** on an Automotive Emulator (1024p).
2.  **Narrator**: "Here is the exact same logic running on the bike dashboard."
3.  **Action**: Tap "Simulate Drive" (Bottom right button).
    *   *Visual*: Verify the screen shows the red/black "Moving" overlay or restricted state.
4.  **Action**: Try to submit OTP while moving.
    *   *Result*: The app blocks it.
5.  **Narrator**: "Because the bike is moving, the policy adapter blocks restricted inputs. The business logic didn't need to change - the platform enforced the safety."

## Phase 3: The Code Walkthrough
*Goal: Show the Architecture.*

1.  Open `AaosPlatformPolicy.kt` in `aaos-policy-adapter`.
    *   Show `CarUxRestrictionsManager` usage and action blocking.
2.  Open `PlatformPolicy` in `core`.
    *   Show `canInteract(UserAction.OTP_INPUT)`.
3.  **Narrator**: "The core asks permission. The policy adapter grants or denies it based on vehicle state."

---
**Conclusion**: "This is how we ship fast (shared logic) while staying safe (policy enforcement)."
