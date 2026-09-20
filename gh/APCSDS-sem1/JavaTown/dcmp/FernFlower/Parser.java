import java.util.ArrayList;
import java.util.Stack;

public class Parser {

   private Scanner scanner;
   private String token;
   private Stack stack;


   public Parser(Scanner var1) {
      this.scanner = var1;
      this.stack = new Stack();
      this.token = var1.next();
   }

   public ArrayList parseStatements() {
      ArrayList var1 = new ArrayList();

      while(!this.token.equals(" ")) {
         var1.add(this.parseStatement());
      }

      return var1;
   }

   private void eat(String var1) {
      if(!this.token.equals(var1) && (!this.token.equals(" ") || !var1.equals(";"))) {
         throw new JavaTownParseException("expected " + var1 + " but found " + this.token);
      } else {
         if(this.stack.isEmpty()) {
            this.token = this.scanner.next();
         } else {
            this.token = (String)this.stack.pop();
         }

      }
   }

   private void barf(String var1) {
      this.stack.push(this.token);
      this.token = var1;
   }

   private Statement parseStatement() {
      System.out.println("parseStatement:  " + this.token);
      String var1;
      if(this.token.equals("public")) {
         this.eat("public");
         this.eat("class");
         var1 = this.token;
         this.eat(var1);
         this.eat("{");
         ArrayList var7 = this.parseDeclarations();
         this.eat("}");
         return new Definition(var1, var7);
      } else if(this.token.equals("return")) {
         this.eat("return");
         Expression var6 = this.parseExpression();
         this.eat(";");
         return new Return(var6);
      } else if(this.token.equals("if")) {
         return this.parseIf();
      } else if(this.token.equals("{")) {
         return this.parseBlock();
      } else {
         var1 = this.token;
         this.eat(var1);
         if(this.token.equals("=")) {
            this.eat("=");
            Expression var8 = this.parseExpression();
            this.eat(";");
            return new Assignment(var1, var8);
         } else {
            String var2 = this.token;
            this.eat(var2);
            if(this.token.equals("=")) {
               this.eat("=");
               Expression var4 = this.parseExpression();
               this.eat(";");
               return new Assignment(var2, var4);
            } else {
               this.barf(var2);
               this.barf(var1);
               System.out.println(this.token + " " + this.stack);

               try {
                  Invocation var3 = (Invocation)this.parseExpression();
                  this.eat(";");
                  return var3;
               } catch (ClassCastException var5) {
                  var5.printStackTrace();
                  throw new JavaTownParseException("invalid syntax");
               }
            }
         }
      }
   }

   private ArrayList parseDeclarations() {
      ArrayList var1 = new ArrayList();

      while(!this.token.equals("}")) {
         var1.add(this.parseDeclaration());
      }

      return var1;
   }

   private Declaration parseDeclaration() {
      String var1 = this.token;
      this.eat(var1);
      String var2 = this.token;
      this.eat(var2);
      String var3;
      if(!this.token.equals(";") && !this.token.equals("(")) {
         var3 = this.token;
         this.eat(var3);
      } else {
         var3 = var2;
      }

      if(!this.token.equals("(")) {
         if(!var1.equals("private")) {
            throw new JavaTownParseException("expected private but found " + this.token);
         } else {
            this.eat(";");
            return new Field(var3);
         }
      } else if(!var1.equals("public") && !var1.equals("private")) {
         throw new JavaTownParseException("illegal start of declaration:  " + var1);
      } else {
         this.eat("(");
         ArrayList var4 = new ArrayList();

         while(!this.token.equals(")")) {
            var2 = this.token;
            this.eat(var2);
            if(!this.token.equals(",") && !this.token.equals(")")) {
               var4.add(this.token);
               this.eat(this.token);
            } else {
               var4.add(var2);
            }

            if(this.token.equals(",")) {
               this.eat(",");
            } else if(!this.token.equals(")")) {
               throw new JavaTownParseException("expected ) but found " + this.token);
            }
         }

         this.eat(")");
         return new Method(var1.equals("public")?0:1, var3, var4, this.parseBlock());
      }
   }

   private Block parseBlock() {
      this.eat("{");
      ArrayList var1 = new ArrayList();

      while(!this.token.equals("}")) {
         var1.add(this.parseStatement());
      }

      this.eat("}");
      return new Block(var1);
   }

   private Expression parseExpression() {
      Object var1;
      for(var1 = this.parseAnded(); this.token.equals("||"); var1 = new BinOp('|', (Expression)var1, this.parseAnded())) {
         this.eat("||");
      }

      return (Expression)var1;
   }

   private Expression parseAnded() {
      Object var1;
      for(var1 = this.parsePossibleEquality(); this.token.equals("&&"); var1 = new BinOp('&', (Expression)var1, this.parsePossibleEquality())) {
         this.eat("&&");
      }

      return (Expression)var1;
   }

   private Expression parsePossibleEquality() {
      Expression var1 = this.parsePossibleComparison();
      if(this.token.equals("==")) {
         this.eat("==");
         return new BinOp('=', var1, this.parsePossibleComparison());
      } else if(this.token.equals("!=")) {
         this.eat("!=");
         return new BinOp('!', var1, this.parsePossibleComparison());
      } else {
         return var1;
      }
   }

   private Expression parsePossibleComparison() {
      Expression var1 = this.parsePossibleSum();
      if(this.token.equals("<")) {
         this.eat("<");
         return new BinOp('<', var1, this.parsePossibleSum());
      } else if(this.token.equals(">")) {
         this.eat(">");
         return new BinOp('>', var1, this.parsePossibleSum());
      } else if(this.token.equals("<=")) {
         this.eat("<=");
         return new BinOp('[', var1, this.parsePossibleSum());
      } else if(this.token.equals(">=")) {
         this.eat(">=");
         return new BinOp(']', var1, this.parsePossibleSum());
      } else {
         return var1;
      }
   }

   private ArrayList parseArguments() {
      this.eat("(");
      ArrayList var1 = new ArrayList();

      while(!this.token.equals(")")) {
         var1.add(this.parseExpression());
         if(this.token.equals(",")) {
            this.eat(",");
         } else if(!this.token.equals(")")) {
            throw new JavaTownParseException("expected ) but found " + this.token);
         }
      }

      this.eat(")");
      return var1;
   }

   private Expression parsePossibleSum() {
      Object var1 = this.parseTerm();

      while(this.token.equals("+") || this.token.equals("-")) {
         if(this.token.equals("+")) {
            this.eat("+");
            var1 = new BinOp('+', (Expression)var1, this.parseTerm());
         } else {
            this.eat("-");
            var1 = new BinOp('-', (Expression)var1, this.parseTerm());
         }
      }

      return (Expression)var1;
   }

   private Expression parseTerm() {
      Object var1 = this.parseFactor();

      while(this.token.equals("*") || this.token.equals("/") || this.token.equals("%")) {
         if(this.token.equals("*")) {
            this.eat("*");
            var1 = new BinOp('*', (Expression)var1, this.parseFactor());
         } else if(this.token.equals("/")) {
            this.eat("/");
            var1 = new BinOp('/', (Expression)var1, this.parseFactor());
         } else {
            this.eat("%");
            var1 = new BinOp('%', (Expression)var1, this.parseFactor());
         }
      }

      return (Expression)var1;
   }

   private Expression parseFactor() {
      if(this.token.equals("-")) {
         this.eat("-");
         return new BinOp('-', new IntExp(0), this.parseFactor());
      } else if(this.token.equals("!")) {
         this.eat("!");
         return new Not(this.parseFactor());
      } else if(this.token.equals("new")) {
         this.eat("new");
         String var1 = this.token;
         this.eat(var1);
         return new Instantiation(var1, this.parseArguments());
      } else {
         return this.parseAtom();
      }
   }

   private Expression parseAtom() {
      if(this.token.equals("null")) {
         this.eat("null");
         return new Null();
      } else if(this.token.equals("true")) {
         this.eat("true");
         return new BoolExp(true);
      } else if(this.token.equals("false")) {
         this.eat("false");
         return new BoolExp(false);
      } else if(this.token.startsWith("\"")) {
         String var3 = this.token;
         this.eat(var3);
         return new StringExp(var3.substring(1, var3.length() - 1));
      } else {
         try {
            int var1 = Integer.parseInt(this.token);
            this.eat(this.token);
            return new IntExp(var1);
         } catch (NumberFormatException var2) {
            return this.parseInvocation();
         }
      }
   }

   public Expression parseInvocation() {
      Object var1;
      String var2;
      ArrayList var3;
      for(var1 = this.parseVariable(); this.token.equals("."); var1 = new Invocation((Expression)var1, var2, var3)) {
         this.eat(".");
         var2 = this.token;
         this.eat(var2);
         var3 = this.parseArguments();
      }

      return (Expression)var1;
   }

   public Expression parseVariable() {
      if(this.token.equals("(")) {
         this.eat("(");
         Expression var2 = this.parseExpression();
         this.eat(")");
         return var2;
      } else if(this.token.equals("this")) {
         this.eat("this");
         return new This();
      } else {
         String var1 = this.token;
         this.eat(var1);
         return new Variable(var1);
      }
   }

   public If parseIf() {
      this.eat("if");
      this.eat("(");
      Expression var1 = this.parseExpression();
      this.eat(")");
      Statement var2 = this.parseStatement();
      Object var3;
      if(this.token.equals("else")) {
         this.eat("else");
         var3 = this.parseStatement();
      } else {
         var3 = new Block(new ArrayList());
      }

      return new If(var1, var2, (Statement)var3);
   }
}
