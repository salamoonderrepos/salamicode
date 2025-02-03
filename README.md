# What is this
SalamiCode is a programming language developed by Salamoonder. The language is meant to be an easy-to-learn language while still remaining somewhat powerfull. SalamiCode offers many different types of features like:
* Dynamic Types
* Assembly-like, with `jump` and `label` statements.
* Automatic type promotion.
* Fast
* Screen, math, and keyboard interactions.
* Object-oriented elements like classes and packages.
* Able to be ran on any system (with the proper JDK installed)
Simply create a file ending in `.salami`, and run it with the java program (look below).

SalamiCode started as a project intending to help me with my math assignments. I wanted a simple formula creator and editor so I could make my own calculator with custom functions. I started coding on my school chromebook then transferred over to my home computer with IntelliJ Idea. Thank you tylerlaceby on YouTube for giving me a headstart.

## How to use (currently)
I have no idea how to build this into an executable or what that would even look like, but here is a way you can try it on your own:
1. Download the source code.
2. Make sure you have JDK 17 installed
3. Run `java Main <fileLocation> <enableRepl> <silent> <color>`

Flags:
* `<fileLocation>` is the location of the file in the system.
* `<enableRepl>` enables the repl for this language. Most statements do not work properly with this mode.
* `<silent>` toggles whether it should silent the loggers from inside the program. (Does not silent the runtime output, or where print statements print to)
* `<color>` toggles whether it should log with color or not. It uses ANSI coloring which doesn't always work on some consoles.

* 
### Repl?
A terminal for SalamiCode so you can test it without having to make a new file. Currently the repl does not offer proper support for many of the statements post 1.2 so run this with caution.

### File?

Inside of the `assets/source` folder, there's a file called `test.salami` which I use personally to test the language. You can make a .salami file anywhere as long as the path matches where the file is located.

## Wikis
I **HIGHLY** reccomend visiting the wikis. It has a lot of information on how to get started with this language.

# How it works

## Lexer
The lexer take in raw text data and converts it into a list of tokens for the parser. These tokens have a type and a value. Take this token: `[OP: "+"]`. It has a type, being of type `OP`, and a value, which is the `+` operator. As of right now, this list of tokens is just another way to write the same file we already have, so we pass this list into the Parser.

## Parser
The parser works on an AST node tree system. This seems daunting at first but it's actually really simple to understand. Each "node" is a type of statement or expression that can be evaluated in the future. For example:  
`[SET: "set"] [ID: "x"] [TO: "to"] [NUM: "13"]`
This list of tokens can be converted into a `SetStatementNode`
```
SetStatementNode{
  ID: X
  VALUE: NUMBER(13)
}
```
Again at this point this means nothing to the computer, because we need to define how this computer is supposed to evaluate this node.

## Interpreter
A list of AST nodes is passed into the interpreter to begin interpreting. It runs each node after the other which is what makes the jump and label statements possile. Things like the `SetStatementNode` can be defined to declare a variable in the current scope.

# Roadmap
Current features:
* Logical operations
* Numeric operations
* Variable declaration
* Subroutine declaration
* Subroutine calling
* Method calling
* Jump statement
* Label statement
* Compare statement
* Booleans
* Floats
* Strings
* String operations
* Proper environment handling

What is planned:
* Class declaration
* Class instantiation
* Variable references
* Arrays
* Decrement statement
* Package creating
* Package importing
* Screen package for making windows
* Perhaps double buffer animation?
* Keyboard package for interacting with keystrokes
* Math package for complex mathmatical operations
* Executable download
* Global variables
* Static variables


