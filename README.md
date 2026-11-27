# JARVIS Android — starter build

This is a native Android starter app for OPPO/Android devices.

## Current working features
- Dark blue JARVIS-style text interface
- `message Name: message text` opens Android's share chooser so you can select a compatible messaging app and recipient, review, and send.
- `call NUMBER` opens the dialer for review
- `open WhatsApp/Telegram/Instagram/YouTube/Chrome/Gmail/Maps/Spotify/Settings`
- `search your words` opens Google search

## Important limitations
- This starter does not silently send messages, place calls, read private chats, or control every app. Android and messaging apps require user confirmation and impose restrictions.
- It does not yet include a conversational AI backend, wake-word listening, notification reading, or accessibility automation.
- Free AI services may have rate limits and may require an API key. Do not paste private keys into public repositories.
- The app does not request microphone, contacts, SMS, or accessibility permissions because the current starter does not need them.

## Build an APK without installing Android Studio on your phone
1. Create a GitHub repository named `jarvis-android`.
2. Upload the contents of this project to the repository root.
3. Open the repository's **Actions** tab and run **Build JARVIS APK** (or push to `main`).
4. Open the completed workflow run, download the `JARVIS-debug-APK` artifact, and extract `app-debug.apk`.
5. Transfer the APK to your OPPO A5 Pro, tap it, and allow installation from that source if Android asks.

The GitHub Actions workflow builds the APK in the cloud. The APK is a debug build for testing, not a Play Store release.
