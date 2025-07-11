[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Static Badge](https://img.shields.io/badge/Current_Version-V1.8-green)
![Static Badge](https://img.shields.io/badge/SalamiCode-orange)


```
   .-'''-.    ____      .---.        ____    ,---.    ,---..-./`)     _______      ,-----.     ______         .-''-.   
  / _     \ .'  __ `.   | ,_|      .'  __ `. |    \  /    |\ .-.')   /   __  \   .'  .-,  '.  |    _ `''.   .'_ _   \  
 (`' )/`--'/   '  \  \,-./  )     /   '  \  \|  ,  \/  ,  |/ `-' \  | ,_/  \__) / ,-.|  \ _ \ | _ | ) _  \ / ( ` )   ' 
(_ o _).   |___|  /  |\  '_ '`)   |___|  /  ||  |\_   /|  | `-'`"`,-./  )      ;  \  '_ /  | :|( ''_'  ) |. (_ o _)  | 
 (_,_). '.    _.-`   | > (_)  )      _.-`   ||  _( )_/ |  | .---. \  '_ '`)    |  _`,/ \ _/  || . (_) `. ||  (_,_)___| 
.---.  \  :.'   _    |(  .  .-'   .'   _    || (_ o _) |  | |   |  > (_)  )  __: (  '\_/ \   ;|(_    ._) ''  \   .---. 
\    `-'  ||  _( )_  | `-'`-'|___ |  _( )_  ||  (_,_)  |  | |   | (  .  .-'_/  )\ `"/  \  ) / |  (_.\.' /  \  `-'    / 
 \       / \ (_ o _) /  |        \\ (_ o _) /|  |      |  | |   |  `-'`-'     /  '. \_/``".'  |       .'    \       /  
  `-...-'   '.(_,_).'   `--------` '.(_,_).' '--'      '--' '---'    `._____.'     '-----'    '-----'`       `'-..-'   
                                                                                                                       
```
(SalamiCode)

# What is this
SalamiCode is a programming language developed by Salamoonder. The language is meant to be an easy-to-learn language while still remaining somewhat powerful. SalamiCode offers many different types of features, like:
* Dynamic Types
* Assembly-like control flow, with `jump` and `label` statements.
* Automatic type promotion.
* High performance
* Interactive
* User package creation and support
* Able to be run on any system
> Simply create a file ending in `.salami`, and run it with the java program (look below).

SalamiCode started as a project intending to help me with my math assignments. I wanted a simple, versatile language that would help me code formulas so I could do my homework faster. The project begun on CodeHS on my school Chromebook and transformed into a full-fledged language with IntelliJ IDEA. Thank you [tylerlaceby](https://www.youtube.com/channel/UCK94nZHoLfxXISrVflJqK5Q) on YouTube for giving me the knowledge needed to start this project.

## How to install

### Archive
1. Download whichever SalamiCode archive you need
2. Unzip
3. Use your command line to run use the associated `.sh` and `.bat` files

> You could optionally, for Windows, add the `salamicode.bat` file to your PATH.

*You don't need to install Java with the archive. It already comes with it*

### Source
1. Download the source code.
2. Install JDK 17 or some other JRE (17+). (only needs `java.base`)
3. Build
4. Run `java Main <file>` (for default settings).
> You can add flags after `<file>`
### Flags:
* `--file <path-to-file>` supplies the file to the program.
* `--repl` enables the repl for this language. Most statements do not work properly with this mode.
* `--silent` enables silent mode for a quieter console.
* `--monochrome` turns off ANSI coloring on text. Some consoles don't have support for ANSI, which is why this is an option. If your log messages look really cluttered, then try running with this flag.
* `--debug` runs with the debugger. Does not work with repl at the same time. The debugger can step through the program given to it.
* `--nopretty` runs without making object prints pretty

## Documentation
I **HIGHLY** recommend visiting [the wikis](https://github.com/salamoonderrepos/salamicode/wiki). It contains a bunch of information for beginner's like langauge syntax, built-in methods, and advanced features for advanced users.

# Roadmap

## 1.9 *- ???*
- Modules
- Some other statements
- Dictionary?

## 1.8 (C) *- Packages*
Added support for packages and the `port` statement. User created
packages are now supported, and you can learn how to make one yourself in the wikis.
* Lexer changes
    * Strings support escaped quotes and newlines
    * Identifiers support ASCII characters
* Package support
* Major backend refactoring
  * Switched back to static Interpreter
  * Each object requests data from eachother instead of being instances individually
* `++` operator now acts as intended. (Adding one after returning)
  * `++` is now an expression rather than a statement. Meaning you can use it in `comp` or a binary expression.
* Port statement supports porting other `.sal` and `.salami` files
* Console logging actively supports log locations
  * `print` and `throw` respectively
* Changed `--nolint` to `--nopretty` to better represent what the flag does
* To supply a file, use `--file <FILE>` instead.
* Errors now properly point to locations within a file.
* Archive distribution (Bundled JRE)

BUGFIXES -
* Fixed bug where methods would skip parent lookup making methods unable to be called inside subroutines.
* Fixed bug where lexer would jot down incorrect values for token locations
* Improved compatibility with Linux
* Couple tweaks to CLI argument parsing

JavaDocs will be written through versions.

## 1.7 *- Attributes and Arrays*
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
* Variable references
* Decrement statement
* Screen package for making windows
* Keyboard package for interacting with keystrokes
* Math package for complex mathematical operations
* Executable download
* Global variables
* Modules


> Licenced under MIT