/*
 * Decompiled with CFR 0.150.
 */
import java.io.IOException;
import java.io.Reader;

public class Scanner {
    private Reader in;
    private char ch;
    private String recent;

    public Scanner(Reader reader) {
        this.in = reader;
        this.recent = "";
        this.advance();
    }

    private void advance() {
        try {
            int n = this.in.read();
            this.ch = n == -1 ? (char)'\u0000' : (char)n;
            this.recent = this.recent + this.ch;
            while (this.recent.length() > 100 && this.recent.indexOf("\n") != -1) {
                this.recent = this.recent.substring(this.recent.indexOf("\n") + 1);
            }
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    private void eat(char c) {
        if (this.ch != c) {
            throw new JavaTownParseException("expected " + c + " but found " + this.ch);
        }
        this.advance();
    }

    private boolean isLetter() {
        return this.ch >= 'a' && this.ch <= 'z' || this.ch >= 'A' && this.ch <= 'Z' || this.ch == '_';
    }

    private boolean isDigit() {
        return this.ch >= '0' && this.ch <= '9';
    }

    public String next() {
        while (this.ch == ' ' || this.ch == '\r' || this.ch == '\n' || this.ch == '\t') {
            this.eat(this.ch);
        }
        String string = "";
        if (this.ch == '/') {
            this.eat('/');
            if (this.ch == '/') {
                while (this.ch != '\n' && this.ch != '\u0000') {
                    this.eat(this.ch);
                }
                return this.next();
            }
            return "/";
        }
        if (this.isLetter()) {
            while (this.isLetter() || this.isDigit()) {
                string = string + this.ch;
                this.eat(this.ch);
            }
            return string;
        }
        if (this.isDigit()) {
            while (this.isDigit()) {
                string = string + this.ch;
                this.eat(this.ch);
            }
            return string;
        }
        if ("=!<>".indexOf(this.ch + "") != -1) {
            string = string + this.ch;
            this.eat(this.ch);
            if (this.ch == '=') {
                string = string + '=';
                this.eat('=');
            }
            return string;
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
        if (this.ch == '\u0000') {
            this.eat('\u0000');
            return " ";
        }
        if ("{};.(),+-*/%".indexOf("" + this.ch) != -1) {
            string = string + this.ch;
            this.eat(this.ch);
            return string;
        }
        if (this.ch == '\"') {
            string = string + '\"';
            this.eat('\"');
            while (this.ch != '\"' && this.ch != '\u0000') {
                string = string + this.ch;
                this.eat(this.ch);
            }
            if (this.ch == '\u0000') {
                throw new JavaTownParseException("missing closing quotation mark on string");
            }
            string = string + '\"';
            this.eat('\"');
            return string;
        }
        throw new JavaTownParseException("invalid character " + this.ch);
    }

    public String getRecent() {
        return this.recent;
    }
}

