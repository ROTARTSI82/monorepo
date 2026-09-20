import java.io.IOException;
import java.io.Reader;

public class Scanner {

   private Reader in;
   private char ch;
   private String recent;


   public Scanner(Reader var1) {
      this.in = var1;
      this.recent = "";
      this.advance();
   }

   private void advance() {
      try {
         int var1 = this.in.read();
         if(var1 == -1) {
            this.ch = 0;
         } else {
            this.ch = (char)var1;
         }

         for(this.recent = this.recent + this.ch; this.recent.length() > 100 && this.recent.indexOf("\n") != -1; this.recent = this.recent.substring(this.recent.indexOf("\n") + 1)) {
            ;
         }

      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private void eat(char var1) {
      if(this.ch == var1) {
         this.advance();
      } else {
         throw new JavaTownParseException("expected " + var1 + " but found " + this.ch);
      }
   }

   private boolean isLetter() {
      return this.ch >= 97 && this.ch <= 122 || this.ch >= 65 && this.ch <= 90 || this.ch == 95;
   }

   private boolean isDigit() {
      return this.ch >= 48 && this.ch <= 57;
   }

   public String next() {
      while(this.ch == 32 || this.ch == 13 || this.ch == 10 || this.ch == 9) {
         this.eat(this.ch);
      }

      String var1 = "";
      if(this.ch == 47) {
         this.eat('/');
         if(this.ch != 47) {
            return "/";
         } else {
            while(this.ch != 10 && this.ch != 0) {
               this.eat(this.ch);
            }

            return this.next();
         }
      } else if(this.isLetter()) {
         while(this.isLetter() || this.isDigit()) {
            var1 = var1 + this.ch;
            this.eat(this.ch);
         }

         return var1;
      } else if(this.isDigit()) {
         while(this.isDigit()) {
            var1 = var1 + this.ch;
            this.eat(this.ch);
         }

         return var1;
      } else if("=!<>".indexOf(this.ch + "") != -1) {
         var1 = var1 + this.ch;
         this.eat(this.ch);
         if(this.ch == 61) {
            var1 = var1 + '=';
            this.eat('=');
         }

         return var1;
      } else if(this.ch == 124) {
         this.eat('|');
         this.eat('|');
         return "||";
      } else if(this.ch == 38) {
         this.eat('&');
         this.eat('&');
         return "&&";
      } else if(this.ch == 0) {
         this.eat('\u0000');
         return " ";
      } else if("{};.(),+-*/%".indexOf("" + this.ch) != -1) {
         var1 = var1 + this.ch;
         this.eat(this.ch);
         return var1;
      } else if(this.ch != 34) {
         throw new JavaTownParseException("invalid character " + this.ch);
      } else {
         var1 = var1 + '\"';
         this.eat('\"');

         while(this.ch != 34 && this.ch != 0) {
            var1 = var1 + this.ch;
            this.eat(this.ch);
         }

         if(this.ch == 0) {
            throw new JavaTownParseException("missing closing quotation mark on string");
         } else {
            var1 = var1 + '\"';
            this.eat('\"');
            return var1;
         }
      }
   }

   public String getRecent() {
      return this.recent;
   }
}
