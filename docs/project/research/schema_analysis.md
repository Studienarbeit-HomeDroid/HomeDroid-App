## Beschreibung
Der HTML-Parser wird kurz hinsichtlich seiner Funktionalität, Struktur und technologischen Details beschrieben. Dabei werden Aspekte wie die zugrunde liegende Programmiersprache sowie grundlegende Merkmale und besondere Eigenschaften des Parsers beleuchtet.

## Implementierung
Der Parser wird in einem Testprojekt implementiert. Dabei sind die folgenden Kriterien zu beachten:
- **Implementierungsaufwand**: Welcher Aufwand muss betrieben werden den Parser in das Projekt zu integrier
- **Dokumentation**: 
	Welche Möglichkeiten stehen dem Nutzer zur Verfügung welche Dokumentationen über den Parser zu erhalten und wie gut und ausführlich sind diese Dokumentationen. 

## Funktionalitäten und Fehlertoleranz
Der Parser wird darauf getestet, ob alle definierten Anforderungen erfüllt werden können.
### Mindestanforderungen

• **JavaScript-Aufrufe auslesen**: 
	Der Parser muss in der Lage sein, JavaScript-Events oder -Aufrufe aus den HTML-Tags korrekt zu extrahieren.
• **Attribute und deren Inhalte extrahieren**: 
	Er muss Attribute und deren Werte zuverlässig auslesen können.
• **HTML-Tag und Inhalt extrahieren**: 
	Der Parser muss Text und andere Inhalte aus den HTML-Elementen präzise extrahieren können.

Zudem wird getestet, wie robust der Parser mit fehlerhaften HTML-Strukturen umgeht. Ziel ist es, die Stabilität und Fehlertoleranz zu bewerten und herauszufinden, wie zuverlässig der Parser mit solchen Szenarien umgehen kann.

### Durchführung
Der Parser soll vordefinierte HTML-Snippets einlesen und das erwartete Ergebnis liefern. Dabei wird überprüft, ob das gewünschte Ergebnis erzielt wird. 
#### Alle Anforderungen erfüllt werden.
**JavaScript-Aufrufe auslesen**
```html
<button onclick="alert('Test')">Test</button>  
```
***Ziel***
Methode die bei klicken ausgeführt wird soll ausgelesen werden können

**Attribute mit Inhalt extrahieren**
```html
<div id="main">Test</div>
```
***Ziel***
Den Name des Attributes und den Inhalt des Attributes zu erhalten

**HTML-Tag und Inhalt extrahieren**
```html
<p>This is a paragraph.</p>
```
***Ziel***
Inhalt des des Tag extrahieren und den Tag selbst erkennen und ausgeben

#### Fehlertoleranz
**Ungeschlossene Tags**
```html
<div>
    <p id="first">[1] first paragraf
    <p id="second"> [2] second paragraf</p>
</div>
```
***Frage***
Können die Inhalte korrekt entnommen werden

**Attributanomalien**
```html
<div id=test>missing quotation marks</div>
<a id="first" id="second">double attributes</a>
```
***Frage***
Kann der Inhalt korrekt ausgelesst werden auch bei Syntaxfehlern. Was passiert bei doppelten Attribut deklarationen


## Zeit- und Speicherverbrauch
Führe Benchmarks durch, um die Leistung des Parsers in Bezug auf Geschwindigkeit und Effizienz zu bewerten. Messungen erfolgen unter verschiedenen Bedingungen, insbesondere bei großen oder komplexen HTML-Dateien.

**Testdaten**
Einfache Dokumente

Komplexe (große) Dokumente 


Diese testen sollten hierbei  10 - 50 mal wiederholt werden. Dies sorgt dafür das ausreißer entfernt werdn und Statisiken erstellt werde die Mittelwert Standardabewicht Varianz etc

Diese sollten dann als Grafik dargestellt werden und mit den anderen Parsern verglichen werden.
## Bewertung
Schlussendlich erfolgt eine Bewertung des Parsers anhand der folgenden Kriterien:

### Genauigkeit
- **Frage**: Wie präzise werden HTML-Dokumente geparst?
- **Bewertung**: Prüfung der Fähigkeit des Parsers, HTML-Dokumente korrekt und vollständig zu interpretieren.

### Fehlerrobustheit
- **Frage**: Kann der Parser ungültiges oder fehlerhaftes HTML verarbeiten, ohne abzustürzen?
- **Bewertung**: Analyse, wie robust der Parser gegenüber strukturellen Fehlern im HTML ist.

### Performance
- **Geschwindigkeit**: Wie schnell verarbeitet der Parser große HTML-Dateien?

### Benutzerfreundlichkeit
- **Dokumentation**: Qualität und Umfang der Dokumentation, inklusive Anleitungen und Beispielen.

### Flexibilität
- **Anpassungsfähigkeit**: Möglichkeit, spezifische Parsing-Regeln zu definieren oder anzupassen.
- **Erweiterbarkeit**: Fähigkeit, Funktionen zu erweitern und individuelle Anpassungen vorzunehmen.

### Community und Wartung
- **Entwickler-Community**: Ist die Community aktiv? 
- **Wartung**: Gibt es regelmäßige Updates und Support?


