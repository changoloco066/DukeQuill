<img width="150" height="250" alt="DukeQuill Logo" src="https://github.com/user-attachments/assets/db2d7283-5c2b-4d61-814f-4e1a0398b3f1" />

# DukeQuill 🪶
 
Un corrector ortográfico y gramatical para el idioma español, desarrollado en Java. Inspirado en el sistema de marcado de errores de procesadores de texto como Word, DukeQuill analiza texto en español e identifica errores ortográficos, morfológicos y de puntuación.
 
El nombre es un homenaje a **Duke**, la mascota oficial de Java, combinado con **Quill** (pluma de escribir) — porque un buen escritor siempre tiene quien lo corrija.
 
---

## Ecosistema DukeQuill

| Repositorio | Descripción | Estado |
|---|---|---|
| **DukeQuill** (este repo) | Aplicación de escritorio Java/Swing | ✅ Activo |
| **[dukequill-api](https://github.com/changoloco066/dukequill-api)** | API REST con Spring Boot | ✅ Activo |
| **[dukequill-web](https://github.com/changoloco066/dukequill-web)** | Frontend web HTML/CSS/JS | 🚧 En desarrollo |

---

## ¿Qué hace DukeQuill?
 
DukeQuill recibe un texto en español y lo analiza en varias capas:
 
1. **Análisis léxico** — divide el texto en tokens (palabras, signos, números, espacios)
2. **Verificación ortográfica** — compara cada palabra contra un diccionario de ~80,000 palabras del español
3. **Análisis morfológico** — reconoce conjugaciones verbales y formas derivadas usando LanguageTool
4. **Reglas de puntuación** — valida el uso correcto de signos de interrogación, exclamación, espaciado, mayúsculas y puntos finales
5. **Concordancia de género** — detecta errores como "el casa" o "la perro"
6. **Detección de acentos** — identifica palabras con acento incorrecto según el contexto
---
 
## Lo que se ha logrado
**Motor de análisis**
- [x] Tokenizador (Lexer) completo para texto en español
- [x] Corrector ortográfico con soporte de mayúsculas y morfología
- [x] Análisis morfológico con LanguageTool para reconocer conjugaciones
- [x] Sugerencias de corrección con algoritmo Damerau-Levenshtein
- [x] Detección de acentos contextuales con LanguageTool
- [x] Concordancia de género (artículo + sustantivo)
**Reglas de puntuación**
- [x] Signos de interrogación (`¿` ... `?`) y exclamación (`¡` ... `!`)
- [x] Espaciado correcto antes y después de signos de puntuación
- [x] Mayúscula después de punto
- [x] Punto al final de oración
**Interfaz gráfica**
- [x] Editor de texto con subrayado en tiempo real (rojo = ortografía, azul = puntuación/gramática)
- [x] Tooltips con sugerencias al pasar el mouse sobre errores
- [x] Barra de menú (Archivo, Herramientas, Ver)
- [x] Panel lateral de diccionario personalizado (agregar/eliminar palabras a ignorar)
- [x] Importar archivos `.txt` y `.pdf` para análisis
- [x] Look and Feel nativo del sistema operativo
---
 
## Lo que viene
- [ ] Exportar resultados a archivo `.txt`
- [ ] Estadísticas del texto (palabras, oraciones, densidad de errores)
- [ ] Optimización de búsqueda con BK-Tree
- [ ] Detección de palabras confusables (`haber/a ver`, `sino/si no`)
---
 
## Estructura del proyecto
 
```
dukequill/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── dukequill/
│                   ├── lexer/              # Tokenizador del texto
│                   │   ├── Lexer.java
│                   │   ├── Token.java
│                   │   └── TokenType.java
│                   ├── dictionary/         # Carga y consulta del diccionario
│                   │   └── Dictionary.java
│                   ├── analyzer/           # Motor de análisis
│                   │   ├── SpellChecker.java
│                   │   ├── SpellErrors.java
│                   │   ├── MorphAnalyzer.java
│                   │   ├── AccentChecker.java
│                   │   ├── AccentViolations.java
│                   │   └── algorithms/
│                   │       ├── Levenshtein.java
│                   │       └── DamerauLevenshtein.java
│                   ├── rules/              # Motor de reglas de puntuación
│                   │   ├── Rule.java
│                   │   ├── RuleEngine.java
│                   │   ├── RuleViolation.java
│                   │   ├── InterrogationRule.java
│                   │   ├── ExclamationRule.java
│                   │   ├── SpacingRule.java
│                   │   ├── SpaceBeforePunctuationRule.java
│                   │   ├── UpperCaseRule.java
│                   │   ├── PeriodRule.java
│                   │   └── GenderAgreementRule.java
│                   └── gui/                # Interfaz gráfica
│                       └── MainWindow.java
└── src/
    └── resources/
        ├── dictionary/
        │   ├── Spanish.dic
        │   └── Spanish.aff
        └── icons/
```
 
---
 
## ¿Cómo funciona?
 
El texto pasa por tres etapas principales:
 
**1. Lexer** — divide `"¿Cómo estás?"` en tokens individuales:
```
¿  →  OPEN_PUNCTUATION_SIGN
Cómo  →  WORD
estás  →  WORD
?  →  CLOSE_PUNCTUATION_SIGN
```

**2. SpellChecker + MorphAnalyzer** — cada token de tipo `WORD` se verifica contra el diccionario. Si no existe, se consulta al analizador morfológico para saber si es una forma conjugada válida. Si tampoco lo reconoce, se registra como error y se generan sugerencias usando Damerau-Levenshtein.

**3. RuleEngine + AccentChecker** — aplica reglas de puntuación sobre los tokens y detecta errores de acentuación contextual con LanguageTool.

---
 
## Dependencias
 
| Dependencia | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| LanguageTool (`language-es`) | 6.3 | Morfología y detección de acentos |
| Apache PDFBox | 3.0.1 | Importación de archivos PDF |
| Hunspell (diccionario) | — | Diccionario base del español |
| Maven | 3.9+ | Gestión de dependencias |
 
---
 
## Cómo ejecutar
 
```bash
# Clonar el repositorio
git clone https://github.com/changoloco066/DukeQuill.git
cd DukeQuill/DukeQuill

# Compilar
mvn compile
 
# Ejecutar
mvn exec:java -Dexec.mainClass="com.dukequill.Main"
```
 
---
 
## Documentación

La documentación del proyecto está escrita en español. Cada módulo incluye comentarios Javadoc explicando las decisiones de diseño y el razonamiento detrás de cada implementación.

---

## Créditos y recursos externos

- **[Hunspell Spanish Dictionary](https://github.com/titoBouzout/Dictionaries)** — Diccionario ortográfico del español. Mantenido por titoBouzout.
- **[LanguageTool](https://languagetool.org/)** — Librería open source para análisis morfológico y gramatical del español.
- **[Apache PDFBox](https://pdfbox.apache.org/)** — Librería para extracción de texto de archivos PDF.
- **[Duke](https://wiki.openjdk.org/display/duke/Main)** — Mascota oficial de Java, inspiración del nombre y espíritu del proyecto.

---

*Desarrollado con Java 21 ☕*
