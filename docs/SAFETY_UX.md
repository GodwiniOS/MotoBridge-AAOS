# Safety & UX Strategy

## Distraction Optimization (DO)
Driver distraction is the critical safety concern. This system implements:

### 1. Interaction Blocking
When the vehicle is in motion (Speed > 0):
- **Mechanism**: A high z-index `FrameLayout` (`SafetyOverlayFragment`) intercepts all touch events.
- **Visual Feedback**: A high-contrast "Moving" warning is displayed.
- **Rationale**: Riders cannot safely type or tap complex UI elements while balancing and navigating a motorcycle.

### 2. Glanceability
UI elements are designed for < 2 second glance time.
- **Typography**: Large fonts (min 24sp for logic, 32sp+ for headers).
- **Contrast**: High contrast (White/Cyan on Black).
- **Information Density**: Minimized. Only "Next Turn" or "New Offer" is shown.

### 3. Voice First (Proposed)
While not implemented in this PoC, the architecture supports Voice interactions. The `RideViewModel` exposes methods (`acceptOffer`) that can be triggered by Voice Intents, bypassing the touch screen entirely.

## Security Considerations
- **Sandboxing**: The app runs in standard Android application sandbox.
- **Permissions**: requests `CAR_INFO` for read-only access.
- **No Control**: The app has NO permissions to write to vehicle buses (CAN/LIN), preventing any possibility of the app interfering with engine or braking systems.
