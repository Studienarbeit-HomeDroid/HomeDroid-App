Für alle Anforderungen und Tools zum Testen der App und deren Android-Auto-Funktionalität empfiehlt es sich, die [Google-Dokumentation zur DHU (Desktop Head Unit)](https://developer.android.com/training/cars/testing/dhu?hl=de) zu lesen und die Schritte umzusetzen.

## Vorgehen

1. Führe den Befehl `adb forward tcp:5277 tcp:5277` im Terminal aus. Dabei sollt der Port 5277 gestartet werden. Dies ist der Standard Port
2. Auf dem Android Mobilgerät in den Android Auto Einstellungen `Server für Infotainmentsystem starte` drücken
3. Weiteres Terminal öffnen und zum Pfad `cd SDK_LOCATION/extras/google/auto`navigieren 
4. `./dektop-head-unit` ausführen im Terminal 
5. Nun sollte sich die Android Auto Bildschirm öffnen und mit dem Handy sich verbinden
