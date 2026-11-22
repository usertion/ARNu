# ARNu Diagnostic 0.2

Experimental Auto Rewind Nuvio for Android TV. Grant notification listener access, enable automatic rewind, open Nuvio, play a video, pause and resume without leaving Nuvio. The background listener monitors only Nuvio media sessions and attempts a 7-second seek on resume. Check `adb logcat -s ARNu` for diagnostic results. No notification content is read; no network or storage permission is requested. Disabling the switch stops media monitoring (Android may keep the authorized listener bound).

## Español
Concede acceso a notificaciones, activa el retroceso y abre Nuvio. Reproduce, pausa y reanuda sin salir de Nuvio. Consulta `adb logcat -s ARNu`. Retroceso experimental de 7 segundos. No lee contenido de notificaciones ni solicita acceso a Internet o almacenamiento.

## Build / Compilación
JDK 17, Android SDK 35, Gradle 8.9. GitHub Actions builds a debug APK; download ARNu-Diagnostic-0.2-debug from the successful run artifacts. Debug signing may differ between runs.
