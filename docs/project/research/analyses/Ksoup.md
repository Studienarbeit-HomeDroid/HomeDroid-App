## Beschreibung

Ksoup ist eine Open-Source-Bibliothek, die auf Kotlin basiert und speziell für die Verarbeitung von HTML- und XML-Daten entwickelt wurde. Sie baut auf der bekannten Java-Bibliothek Jsoup auf und bietet ähnliche Funktionen zum Parsen, Extrahieren und Manipulieren von HTML-Inhalten. Mit Ksoup können HTML-Daten aus unterschiedlichen Quellen wie Strings, URLs oder Dateien laden und effizient weiterverarbeiten werden.

Die Bibliothek ermöglicht das Extrahieren von Informationen aus dem DOM und das Bearbeiten von HTML-Strukturen. Darüber hinaus legt Ksoup besonderen Wert auf Sicherheit, indem es eingehende Daten filtert und vor XSS-Angriffen schützt.

Ksoup behauptet, in der Lage zu sein, mit jeder Art von HTML-Dokumenten zu arbeiten, selbst wenn diese Fehler oder Unvollständigkeiten aufweisen
**Quellen:** https://github.com/fleeksoft/ksoup, https://github.com/jhy/jsoup/

## Implementierung

### Implementierungsaufwand

 Um Ksoup im Projekt zu integrieren, sind nur wenige Arbeitsschritte notwendig. Hierfür müssen nur die folgen Codezeilen in die `build.gradle.kts` eingebettet werden und das Projekt neu synchronisiert werden.

```kotlin

val version_v2 = "0.2.0"  

implementation("com.fleeksoft.ksoup:ksoup:$version_v2")  

implementation("com.fleeksoft.ksoup:ksoup-network:$version_v2")

```

Weiter müssen keine eigenständige Einstellungen vorgenommen werden, die Bibliothek ist sofor einsatzbereit

Die Nutzung der Bibliothek ist sehr simpel. Hierzu ist es erstmals nur notwendig den Ksoup Parser zu initaliseren, und das HTML zu übergeben. Hierfür werden einem unterschiedliche Möglichkeiten angeboten. So kann das HTML zu einem statisch im Code übergeben werden. Zum anderen werden Methoden zurverfügung gestellt mit den das HTML als URL oder aus einer Datei aus enthalten werden können. Dafür stehen einem fertige Methoden wie `parseGetRequest`  zur Verfüguing die das API Aufruf vollständig übernimmt
Das auslesen von Daten erfolgt einfach von in der Document Klasse definierten Methoden wie `title()` oder `body()`

```kotlin

Log.i("Title", "title => ${doc.title()}")

```
### Dokumentation

Alle wichtigen Informationen zum Einstieg in Ksoup finden sich im offiziellen [GitHub-Repository](https://github.com/fleeksoft/ksoup). Dort werden die Anwendungsfälle der Bibliothek sowie die Einrichtung detailliert erklärt. Zudem sind Beispiele und Erklärungen zu den verfügbaren Methoden und Klassen enthalten.

Eine umfassende [Dokumentation](https://fleeksoft.github.io/ksoup/index.html) zu allen Methoden, Klassen, Exceptions und Interfaces, die in der Bibliothek enthalten sind, wird ebenfalls bereitgestellt. Diese enthält detaillierte Beschreibungen der Funktionen und ihrer Parameter.

Da viele Konzepte von **Jsoup** übernommen wurden, wird auch auf die [Jsoup-Dokumentation](https://jsoup.org/cookbook/extracting-data/attributes-text-html) verwiesen. Diese ist sehr umfangreich und bietet alle notwendigen Informationen für die Arbeit mit der Bibliothek.
## Edge Cases und Funktionalitäten

Um die Stabilität, Funktionalität und Fehlertoleranz des Parsers zu prüfen, wurden die folgenden Testfälle für komplexe und fehlerhafte HTML-Strukturen definiert:

#### Ungeschlossene Tags
**Html-Struktur**

```html

<div>

    <p id="first">[1] first paragraf

    <p id="second"> [2] second paragraf</p>

</div>

```

**Durchführung**

Der Test überprüft, wie der Parser mit einem ungeschlossenen `<p>`-Tag umgeht, wenn der Inhalt einzelner `<p>`-Tags anhand ihrer IDs extrahiert werden soll. Der folgende Codeausschnitt zeigt das Szenario:

```kotlin

fun unclosedTagTest()  

{  

    val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)  

    val first = doc.getElementById("first")  

    val second = doc.getElementById("second")  

    if (first != null) {  

           Log.i("First", first.text())  

       }  

    if (second != null) {  

        Log.i("Second", second.text())  

    }  

}

```

**Ziel**:
Der Parser sollte den Inhalt der einzelnen `<p>`-Tags anhand ihrer IDs entnehmen, auch wenn einer davon nicht korrekt geschlossen ist.

  

**Ergebnis**
Der Parser kann das fehlerhafte HTML ohne Fehlermeldung lesen. Unabhängig davon, ob ein Tag geschlossen ist oder nicht, wird der Text des Tags vollständig und korrekt ausgegeben.

  

![[Pasted image 20241107175419.png]]

  

#### Attributanomalien  
**Html-Struktur:**

```html

<div id=test>missing quotation marks</div>

<a id="first" id="second">double attributes</a>

```

**Durchführung:**
Der Test überprüft, wie der Parser mit Attributanomalien umgeht. Hierfür können Fehler in der ID-Deklaration sowie doppelt vorkommende IDs getestet werden.

```kotlin

fun attributAnomaliesTest() {  

  

    val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)  

    val missingQuotationMarks = doc.getElementById("test");  

    val doubleAttributesByIdFirst = doc.getElementById("first")  

    val doubleAttributesByIdSecond = doc.getElementById("second")  

    if (missingQuotationMarks != null)  

    {  

        Log.i("Result Missing Quotation Marks", missingQuotationMarks.text())  

    }  

    if (doubleAttributesByIdFirst != null)  

    {  

        Log.i("Result double Attributes [ID First]", doubleAttributesByIdFirst.text())  

    }  

    if (doubleAttributesByIdSecond != null)  

    {  

        Log.i("Result double Attributes [ID Second]", doubleAttributesByIdSecond.text())  

    }  

}

```

**Ergebnis:**
Der Parser kann auch bei fehlenden Anführungszeichen in der Attributdeklaration die ID korrekt erkennen und den gewünschten Text ausgeben.
  

![[Pasted image 20241108151125.png]]

Bei einer doppelten Attributdeklaration wird nur die erste Deklaration berücksichtigt; die zweite bleibt unberücksichtigt.

![[Pasted image 20241108151218.png]]
#### Javascript CSS Parsing
**Html-Struktur:**

```html

<button onclick="alert('Klick!')">Klick mich</button>  

<style>body { background-color: blue; }</style>

```

**Durchführung:**
Der Test prüft, ob der Parser JavaScript-Aufrufe innerhalb von Tags korrekt identifizieren kann. Zusätzlich wird getestet, ob CSS-Inhalte zuverlässig erkannt und extrahiert werden können
  
```kotlin

fun testJavaScriptAndCssTest() {  

    val html = """  

                <button onclick="alert('Klick!')">Klick mich</button>  

                <style>body { background-color: blue; }</style>      

               """.trimIndent()  

  

    val doc: com.fleeksoft.ksoup.nodes.Document = Ksoup.parse(html = html)  

    val button = doc.select("button[onclick]").first()  

    val style = doc.select("style").first()  

  

    if(button != null) {  

        Log.i("Result Button", button.attr("onclick"))  

    }  

    if(style != null) {  

        Log.i("Result Css", style.data())  

    }  

}

```

**Ergebnis**
Der Parser ist in der Lage, JavaScript-Aufrufe innerhalb von Tags zu erkennen, ebenso wie CSS-Inhalte zuverlässig zu identifizieren und zu extrahieren.
  

![[Pasted image 20241108154115.png]]

## Bewertung

Der Parser soll nun anhand der Kriterien **Genauigkeit, Fehlerrobustheit, Performance, Benutzerfreundlichkeit, Flexibilität und Community Wartung** bewertet werden. Diese Bewertung gibt eine fundierte Auskunft über die Leistung des getesteten Parsers und ermöglicht eine abschließende Einschätzung.

**Genauigkeit**

D