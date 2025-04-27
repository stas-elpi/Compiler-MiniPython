import java.io.*;
import java.util.Hashtable;

import minipython.lexer.Lexer;
import minipython.parser.Parser;
import minipython.node.Start;

public class ParserTest1
{
  public static void main(String[] args)
  {
    try
    {
      Parser parser =
        new Parser(
        new Lexer(
        new PushbackReader(
        new FileReader(args[0].toString()), 1024)));

      Start ast = parser.parse();
      Hashtable symtable =  new Hashtable();
      Hashtable symtable3 =  new Hashtable();
      Hashtable symtable1 =  new Hashtable();
      Hashtable symtable2 =  new Hashtable();
      
      ast.apply(new myvisitor1_5(symtable));
      ast.apply(new myvisitor2(symtable));
      ast.apply(new myvisitor3(symtable3));
      ast.apply(new TypeCheckVisitor4(symtable1));
      ast.apply(new TypeCheckVisitor4b(symtable1));
      ast.apply(new MyFunctionVisitor6(symtable1));
      ast.apply(new FunctionRedeclarationChecker7(symtable2));
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  }
}

