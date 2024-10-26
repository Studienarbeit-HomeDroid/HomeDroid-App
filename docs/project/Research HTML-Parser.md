# Ksoup

Eine Kotlin Multiplatform-Bibliothek zum Arbeiten mit HTML & XML.

**Einbinden mit:
`implementation("com.fleeksoft.ksoup:ksoup:<version>")`

**Beispiel Verwendung:
`val html = "<html><head><title>Beispiel</title></head><body>Hallo Welt</body></html>"
`val doc: Document = Ksoup.parse(html = html) println("Titel: ${doc.title()}") 
	`// Ausgabe: Titel: Beispiel 
`println("Body Text: ${doc.body().text()}") 
	`// Ausgabe: Body Text: Hallo Welt

**Hauptfunktionen
 -  HTML von URLs, Dateien oder Strings scrapen und parsen
 - Daten mit DOM-Traversierung oder CSS-Selektoren finden und extrahieren
 - HTML-Elemente, Attribute und Text manipulieren
 - Benutzergenerierte Inhalte gegen eine Safelist bereinigen, um XSS-Angriffe zu verhindern
 - Sauberes HTML ausgeben
 
 Es gibt auch die Möglichkeit **HTML direkt von einer URL zu parsen**

# kotlin.html

Offizielle Kotlin-Bibliothek zum Generieren und Parsen von HTML. Bietet eine DSL (Domain Specific Language) zum Erstellen von HTML-Strukturen und kann auch zum Parsen verwendet werden.

- Gute Integration mit Kotlin
- Multiplatform
- Offiziell von JetBrains unterstützt

Nachteil: Hauptsächlich für Generierung konzipiert, Parsing-Funktion weniger umfangreich.
--> für uns ungeeignet

# Kobalt

Angelehnt an Javas Jsoup --> Ähnliche verwendung

Nachteil: Keine Multiplatform Unterstützung --> nur für Java Virtual Machine und Android 

--> Würde theoretisch für uns reichen, aber warum nicht gleich Ksoup?

# Krossbow

Ähnlich wie kotlin.html
Vorteile: 
- einfache API
- Mutliplatform
Nachteil:
- Parser wenig umfangreich
	- Parser für uns wichtigste Funktion 

# Eigene Implementierung

+ Voller Kontrolle bei einfachem Parsing
+ keine zusätzliche Abhängigkeit

Nachteil: 
- Aufwändig bei komplexem Parsing
- Möglicherweise weniger Robust
- **Zusätzlicher Aufwand + Aufwand kann schwer abgeschätzt werden

AI-Beispiel:

`class SimpleHtmlParser { 
`	private val tagPattern = "<(\\w+)[^>]*>(.*?)
	`</\\1>".toRegex(RegexOption.DOT_MATCHES_ALL)
	`private val attributePattern = "(\\w+)\\s*=\\s*\"([^\"]*)\"".toRegex() 

`	fun parse(html: String): HtmlElement { 
		`val root = HtmlElement("root", mutableMapOf(), mutableListOf()) 
		`parseRecursive(html, root) 
		`return root 
	`} 

	private fun parseRecursive(html: String, parent: HtmlElement) { 
		tagPattern.findAll(html).forEach { matchResult -> 
			val (tag, content) = matchResult.destructured 
			val attributes = parseAttributes(matchResult.value) 
			val element = HtmlElement(tag, attributes, mutableListOf()) 
			parent.children.add(element) 
			parseRecursive(content, element) 
		} 
	} 
...