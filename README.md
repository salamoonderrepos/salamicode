[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Static Badge](https://img.shields.io/badge/Current_Version-V1.7-green)
![Static Badge](https://img.shields.io/badge/SalamiCode-orange)
```
 _______  _______  ___      _______  __   __  ___   _______  _______  ______   _______ 
|       ||   _   ||   |    |   _   ||  |_|  ||   | |       ||       ||      | |       |
|  _____||  |_|  ||   |    |  |_|  ||       ||   | |       ||   _   ||  _    ||    ___|
| |_____ |       ||   |    |       ||       ||   | |       ||  | |  || | |   ||   |___
|_____  ||       ||   |___ |       ||       ||   | |      _||  |_|  || |_|   ||    ___|
 _____| ||   _   ||       ||   _   || ||_|| ||   | |     |_ |       ||       ||   |___
|_______||__| |__||_______||__| |__||_|   |_||___| |_______||_______||______| |_______|
```
(SalamiCode)

# What is this
SalamiCode is a programming language developed by Salamoonder. The language is meant to be an easy-to-learn language while still remaining somewhat powerful. SalamiCode offers many different types of features, like:
* Dynamic Types
* Assembly-like control flow, with `jump` and `label` statements.
* Automatic type promotion.
* High performance
* Interactive
* Object-oriented elements like classes and packages (planned).
* Able to be run on any system (JDK 17+)
> Simply create a file ending in `.salami`, and run it with the java program (look below).

SalamiCode started as a project intending to help me with my math assignments. I wanted a simple versatile language that would help me code formulas, so I could do my homework faster. The project begun on CodeHS on my school chromebook and transformed into a full-fledged language with IntelliJ IDEA. Thank you [tylerlaceby](https://www.youtube.com/channel/UCK94nZHoLfxXISrVflJqK5Q) on YouTube for giving me the knowledge needed to start this project.

## How to use (currently)
1. Download the source code.
2. Install JDK 17
3. Compile
4. Run `java Main <file>` (for default settings).
### Flags:
* `--repl` enables the repl for this language. Most statements do not work properly with this mode.
* `--silent` enables silent mode for a quieter console.
* `--monochrome` turns off ANSI coloring on text. Some consoles don't have support for ANSI, which is why this is an option. If your log messages look really cluttered, then try running with this flag.
* `--debug` runs with the debugger. Does not work with repl at the same time. The debugger can step through the program given to it.

## Documentation
I **HIGHLY** recommend visiting [the wikis](https://github.com/salamoonderrepos/salamicode/wiki). It contains a bunch of information for beginner's like langauge syntax, built-in methods, and advanced features for advanced users.

# Roadmap

## 1.9 *- ???*
- Package creation and implementation

## 1.8 *- ???*
- Custom object creation
- Classes
- Executable installer

## 1.7 (C) *- Attributes and Arrays*
Added arrays and indexing for strings and arrays using `from` statement. Access attributes of values using `to`.
- Tweaks to Lexer, Parser, and Interpreter.
- Debugger tool available with `--debug`.
- Pretty printing
- Some package work
- Main method rewrite
- CLI args changed to flags
- Better file validation

+ Arrays
+ Throw statement
+ Attributes
+ Indexing
+ Empty word(s)

## 1.6 *- Official Release*
First working version of SalamiCode. Basic commands.
- Tweaks to Lexer
- Tweaks to Parser
- Tweaks to Interpreter
- Added an initializer
- JavaDocs
- Logger and better CLI with color
- Section timing

+ Compare Statements
+ Jump Statements
+ Label Statements
+ Print Statements
+ Subroutine Declaration
+ Subroutine Calling
+ Method Calling

Added JavaDocs to some important functions.

## 1.5 *- Official Pre-Release*
Completed lexer, parser, and interpreter. Variables and runtime values are finished.
+ Complete Lexer
+ Complete Parser
+ Complete Interpreter
+ Variables
+ Early boolean logic
+ Strings and floats

## 1.4 *- Interpreter Started*
Finished full lexer.
+ Early Interpreter
+ Runtime
+ Repl introduction
+ Static lexer and parser

## 1.3 *- AST Tree Structure*
Rewrote parser to make an AST tree out of nodes.
+ Parser rewrite
+ Node structure started

## 1.2 *- Lexer Finished*
Finished full lexer.
+ Lexer

## 1.1 *- Early Lexer*
Added a dedicated lexer and parser for the language.
+ Basic Lexer
+ Parser

## 1.0 *- Initial*
Initial release. Basic calculator-like commands.
+ Main file

What is planned:
* Class declaration
* Class instantiation
* Variable references
* Decrement statement
* Package creating
* Package importing
* Screen package for making windows
* Perhaps double buffer animation?
* Keyboard package for interacting with keystrokes
* Math package for complex mathematical operations
* Executable download
* Global variables
* Static variables


> Licenced under MIT