# Compiler MiniPython

In this project, a simple compiler for a subset of Python, called MiniPython, was developed using the SableCC tool. The project includes the stages of lexical analysis, syntactic analysis, abstract syntax tree creation, and semantic analysis.

# 1. Lexical Analysis

Lexical analysis is the first phase of translation, where the input text is split into lexical units (tokens) such as keywords, identifiers, and numbers.
The file LexerTest1.java is provided, containing a main method which calls the lexical analyzer and prints the execution results to the screen.

##Test procedure:
From the project directory, execute:

    sablecc minipython.grammar

After execution, a directory named minipython is created, containing the following four folders: analysis, lexer, node, and parser.
Then compile and run:

    javac LexerTest1.java
    java LexerTest1 minipythonexample.py

# 2. Syntactic Analysis

Syntactic analysis checks whether the sequence of tokens follows the grammatical rules of the language, creating a syntax tree.

The minipython.grammar file is extended by adding the Productions section with grammar rules, handling precedence issues where needed to resolve ambiguities (ambiguous grammar).
The file ParserTest1.java is provided, containing a main method that calls the syntactic analyzer and prints the results.

Again, from the project root directory:

    sablecc minipython.grammar
    javac ParserTest1.java
    java ParserTest1 minipythonexample.py

# 3. Abstract Syntax Tree (AST) Creation
   
The goal of building an abstract syntax tree is to discard unnecessary elements and nodes from the syntax tree that are no longer needed for the subsequent compilation stages. Up to the syntactic analysis phase, specific tokens and grammar levels were needed to enforce precedence and resolve potential conflicts. After the syntactic structure has been determined, these extra details are no longer necessary.

Procedure:

    sablecc minipython.grammar
    javac ASTTest1.java
    java ASTTest1 example.py

(The example.py file is included.)
The output now shows the abstract syntax tree structure.

# 4.Symbol Table and Semantic Analysis
   
A symbol table is created to store necessary information for semantic analysis, such as methods and variables. The symbol table is then used by a type checker. The checking process is performed on the already constructed abstract syntax tree (AST).

The tree is traversed using Visitor patterns implemented in Java.
The purpose of this phase is to detect and report semantic errors by displaying appropriate error messages.

Types of errors that can be detected:

a)	Use of undeclared variable, e.g.:

    def add(x, y):
     return x + y
    print(k)

A variable is considered declared if a value has been assigned to it or if it is used as a function parameter. All variables are considered global.

The following code is incorrect because the variable is used before it is declared:

     def add(x, y):
     return x + y
     print(k)
     k = 0
 
b)	Call of an undeclared function (a function may be used before its declaration).

c)	Incorrect number of arguments in a function call.

The following code is correct:

    def add(x, y=2):
        return x + y 
    print(add(1))
d) Use of a variable of an incompatible type, e.g., using a string as an integer:

    x = "hello world"
    print(x + 2)
    
The following code is incorrect:

    def add(x, y):
        return x + y
    k = "hello world"
    print(add(2, k))

e)	Arithmetic operations where None is used as an operand.

f)	Incorrect use of a function's return value, e.g.:

    def add(x, y):
        return "hello world"
    print(add(2, 1) + 2)

g) Duplicate function declarations with the same number of arguments.

If a function is declared with 2 arguments and another with the same name but 3 arguments where one has a default value, it is still considered an error.
The following declarations are incorrect:

    def add(x, y):
    def add(x, y, z=1)
Also, the following declarations are incorrect:

    def add(x, y, z):
    def add(x, y, z=1)
