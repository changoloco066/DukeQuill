<img width="150" height="250" alt="Wave" src="https://github.com/user-attachments/assets/db2d7283-5c2b-4d61-814f-4e1a0398b3f1" />

# DukeQuill 🪶

Un corrector ortográfico y gramatical para el idioma español, desarrollado en Java. Inspirado en el sistema de marcado de errores de procesadores de texto como Word, DukeQuill analiza texto en español e identifica errores ortográficos, morfológicos y de puntuación.

El nombre es un homenaje a **Duke**, la mascota oficial de Java, combinado con **Quill** (pluma de escribir) — porque un buen escritor siempre tiene quien lo corrija.

---

## ¿Qué hace DukeQuill?

DukeQuill recibe un texto en español y lo analiza en varias capas:

1. **Análisis léxico** — divide el texto en tokens (palabras, signos, números, espacios)
2. **Verificación ortográfica** — compara cada palabra contra un diccionario de ~80,000 palabras del español
3. **Análisis morfológico** — reconoce conjugaciones verbales y formas derivadas usando LanguageTool
4. **Reglas de puntuación** — valida el uso correcto de signos de interrogación, exclamación y espaciado

---

## Lo que se ha logrado

- [x] Tokenizador (Lexer) completo para texto en español
- [x] Carga del diccionario Hunspell (`es_ES.dic`) en memoria con `HashSet` para búsqueda O(1)
- [x] Corrector ortográfico (`SpellChecker`) con soporte de mayúsculas
- [x] Análisis morfológico con LanguageTool para reconocer conjugaciones
- [x] Reglas de puntuación:
  - [x] Signos de interrogación (`¿` ... `?`)
  - [x] Signos de exclamación (`¡` ... `!`)
  - [x] Espaciado después de signos de puntuación
- [x] Interfaz gráfica (Swing) con:
  - [x] Editor de texto
  - [x] Tabla de errores ortográficos
  - [x] Tabla de violaciones de reglas de puntuación
- [x] Mayúscula después de punto
- [x] Detección de espacio antes de puntuación
- [x] Punto al final de oración
- [x] Subrayado en tiempo real (rojo = ortografía, azul = puntuación)
- [ ] Auto corrector de palabras (usando el algortimo de Levenshtein distance)
- [ ] Palabras confusables (`haber/a ver`, `sino/si no`, `porque/por qué`)

---

## Lo que viene

- [ ] Exportar resultados a archivo `.txt`
- [ ] Estadísticas del texto (palabras, oraciones, densidad de errores)
- [ ] API REST con Spring Boot para versión web

---

## Estructura del proyecto

```
dukequill/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── dukequill/
│                   ├── lexer/          # Tokenizador del texto
│                   │   ├── Lexer.java
│                   │   ├── Token.java
│                   │   └── TokenType.java
│                   ├── dictionary/     # Carga y consulta del diccionario
│                   │   └── Dictionary.java
│                   ├── analyzer/       # Motor de análisis ortográfico
│                   │   ├── SpellChecker.java
│                   │   ├── SpellErrors.java
│                   │   └── MorphAnalyzer.java
│                   ├── rules/          # Motor de reglas de puntuación
│                   │   ├── Rule.java
│                   │   ├── RuleEngine.java
│                   │   ├── RuleViolation.java
│                   │   ├── InterrogationRule.java
│                   │   ├── ExclamationRule.java
│                   │   └── SpacingRule.java
│                   └── gui/            # Interfaz gráfica
│                       └── MainWindow.java
└── src/
    └── resources/
        └── dictionary/
            ├── Spanish.dic
            └── Spanish.aff
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

**2. SpellChecker + MorphAnalyzer** — cada token de tipo `WORD` se verifica contra el diccionario. Si no existe, se consulta al analizador morfológico para saber si es una forma conjugada válida. Si tampoco lo reconoce, se registra como error.

**3. RuleEngine** — aplica reglas de puntuación sobre la secuencia de tokens para detectar violaciones como signos sin cerrar o falta de espacios.

---

## Dependencias

| Dependencia | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| LanguageTool (`language-es`) | 6.3 | Análisis morfológico del español |
| Hunspell (diccionario) | — | Diccionario base del español |
| Maven | 3.9+ | Gestión de dependencias |

---

## Cómo ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/tuusuario/dukequill.git
cd dukequill

# Compilar
mvn compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.dukequill.Main"
```

---

## Documentación

La documentación del proyecto está escrita en español. Cada módulo incluye comentarios explicando las decisiones de diseño y el razonamiento detrás de cada implementación.

---

## Créditos y recursos externos

Este proyecto hace uso de los siguientes recursos de terceros:

- **[Hunspell Spanish Dictionary](https://github.com/titoBouzout/Dictionaries)** — Diccionario ortográfico del español utilizado como base de datos de palabras válidas. Mantenido por titoBouzout.

- **[LanguageTool](https://languagetool.org/)** — Librería de código abierto para análisis morfológico y gramatical del español. Utilizada para reconocer conjugaciones verbales y formas derivadas.

- **[Duke](https://wiki.openjdk.org/display/duke/Main)** — Mascota oficial de Java, inspiración del nombre y espíritu del proyecto.

---

*Desarrollado con Java 21 *
