# ARNu — Auto Rewind Nuvio

**ARNu** is a lightweight, independent companion app for **Nuvio on Android TV**. It automatically rewinds playback when you resume after a pause, so you don't miss the dialogue or action.

**Current version:** 0.6 — beta / testing.

[Español](#español) · [English](#english) · [Build](#build-from-source) · [License](LICENSE)

## Español

ARNu retrocede automáticamente unos segundos al reanudar un vídeo en **Nuvio**. Funciona en segundo plano, sin modificar Nuvio ni instalar complementos en su interior.

### Funciones

- Retroceso configurable de **1 a 30 segundos**.
- **Pausa mínima** configurable de **0 a 120 segundos**: si la interrupción dura menos, no retrocede. El valor 0 significa que retrocede después de cualquier pausa.
- Interruptor para activar o desactivar el retroceso.
- Opción de inicio automático con Android TV.
- Botones **Ir a Nuvio**, **Salir al inicio** y **Detener ARNu**.
- Interfaz para televisión, manejable con el mando, en español e inglés.
- Ajustes persistentes y funcionamiento en segundo plano.

### Instalación y configuración

1. Descarga el APK desde [Releases](https://github.com/usertion/ARNu/releases) **cuando haya una versión publicada**. Hasta entonces, las compilaciones de prueba se encuentran en [Actions](https://github.com/usertion/ARNu/actions) → ejecución correcta → **Artifacts** (el APK está dentro de un ZIP).
2. Instala el APK en tu dispositivo Android TV. Puede ser necesario autorizar la instalación desde la aplicación que utilices para abrirlo.
3. Abre **ARNu** y pulsa **Configurar acceso multimedia**. Android mostrará el ajuste de *Acceso a notificaciones*: autoriza ARNu. Este permiso permite acceder a las sesiones multimedia activas; ARNu no necesita leer el texto de tus notificaciones.
4. Activa **Retroceso automático** y ajusta el tiempo de retroceso y la pausa mínima.
5. Opcionalmente, activa **Iniciar con Android TV**.
6. Pulsa **Ir a Nuvio** y prueba a pausar y reanudar un vídeo.

**Ejemplo:** retroceso de 7 s y pausa mínima de 15 s: una pausa de 3 s no provoca retroceso; una de 30 s sí.

**Botones de salida:** «Ir a Nuvio» y «Salir al inicio» cierran la pantalla de configuración, pero mantienen activo el retroceso. «Detener ARNu» desactiva el retroceso hasta que vuelvas a activarlo. Android puede conservar el servicio autorizado en memoria aunque esté desactivado.

### Compatibilidad y limitaciones

- Requiere Android **8.0 (API 26)** o superior; está diseñado para Android TV.
- Necesita Nuvio instalado y una sesión multimedia activa que permita desplazamientos (*seek*).
- El comportamiento puede variar según el dispositivo, la versión de Nuvio y el reproductor utilizado.
- El arranque automático depende de que el sistema permita ejecutar el receptor de inicio y conectar el servicio autorizado.
- ARNu no garantiza que Android nunca cierre su proceso. La optimización de batería del fabricante puede afectar al funcionamiento.
- **Versión beta:** probada inicialmente en un Tanix TX5 con Android 14. No se ha validado todavía en todos los dispositivos.

### Privacidad

ARNu no requiere permiso de Internet, acceso a archivos ni permisos de root. Utiliza el acceso a notificaciones de Android para obtener las **sesiones multimedia activas**, detectar el estado de reproducción de Nuvio y solicitar el retroceso. El código no procesa el contenido de las notificaciones ni incorpora analítica o publicidad.

### Problemas conocidos / diagnóstico

- Si no funciona, comprueba que el acceso a notificaciones de ARNu está autorizado y que el retroceso automático está activado.
- Si la pausa es inferior al mínimo configurado, **no debe** retroceder.
- Si Nuvio cambia de contenido o de posición durante la pausa, ARNu puede omitir el retroceso para evitar un salto incorrecto.
- Para diagnóstico mediante ADB: `adb logcat -s ARNu`.
- Las compilaciones **debug** de GitHub Actions pueden tener firmas diferentes. Si Android rechaza la actualización, no desinstales sin antes tener en cuenta que perderás los datos y permisos de la app. Las versiones públicas deben firmarse siempre con la misma clave.

## English

ARNu is an independent Android TV companion app for Nuvio. It rewinds playback when you resume after a pause, helping you catch what you missed.

**Features:** adjustable rewind (1–30 s), minimum pause threshold (0–120 s; 0 = always), enable/disable switch, start on boot, TV-friendly remote controls, persistent preferences, background operation, and actions to open Nuvio, return home or disable ARNu.

**Setup:** install the APK, open ARNu, grant notification listener access under Android settings, enable automatic rewind, configure both times and select **Go to Nuvio**. The permission is used to access active media sessions; ARNu does not process notification text.

**Compatibility:** Android 8.0+; designed for Android TV and Nuvio. Functionality depends on device firmware, media-session support and battery-management behavior. This is beta software.

**Privacy:** no Internet, storage or root permission; no telemetry or advertising. See the source code for details.

**Troubleshooting:** `adb logcat -s ARNu`. A pause shorter than the configured threshold does not trigger rewind.

## Build from source

Requires JDK 17, Android SDK Platform 35, Android Build Tools 35.0.0 and Gradle 8.9.

```bash
gradle --no-daemon assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`.

The [GitHub Actions workflow](.github/workflows/build-apk.yml) builds a **debug** APK and uploads it as a temporary ZIP artifact. **Do not treat this as a stable public update channel:** debug signatures may change between builds.

For public releases, create and securely retain a **permanent release signing key**, build a signed APK, and attach the APK directly to a GitHub Release. Never commit the private signing key or its passwords to the repository.

## License

[MIT License](LICENSE). ARNu is an independent community project and is not affiliated with Nuvio.
