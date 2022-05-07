use crate::grouping::*;
use crate::token::Token;
use crate::token::TokenType::*;
use crate::Scanner;
use std::collections::HashMap;
use std::fs::File;
use std::io::BufRead;
use std::io::{BufReader, Error, ErrorKind};

pub struct Document {
    internal: GenericGroup<Sentence>,
    scanner: Scanner,
    token: Token,
}

#[derive(Debug)]
pub struct LinguisticFeatures {
    name: String,
    avg_word_len: f64,
    type_token: f64,     // number of unique words / total words
    hapax_legomana: f64, // number of words appearing only once / total words
    words_per_sent: f64,
    sent_complexity: f64, // average number of phrases per sentence
}

impl Document {
    pub fn new(mut scanner: Scanner) -> Document {
        let mut ret = Document {
            internal: GenericGroup::<Sentence>::new(),
            token: scanner.next_token(),
            scanner,
        };
        ret.parse_document();
        ret
    }

    pub fn borrow(&self) -> &Vec<Sentence> {
        self.internal.borrow()
    }

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
        LinguisticFeatures {
            name: "UNKNOWN".to_string(),
            avg_word_len: chars as f64 / words,
            type_token: wordcount.len() as f64 / words,
            hapax_legomana: one_off_words as f64 / words,
            words_per_sent: words as f64 / self.internal.borrow().len() as f64,
            sent_complexity: phrases as f64 / self.internal.borrow().len() as f64,
        }
    }
}

impl LinguisticFeatures {
    pub fn get_name(&self) -> &str {
        &self.name
    }
    pub fn avg_word_len(&self) -> f64 {
        self.avg_word_len
    }
    pub fn type_token(&self) -> f64 {
        self.type_token
    }
    pub fn hapax_legomana(&self) -> f64 {
        self.hapax_legomana
    }
    pub fn words_per_sent(&self) -> f64 {
        self.words_per_sent
    }
    pub fn sent_complexity(&self) -> f64 {
        self.sent_complexity
    }

    pub fn accumulate(&self, weights: &[f64]) -> f64 {
        if weights.len() != 5 {
            panic!("accumulate() must be called with array of 5 floats");
        }

        self.avg_word_len * weights[0]
            + self.type_token * weights[1]
            + self.hapax_legomana * weights[2]
            + self.words_per_sent * weights[3]
            + self.sent_complexity * weights[4]
    }

    pub fn from_file(name: &str) -> Result<LinguisticFeatures, std::io::Error> {
        let mut lines = BufReader::new(File::open(name)?)
            .lines()
            .filter(|x| x.is_ok())
            .map(|x| x.unwrap())
            .take(6)
            .collect::<Vec<String>>();

        let name = lines
            .first()
            .ok_or_else(|| Error::new(ErrorKind::Other, "Author Field missing in from_file"))?
            .clone();

        let get_float = |n: usize| -> Result<f64, std::io::Error> {
            match lines
                .get(n)
                .ok_or_else(|| Error::new(ErrorKind::Other, "Missing Field in from_file"))?
                .parse::<f64>()
            {
                Ok(x) => Ok(x),
                Err(e) => Err(Error::new(
                    ErrorKind::Other,
                    format!("Missing field: {}", e),
                )),
            }
        };

        Ok(LinguisticFeatures {
            name,
            avg_word_len: get_float(1)?,
            type_token: get_float(2)?,
            hapax_legomana: get_float(3)?,
            words_per_sent: get_float(4)?,
            sent_complexity: get_float(5)?,
        })
    }
}

impl Document {
    fn peek(&self) -> &Token {
        &self.token
    }

    fn next_token(&mut self) -> Token {
        let mut ret = self.scanner.next_token();
        std::mem::swap(&mut self.token, &mut ret);
        ret // ret should now contain the old value of self.token
    }

    fn eat(&mut self, t: Token) {
        if t == self.token {
            self.next_token();
        } else {
            panic!("Unexpected token: Expected {}, got {}", t, self.token);
        }
    }

    fn parse_phrase(&mut self) -> Phrase {
        // TODO: Should digit be included here? Should Unknown be included?
        let mut ret = Phrase::new();
        while !matches!(
            self.peek().get_type(),
            EndOfPhrase | EndOfSentence | EndOfFile
        ) {
            let next = self.next_token();
            if next.get_type() == Word {
                ret.add(next)
            }
        }

        ret
    }

    fn parse_sentence(&mut self) -> Sentence {
        let mut ret = Sentence::new();

        while !matches!(self.peek().get_type(), EndOfFile | EndOfSentence) {
            while self.peek().get_type() == EndOfPhrase {
                self.next_token();
            }

            ret.add(self.parse_phrase());
        }

        ret
    }

    fn parse_document(&mut self) {
        while self.peek().get_type() != EndOfFile {
            while self.peek().get_type() == EndOfSentence {
                self.next_token();
            }

            // can't combine into 1 line since rust complains about multiple
            // mutable borrows ????
            let sent = self.parse_sentence();
            self.internal.add(sent);
        }
    }
}

impl std::fmt::Display for Document {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        write!(f, "{}", self.internal)
    }
}
