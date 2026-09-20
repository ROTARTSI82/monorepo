package p000;

import java.io.IOException;
import java.io.Reader;

/* renamed from: Scanner */
public class Scanner {

    /* renamed from: ch */
    private char f8ch;

    /* renamed from: in */
    private Reader f9in;
    private String recent = "";

    public Scanner(Reader reader) {
        this.f9in = reader;
        advance();
    }

    private void advance() {
        try {
            int read = this.f9in.read();
            if (read == -1) {
                this.f8ch = 0;
            } else {
                this.f8ch = (char) read;
            }
            this.recent += this.f8ch;
            while (this.recent.length() > 100 && this.recent.indexOf("\n") != -1) {
                this.recent = this.recent.substring(this.recent.indexOf("\n") + 1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void eat(char c) {
        if (this.f8ch == c) {
            advance();
            return;
        }
        throw new JavaTownParseException("expected " + c + " but found " + this.f8ch);
    }

    private boolean isLetter() {
        return (this.f8ch >= 'a' && this.f8ch <= 'z') || (this.f8ch >= 'A' && this.f8ch <= 'Z') || this.f8ch == '_';
    }

    private boolean isDigit() {
        return this.f8ch >= '0' && this.f8ch <= '9';
    }

    public String next() {
        while (true) {
            if (this.f8ch != ' ' && this.f8ch != 13 && this.f8ch != 10 && this.f8ch != 9) {
                break;
            }
            eat(this.f8ch);
        }
        String str = "";
        if (this.f8ch == '/') {
            eat(BinOp.DIVIDE);
            if (this.f8ch != '/') {
                return "/";
            }
            while (this.f8ch != 10 && this.f8ch != 0) {
                eat(this.f8ch);
            }
            return next();
        } else if (isLetter()) {
            while (true) {
                if (!isLetter() && !isDigit()) {
                    return str;
                }
                str = str + this.f8ch;
                eat(this.f8ch);
            }
        } else if (isDigit()) {
            while (isDigit()) {
                str = str + this.f8ch;
                eat(this.f8ch);
            }
            return str;
        } else if ("=!<>".indexOf(this.f8ch + "") != -1) {
            String str2 = str + this.f8ch;
            eat(this.f8ch);
            if (this.f8ch != '=') {
                return str2;
            }
            String str3 = str2 + BinOp.f0EQ;
            eat(BinOp.f0EQ);
            return str3;
        } else if (this.f8ch == '|') {
            eat(BinOp.f6OR);
            eat(BinOp.f6OR);
            return "||";
        } else if (this.f8ch == '&') {
            eat(BinOp.AND);
            eat(BinOp.AND);
            return "&&";
        } else if (this.f8ch == 0) {
            eat((char) 0);
            return " ";
        } else if ("{};.(),+-*/%".indexOf("" + this.f8ch) != -1) {
            String str4 = str + this.f8ch;
            eat(this.f8ch);
            return str4;
        } else if (this.f8ch == '\"') {
            String str5 = str + '\"';
            eat('\"');
            while (this.f8ch != '\"' && this.f8ch != 0) {
                str5 = str5 + this.f8ch;
                eat(this.f8ch);
            }
            if (this.f8ch == 0) {
                throw new JavaTownParseException("missing closing quotation mark on string");
            }
            String str6 = str5 + '\"';
            eat('\"');
            return str6;
        } else {
            throw new JavaTownParseException("invalid character " + this.f8ch);
        }
    }

    public String getRecent() {
        return this.recent;
    }
}
