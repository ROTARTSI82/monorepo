use crate::token::TokenType::{Digit, EndOfFile, EndOfPhrase, EndOfSentence, Word};
use crate::token::{Token, TokenType};
use std::io::Read;

pub struct Scanner {
    file: Box<dyn Read>,
    char: char,
    eof: bool,
}

impl Scanner {
    pub fn new(file: Box<dyn Read>) -> Scanner {
        let mut ret = Scanner {
            file,
            char: '\0',
            eof: false,
        };

        ret.consume_next();
        ret
    }
}

impl Scanner {
    fn consume_next(&mut self) {
        if let Some(b) = self.file.as_mut().bytes().next() {
            self.char = b.expect("File IO error") as char;
        } else {
            self.eof = true;
        }
    }

    fn is_letter(c: char) -> bool {
        matches!(c, 'A'..='Z' | 'a'..='z')
    }

    fn is_digit(c: char) -> bool {
        matches!(c, '0'..='9')
    }

    fn is_sent_term(c: char) -> bool {
        matches!(c, '.' | '?' | '!')
    }

    fn is_phrase_term(c: char) -> bool {
        matches!(c, ',' | ':' | ';')
    }

    fn is_special(c: char) -> bool {
        matches!(c, '-' | '"' | '\'')
    }

    fn is_whitespace(c: char) -> bool {
        matches!(c, ' ' | '\t' | '\n')
    }
}

impl Scanner {
    pub fn has_next(&self) -> bool {
        !self.eof
    }

    pub fn get(&self) -> char {
        self.char
    }

    pub fn next(&mut self) -> char {
        // return '\0';

        let ret = self.get();
        self.consume_next();
        ret
    }

    pub fn next_token(&mut self) -> Token {
        match self.next() {
            _ if !self.has_next() => Token::new(EndOfFile, "".to_string()),
            c @ '0'..='9' => Token::new(Digit, c.to_string()),
            c @ ('.' | '!' | '?') => Token::new(EndOfSentence, c.to_string()),
            c @ (',' | ':' | ';') => Token::new(EndOfPhrase, c.to_string()),
            c @ ('a'..='z' | 'A'..='Z') => {
                let mut ret = c.to_string();
                while self.has_next()
                    && match self.get() {
                        c @ ('a'..='z' | 'A'..='Z' | '0'..='9' | '-' | '\'' | '_') => {
                            ret += &c.to_string();
                            true
                        }
                        _ => false,
                    }
                {
                    self.consume_next();
                }

                self.eof = false; // hack to emit eof token
                Token::new(Word, ret.to_lowercase())
            }

            c => Token::new(TokenType::Unknown, c.to_string()),
        }
    }

    pub fn eat(&mut self, c: char) {
        if self.char == c {
            self.consume_next();
        } else {
            panic!("Char does not match! Expected '{}', got '{}'", c, self.char);
        }
    }
}
