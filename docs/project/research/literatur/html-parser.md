# Ksoup
> Es gibt unterschiedliche implementierung von Ksoup
> https://github.com/fleeksoft/ksoup
> https://github.com/MohamedRejeb/Ksoup

Eine Kotlin Multiplatform-Bibliothek zum Arbeiten mit HTML & XML.

**Einbinden mit:**

`implementation("com.fleeksoft.ksoup:ksoup:<version>")`

**Beispiel Verwendung:**

	val html = "<html><head><title>Beispiel</title></head><body>Hallo Welt</body></html>"
	val doc: Document = Ksoup.parse(html = html)
	println("Titel: ${doc.title()}") // Ausgabe: Titel: Beispiel
	println("Body Text: ${doc.body().text()}") // Ausgabe: Body Text: Hallo Welt


**Hauptfunktionen**

 - HTML von URLs, Dateien oder Strings scrapen und parsen
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

# Skrape{it}

https://github.com/skrapeit/skrape.it

- Integriert sich nahtlos mit anderen Kotlin Bibliotheken
- legt großen Wert auf DSL -> einfaches Lesen des Codes
- mehr Flexibilität
- gute Doku

# Kobalt

Angelehnt an Javas Jsoup --> Ähnliche verwendung

Nachteil: Keine Multiplatform Unterstützung --> nur für Java Virtual Machine und Android 

--> Würde theoretisch für uns reichen, aber warum nicht gleich Ksoup?

Wird von Googles Cobalt genutzt --> nicht für uns geeignet

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
- **Zusätzlicher Aufwand + Aufwand kann schwer abgeschätzt werden**

AI-Beispiel:

	class SimpleHtmlParser {
    private val tagPattern = "<(\\w+)[^>]*>(.*?)</\\1>".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val attributePattern = "(\\w+)\\s*=\\s*\"([^\"]*)\"".toRegex()


    fun parse(html: String): HtmlElement {
        val root = HtmlElement("root", mutableMapOf(), mutableListOf())
        parseRecursive(html, root)
        return root
    }

    private fun parseRecursive(html: String, parent: HtmlElement) {
        tagPattern.findAll(html).forEach { matchResult ->
            val (tag, content) = matchResult.destructured
            val attributes = parseAttributes(matchResult.value)
            val element = HtmlElement(tag, attributes, mutableListOf())
            parent.children.add(element)
            parseRecursive(content, element)
        }
    }

    private fun parseAttributes(tag: String): MutableMap<String, String> {
        return attributePattern.findAll(tag)
            .associate { it.groupValues[1] to it.groupValues[2] }
            .toMutableMap()
    }
	}

	data class HtmlElement(
    val tag: String,
    val attributes: MutableMap<String, String>,
    val children: MutableList<HtmlElement>
	) {
 
    fun getText(): String {
        return if (children.isEmpty()) {
            attributes["text"] ?: ""
        } else {
            children.joinToString("") { it.getText() }
        }
    }


    fun getElementsByTag(tagName: String): List<HtmlElement> {
        return if (tag.equals(tagName, ignoreCase = true)) {
            listOf(this)
        } else {
            children.flatMap { it.getElementsByTag(tagName) }
        }
    }
	}

	fun main() {
    val html = """
        <html>
            <head>
                <title>Beispielseite</title>
            </head>
            <body>
                <h1>Willkommen</h1>
                <p>Dies ist ein <a href="https://example.com">Link</a>.</p>
                <ul>
                    <li>Element 1</li>
                    <li>Element 2</li>
                </ul>
            </body>
        </html>
    """.trimIndent()

    val parser = SimpleHtmlParser()
    val rootElement = parser.parse(html)

    // Beispiele für die Verwendung
    println("Titel: ${rootElement.getElementsByTag("title").firstOrNull()?.getText()}")
    println("Alle Links:")
    rootElement.getElementsByTag("a").forEach { link ->
        println("- Text: ${link.getText()}, Href: ${link.attributes["href"]}")
    }
    println("Listenpunkte:")
    rootElement.getElementsByTag("li").forEach { item ->
        println("- ${item.getText()}")
    }
	}
