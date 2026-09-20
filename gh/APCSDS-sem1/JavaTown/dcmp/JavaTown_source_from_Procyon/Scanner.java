import java.io.IOException;
import java.io.Reader;

// 
// Decompiled by Procyon v0.5.36
// 

public class Scanner
{
    private Reader in;
    private char ch;
    private String recent;
    
    public Scanner(final Reader in) {
        this.in = in;
        this.recent = "";
        this.advance();
    }
    
    private void advance() {
        try {
            final int read = this.in.read();
            if (read == -1) {
                this.ch = '\0';
            }
            else {
                this.ch = (char)read;
            }
            this.recent += this.ch;
            while (this.recent.length() > 100 && this.recent.indexOf("\n") != -1) {
                this.recent = this.recent.substring(this.recent.indexOf("\n") + 1);
            }
        }
        catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    private void eat(final char c) {
        if (this.ch == c) {
            this.advance();
            return;
        }
        throw new JavaTownParseException("expected " + c + " but found " + this.ch);
    }
    
    private boolean isLetter() {
        return (this.ch >= 'a' && this.ch <= 'z') || (this.ch >= 'A' && this.ch <= 'Z') || this.ch == '_';
    }
    
    private boolean isDigit() {
        return this.ch >= '0' && this.ch <= '9';
    }
    
    public String next() {
        while (this.ch == ' ' || this.ch == '\r' || this.ch == '\n' || this.ch == '\t') {
            this.eat(this.ch);
        }
        String str = "";
        if (this.ch == '/') {
            this.eat('/');
            if (this.ch == '/') {
                while (this.ch != '\n' && this.ch != '\0') {
                    this.eat(this.ch);
                }
                return this.next();
            }
            return "/";
        }
        else {
            if (this.isLetter()) {
                while (this.isLetter() || this.isDigit()) {
                    str += this.ch;
                    this.eat(this.ch);
                }
                return str;
            }
            if (this.isDigit()) {
                while (this.isDigit()) {
                    str += this.ch;
                    this.eat(this.ch);
                }
                return str;
            }
            if ("=!<>".indexOf(this.ch + "") != -1) {
                String str2 = str + this.ch;
                this.eat(this.ch);
                if (this.ch == '=') {
                    str2 += '=';
                    this.eat('=');
                }
                return str2;
            }
            if (this.ch == '|') {
                this.eat('|');
                this.eat('|');
                return "||";
            }
            if (this.ch == '&') {
                this.eat('&');
                this.eat('&');
                return "&&";
            }
            if (this.ch == '\0') {
                this.eat('\0');
                return " ";
            }
            if ("{};.(),+-*/%".indexOf("" + this.ch) != -1) {
                final String string = str + this.ch;
                this.eat(this.ch);
                return string;
            }
            if (this.ch != '\"') {
                throw new JavaTownParseException("invalid character " + this.ch);
            }
            String s = str + '\"';
            this.eat('\"');
            while (this.ch != '\"' && this.ch != '\0') {
                s += this.ch;
                this.eat(this.ch);
            }
            if (this.ch == '\0') {
                throw new JavaTownParseException("missing closing quotation mark on string");
            }
            final String string2 = s + '\"';
            this.eat('\"');
            return string2;
        }
    }
    
    public String getRecent() {
        return this.recent;
    }
}
