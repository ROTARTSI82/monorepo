//! Defines a [`Scanner`] for scanning over input one character at a time
//! and for tokenization of the input.

use crate::token::TokenType::{Digit, EndOfFile, EndOfPhrase, EndOfSentence, Word};
use crate::token::{Token, TokenType};
use std::io::Read;

/// Scanner object for reading an input stream character by character,
/// and for tokenizing the input
pub struct Scanner {
    file: Box<dyn Read>,
    char: char,
    eof: bool,
}

impl Scanner {
    /// Constructs a new [`Scanner`] from an arbitrary input source
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
    /// Checks whether we have reached the end of the input stream
    pub fn has_next(&self) -> bool {
        !self.eof
    }

    /// Gets the next character in the input stream without
    /// consuming it or advancing the Scanner
    pub fn peek(&self) -> char {
        self.char
    }

    /// Gets the next character in the input stream and advances
    /// the Scanner to the next one after that
    pub fn next(&mut self) -> char {
        let ret = self.peek();
        self.consume_next();
        ret
    }

    /// Consumes some number of characters from the Scanner to
    /// construct the next valid [`Token`] from the contents of the input.
    ///
    /// See documentation of [`Token`] for the variants that can be produced.
    pub fn next_token(&mut self) -> Token {
        match self.next() {
            _ if !self.has_next() => Token::new(EndOfFile, "".to_string()),
            c @ '0'..='9' => Token::new(Digit, c.to_string()),
            c @ ('.' | '!' | '?') => Token::new(EndOfSentence, c.to_string()),
            c @ (',' | ':' | ';') => Token::new(EndOfPhrase, c.to_string()),
            c @ ('a'..='z' | 'A'..='Z') => {
                let mut ret = c.to_string();
                while self.has_next()
                    && match self.peek() {
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

    /// Consumes the next character if it matches the character specified
    /// by the argument `c`.
    ///
    /// # Panics
    /// This function always panics if the next character does not equal
    /// the character `c`
    #[allow(dead_code)]
    pub fn eat(&mut self, c: char) {
        if self.char == c {
            self.consume_next();
        } else {
            panic!("Char does not match! Expected '{}', got '{}'", c, self.char);
        }
    }
}

impl Scanner {
    /// Consumes the next character in the input source, advancing this [`Scanner`] forward.
    fn consume_next(&mut self) {
        if let Some(b) = self.file.as_mut().bytes().next() {
            self.char = b.expect("File IO error") as char;
        } else {
            self.eof = true;
        }
    }

    /// Checks if the character `c` is a letter (case insensitive)
    #[allow(dead_code)]
    fn is_letter(c: char) -> bool {
        matches!(c, 'A'..='Z' | 'a'..='z')
    }

    /// Checks if the character `c` is a digit (in the range `0` to `9`)
    #[allow(dead_code)]
    fn is_digit(c: char) -> bool {
        matches!(c, '0'..='9')
    }

    /// Checks if the character `c` is a sentence terminator (`'.'`, `'?'`, or `'!'`)
    #[allow(dead_code)]
    fn is_sent_term(c: char) -> bool {
        matches!(c, '.' | '?' | '!')
    }

    /// Checks if the character `c` is a phrase terminator (`','`, `':'`, or `';'`)
    #[allow(dead_code)]
    fn is_phrase_term(c: char) -> bool {
        matches!(c, ',' | ':' | ';')
    }

    /// Checks if the character `c` is a special character (`'-'`, `'"'`, or `'\''`)
    #[allow(dead_code)]
    fn is_special(c: char) -> bool {
        matches!(c, '-' | '"' | '\'')
    }

    /// Checks if a character `c` is whitespace (`' '`, `'\t'`, or `'\n'`)
    #[allow(dead_code)]
    fn is_whitespace(c: char) -> bool {
        matches!(c, ' ' | '\t' | '\n')
    }
}
