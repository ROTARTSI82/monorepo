//! Defines types for parsing [`Document`]s and
//! calculating the [`LinguisticFeatures`] of the document
//! for author identification.

use crate::grouping::*;
use crate::token::Token;
use crate::token::TokenType::*;
use crate::Scanner;
use std::collections::HashMap;

use crate::features::LinguisticFeatures;

/// Represents a parsed document with a hierarchy of
/// sentences, phrases, and words, which is stored as a tree.
pub struct Document {
    internal: GenericGroup<Sentence>,
    scanner: Scanner,
    token: Token,
}

impl Document {
    /// Constructs a new [`Document`] from a [`Scanner`] over an
    /// arbitrary input stream.
    ///
    /// NOTE: This function is `O(n)` with `n` being the size
    /// of the input stream.
    pub fn new(mut scanner: Scanner) -> Document {
        let mut ret = Document {
            internal: GenericGroup::<Sentence>::new(),
            token: scanner.next_token(),
            scanner,
        };
        ret.parse_document();
        ret
    }

    /// Obtains an immutable reference to the internal list of
    /// Sentences which make up the text.
    #[allow(dead_code)]
    pub fn borrow(&self) -> &Vec<Sentence> {
        self.internal.borrow()
    }

    /// Computes the [`LinguisticFeatures`] (various statistical parameters)
    /// associated with the document in amortized `O(n)`
    /// with n being the length of the document.
    pub fn get_features(&self) -> LinguisticFeatures {
        let mut wordcount = HashMap::<String, u32>::new();

        let mut words = 0;
        let mut one_off_words = 0;
        let mut phrases = 0;
        let mut chars = 0;

        for sent in self.internal.borrow() {
            for phrase in sent.borrow() {
                phrases += 1;

                for word in phrase.borrow() {
                    if word.get_type() == Word {
                        words += 1;
                        chars += word.contents().len();

                        if !wordcount.contains_key(word.contents()) {
                            wordcount.insert(word.contents().to_string(), 1);
                            one_off_words += 1;
                        } else {
                            let v = wordcount.get_mut(word.contents()).unwrap();
                            if *v == 1 {
                                one_off_words -= 1;
                            }

                            *v += 1;
                        }
                    }
                }
            }
        }

        let words = words as f64;

        println!("Characters: {}", chars);
        println!("Words: {}", words);
        println!("Phrases: {}", phrases);
        println!("Sentences: {}", self.internal.borrow().len());
        println!("Unique Words: {}", wordcount.len());
        println!("One-Off Words: {}", one_off_words);

        LinguisticFeatures::new(
            "UNKNOWN".to_string(),
            [
                chars as f64 / words,
                wordcount.len() as f64 / words,
                one_off_words as f64 / words,
                words as f64 / self.internal.borrow().len() as f64,
                phrases as f64 / self.internal.borrow().len() as f64,
            ],
        )
    }
}

impl Document {
    /// Gets the next token about to be returned without consuming it
    /// or advancing the internal `scanner` forward. This function is `O(1)`.
    fn peek(&self) -> &Token {
        &self.token
    }

    /// Gets the next token in the input stream and consumes it,
    /// advancing the internal `scanner` forward. This function is `O(1)`.
    fn next_token(&mut self) -> Token {
        let mut ret = self.scanner.next_token();
        std::mem::swap(&mut self.token, &mut ret);
        // println!("{}", ret);
        ret // ret should now contain the old value of self.token
    }

    /// Consumes the next token if the next token is equal in value to
    /// the token `t` specified. This function is `O(1)`
    ///
    /// # Panics
    /// This function always panics if the next token is NOT the same as the token passed in
    /// to this function.
    #[allow(dead_code)]
    fn eat(&mut self, t: Token) {
        if t == self.token {
            self.next_token();
        } else {
            panic!("Unexpected token: Expected {}, got {}", t, self.token);
        }
    }

    /// Reads the internal input source until a complete phrase is received,
    /// returning the next phrase found in the text. This function is `O(n)`.
    ///
    /// This function will consume tokens until a [`EndOfPhrase`], [`EndOfSentence`],
    /// or [`EndOfFile`] is reached.
    fn parse_phrase(&mut self) -> Phrase {
        let mut ret = Phrase::new();
        while !matches!(
            self.peek().get_type(),
            EndOfPhrase | EndOfSentence | EndOfFile
        ) {
            let next = self.next_token();
            if next.get_type() == Word && !next.contents().is_empty() {
                ret.add(next)
            }
        }

        ret
    }

    /// Reads the internal input source until a full sentence is received,
    /// returning the next sentence found in the text. This function is `O(n)`.
    ///
    /// This function will consume tokens until a [`EndOfFile`] or [`EndOfSentence`]
    /// is reached.
    fn parse_sentence(&mut self) -> Sentence {
        let mut ret = Sentence::new();

        while !matches!(self.peek().get_type(), EndOfFile | EndOfSentence) {
            while self.peek().get_type() == EndOfPhrase {
                self.next_token();
            }

            let phrase = self.parse_phrase();
            if !phrase.borrow().is_empty() {
                ret.add(phrase);
            }
        }

        ret
    }

    /// Reads the entire input source and loads the contents into this document.
    /// This function will keep reading until an [`EndOfFile`]. This function is `O(n)`.
    fn parse_document(&mut self) {
        while self.peek().get_type() != EndOfFile {
            while self.peek().get_type() == EndOfSentence {
                self.next_token();
            }

            // can't combine into 1 line since rust complains about multiple
            // mutable borrows ????
            let sent = self.parse_sentence();
            if !sent.borrow().is_empty() {
                self.internal.add(sent);
            }
        }
    }
}

impl std::fmt::Display for Document {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        // write!(f, "{}", self.internal)

        writeln!(f, "DOCUMENT")?;
        for sent in self.internal.borrow() {
            writeln!(f, "\tSENTENCE")?;
            for phrase in sent.borrow() {
                writeln!(f, "\t\tPHRASE")?;
                for word in phrase.borrow() {
                    writeln!(f, "\t\t\t{}", word.contents())?;
                }
            }
        }

        Ok(())
    }
}
