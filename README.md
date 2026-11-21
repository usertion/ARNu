# ARNu Diagnostic 0.1

Small Android TV proof of concept for **Auto Rewind Nuvio (ARNu)**.

## Purpose
Check whether an independent Android TV app can locate Nuvio's active MediaSession and issue a 7-second `seekTo` while playback is paused. This is **not yet** an automatic rewind app.

## Build
Open the project folder in Android Studio with JDK 17 and Android SDK 35 installed. Sync Gradle and choose **Build > Build APK(s)**. The debug APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`. Requires Android Gradle Plugin 8.7.3 and a compatible Gradle installation (Android Studio can configure the Gradle wrapper).

## Test
1. Open Nuvio and pause a video.
2. Open ARNu Diagnostic and grant notification listener access (used only to read media sessions).
3. Tap **Detect Nuvio**.
4. Tap **Rewind 7 s**.
5. Return to Nuvio and check the playback position. The app only confirms the command was sent; it cannot yet prove Nuvio executed it.

## Privacy
No network permissions, analytics, or storage access. The notification listener service is empty; Android grants media-session access to enabled notification listeners. This proof of concept does not read notification content.

## Español
Prueba técnica para comprobar si una aplicación independiente puede detectar la sesión multimedia de Nuvio y enviar una orden de retroceso de 7 segundos. Abre Nuvio, pausa un vídeo, concede el acceso a notificaciones/sesiones multimedia, pulsa **Detectar Nuvio** y después **Retroceder 7 s**. Regresa a Nuvio y comprueba si ha cambiado la posición. Todavía no realiza el retroceso automáticamente.

## GitHub Actions / Compilación automática
In the **Actions** tab, select **Build ARNu Diagnostic APK**, then **Run workflow**. Once successful, download `ARNu-Diagnostic-0.1-debug` under **Artifacts** and unzip to obtain the APK.

En **Actions**, selecciona **Build ARNu Diagnostic APK** y después **Run workflow**. Cuando termine correctamente, descarga `ARNu-Diagnostic-0.1-debug` en **Artifacts** y descomprime el ZIP para obtener la APK.

**Note:** The GitHub Actions build is not yet verified. If it fails, share the error log so the workflow can be corrected.
