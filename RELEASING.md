# Public release checklist (maintainer)

This repository currently builds **debug** APKs. Before publishing a downloadable APK for nontechnical users:

1. Test the current beta on Android TV, including pause threshold, Nuvio launch, home exit, stop, permissions and reboot.
2. Generate a **permanent Android release keystore** on a trusted machine. Store the keystore and passwords securely in at least two protected locations; losing the key prevents normal updates.
3. Build a **release APK signed with that same key** for every subsequent release. Do not commit the keystore or passwords to Git.
4. Verify the APK signature, package ID (`org.arnu.diagnostic`), versionCode and install/update behavior.
5. In GitHub **Settings → General → Danger Zone → Change repository visibility**, make the repository public when ready.
6. Open **Releases → Draft a new release**, create a tag (for example `v0.6.0-beta`), upload the signed APK directly, add release notes and mark it as a pre-release.
7. Test the download and installation from a second device, without ADB.
8. Future releases must increase `versionCode` and reuse the **same signing key**.

**Important:** previously installed GitHub Actions debug APKs may not accept an update from the new release key. Early testers may have to uninstall their debug build before installing the first signed public release. Uninstalling clears ARNu settings and the notification-listener authorization.

Do not publicly release a debug APK as if it were a permanently updatable production build.
