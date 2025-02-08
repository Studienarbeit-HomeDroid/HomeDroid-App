## Prinzipien

Die Gestaltung der Benutzeroberfläche einer Automotive-App muss verschiedenen Einschränkungen und Richtlinien folgen, um Ablenkungen des Fahrers zu vermeiden. Es ist wichtig, die von Google definierten Designprinzipien zu beachten, die sowohl die Interaktion mit der App als auch die visuelle Darstellung betreffen. Dazu gehören Vorgaben für die Art der Interaktion, die Textgestaltung und der allgemeine Stil der App. Diese Prinzipien gewährleisten, dass die Benutzeroberfläche sicher, benutzerfreundlich und für die Nutzung im Fahrzeug optimiert ist. Weitere Details:  [Design-Richtlinien von Google](https://developers.google.com/cars/design/design-foundations/interaction-principles?hl=de).

Quelle: https://developers.google.com/cars/design/design-foundations/interaction-principles?hl=de
## Entwicklung

### Kategorien
Aufgrund der spezifischen Anforderungen für Android Auto-Apps werden nur bestimmte Arten von Anwendungen unterstützt. Diese unterscheiden sich in ihren Anwendungszwecken, den Plattformen, auf denen sie ausgeführt werden können, sowie der Nutzungsmöglichkeit während der Fahrt oder im geparkten Zustand. Die unterstützten Kategorien umfassen: Medien-Audio, Nachrichten, Navigation, POI-Apps, IoT-Apps, Video, Spiele und Browser.

Für dieses Projekt wurde die IoT-App gewählt, da diese Kategorie den Anwendungszweck der HomeDroid App perfekt widerspiegelt. Sie ist auf der Plattform Android Auto lauffähig und unterstützt die Nutzung während der Fahrt, was den Anforderungen der App entspricht. 

Quelle: https://developer.android.com/training/cars?hl=de

### Vorlagen
Das Entwerfen einer App für Android Auto erfordert die Verwendung vordefinierter Vorlagen aus der Android for Cars App Bibliothek. Diese gewährleistet, dass die Prinzipien eingehalten werden und standardisiert die Android Auto App-Ansichten. Wichtig ist, dass die Auswahl der Vorlagen auch von der App-Kategorie abhängt, da für unterschiedliche Apps verschiedene Vorlagen verwendet werden können.

Google stellt zudem einen Prozess zur Verfügung, der bei der Gestaltung des Designs hilft und bei der Auswahl der passenden Vorlagen unterstützt. Dabei werden die folgenden Schritte durchlaufen: Nutzeraufgaben definieren, Aufgabenabläufe planen, Fahrstatus entscheiden, Kommunikation planen, App anpassen

### Komponenten
Komponenten können innerhalb von Vorlagen verwendet werden, diese können dann Schaltflächen oder Textfelder sein die der Vorlage übergeben werden können. 



