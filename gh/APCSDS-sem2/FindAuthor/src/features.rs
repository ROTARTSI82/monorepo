//! Defines [`LinguisticFeatures`] for storing statistical parameters for
//! documents, and methods for calculating the deviation and for loading
//! [`LinguisticFeatures`] from database files.

use std::fs::File;
use std::io::BufRead;
use std::io::{BufReader, Error, ErrorKind};
use std::path::PathBuf;

/// Simple data struct to store the statistical parameters of the text.
/// See documentation for
/// [`get_name`][`LinguisticFeatures::get_name`],
/// [`avg_word_len`][`LinguisticFeatures::avg_word_len`],
/// [`type_token`][`LinguisticFeatures::type_token`],
/// [`hapax_legomana`][`LinguisticFeatures::hapax_legomana`],
/// [`words_per_sent`][`LinguisticFeatures::words_per_sent`], and
/// [`sent_complexity`][`LinguisticFeatures::sent_complexity`] for an
/// explanation of the statistics stored by `vec` (in that order).
#[derive(Debug)]
pub struct LinguisticFeatures {
    name: String,
    vec: [f64; 5],
}

impl LinguisticFeatures {
    /// Constructs a new [`LinguisticFeatures`] object from
    /// an author name and an array of statistical features.
    ///
    /// See documentation referred to in [`LinguisticFeatures`] for
    /// the elements expected in `vec`
    pub fn new(name: String, vec: [f64; 5]) -> LinguisticFeatures {
        LinguisticFeatures { name, vec }
    }

    /// Gets the name of the author of the document, or
    /// `"UNKNOWN"` if the author is not known
    #[allow(dead_code)]
    pub fn get_name(&self) -> &str {
        &self.name
    }

    /// Gets the average word length throughout the document,
    /// which is the total number of characters in all words
    /// divided by the number of words in the document, with words being
    /// [`Token`][crate::token::Token]s with [`TokenType::Word`][crate::token::TokenType::Word]
    #[allow(dead_code)]
    pub fn avg_word_len(&self) -> f64 {
        self.vec[0]
    }

    /// Gets the type token ratio of the document, which is
    /// the number of unique words used divided by the number of
    /// total words in the document, with words being
    /// [`Token`][crate::token::Token]s with [`TokenType::Word`][crate::token::TokenType::Word]
    #[allow(dead_code)]
    pub fn type_token(&self) -> f64 {
        self.vec[1]
    }

    /// Gets the Hapax-Legomana ratio of the text, which is
    /// the number of words occurring exactly once throughout the text divided by
    /// the number of total words in the text, with words being
    /// [`Token`][crate::token::Token]s with [`TokenType::Word`][crate::token::TokenType::Word]
    #[allow(dead_code)]
    pub fn hapax_legomana(&self) -> f64 {
        self.vec[2]
    }

    /// Gets the average number of words per [`Sentence`], which is the total
    /// number of words in the text divided by the number of [`Sentence`]s, with words being
    /// [`Token`][crate::token::Token]s with [`TokenType::Word`][crate::token::TokenType::Word]
    ///
    /// [`Sentence`]: crate::grouping::Sentence
    #[allow(dead_code)]
    pub fn words_per_sent(&self) -> f64 {
        self.vec[3]
    }

    /// Gets the sentence complexity of the text, which is the average number
    /// of [`Phrase`]s per [`Sentence`] (i.e. Total number of [`Phrase`]s in the text
    /// divided by the total number of [`Sentence`]s)
    ///
    /// [`Sentence`]: crate::grouping::Sentence
    /// [`Phrase`]: crate::grouping::Phrase
    #[allow(dead_code)]
    pub fn sent_complexity(&self) -> f64 {
        self.vec[4]
    }

    /// Gets the weighted "[root square mean error]" (standard deviation,
    /// or root square mean deviation) of this text when compared to the `rhs` text.
    ///
    /// Each square of a difference is multiplied by the corresponding weight in `weights`
    ///
    /// # Panics
    /// This function will always panic if `weights` is not an array of length 5.
    ///
    /// [root square mean error]: https://en.wikipedia.org/wiki/Root-mean-square_deviation#Formula
    pub fn rmse(&self, rhs: &LinguisticFeatures, weights: &[f64]) -> f64 {
        if weights.len() != 5 {
            panic!("accumulate() must be called with array of 5 floats");
        }

        ((0..5)
            .map(|i| weights[i] * (self.vec[i] - rhs.vec[i]).powi(2))
            .sum::<f64>()
            / 5.0)
            .sqrt()
    }

    /// Loads a [`LinguisticFeatures`] object from a file in the database.
    /// The file should have six lines, with the first line containing
    /// the name of the author, and the other lines containing the 5 values for
    /// [`LinguisticFeatures::vec`] in order, formatted in a way that can be
    /// parsed by `.parse::<f64>()`
    ///
    /// Those 5 values (in order) would be:
    /// [`average word length`][`LinguisticFeatures::avg_word_len`],
    /// [`type token ratio`][`LinguisticFeatures::type_token`],
    /// [`Hapax-Legomana ratio`][`LinguisticFeatures::hapax_legomana`],
    /// [`average words per sentence`][`LinguisticFeatures::words_per_sent`], and
    /// [`sentence complexity`][`LinguisticFeatures::sent_complexity`]
    ///
    /// # Errors
    /// If the file cannot be read or parsed correctly due to being formatted incorrectly,
    /// the corresponding [`Error`] is returned
    ///
    pub fn from_file(name: PathBuf) -> Result<LinguisticFeatures, Error> {
        let lines = BufReader::new(File::open(name)?)
            .lines()
            .filter(|x| match x {
                Ok(_) => true,
                Err(e) => {
                    eprintln!("Failed to get line from file: {}", e);
                    false
                }
            })
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
            vec: [
                get_float(1)?,
                get_float(2)?,
                get_float(3)?,
                get_float(4)?,
                get_float(5)?,
            ],
        })
    }
}
