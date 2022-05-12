//! Defines [`Token`]s and [`TokenType`]s for the tokenization of
//! text into [`Sentence`]s, [`Phrase`]s, and [`Document`]s.
//!
//! [`Sentence`]: crate::grouping::Sentence
//! [`Phrase`]: crate::grouping::Phrase
//! [`Document`]: crate::document::Document

/// An enum of all possible token types
#[derive(Debug, PartialEq, Hash, Clone)]
pub enum TokenType {
    /// A word, starting with a letter and containing any number of
    /// letters, digits, hyphens (`'-'`), or single quotes.
    /// The string contents of [`Token`]s with this type should always be in lowercase.
    Word,

    /// The marker for the end of a [`Sentence`], thereby terminating [`Phrase`]s too.
    /// The string contents of [`Token`]s with this type
    /// may be the single characters `'.'`, `'!'`, or `'?'`
    ///
    /// [`Sentence`]: crate::grouping::Sentence
    /// [`Phrase`]: crate::grouping::Phrase
    EndOfSentence,

    /// The marker for the end of the file, emitted when the end of the input stream is reached,
    /// thereby terminating [`Sentence`]s and [`Phrase`]s too.
    /// The string contents of [`Token`]s with this type should be an empty string.
    ///
    /// [`Sentence`]: crate::grouping::Sentence
    /// [`Phrase`]: crate::grouping::Phrase
    EndOfFile,

    /// The marker for the end of a [`Phrase`].
    /// The string contents of [`Token`]s with this type may be the single characters
    /// `','`, `':'`, or `';'`
    ///
    /// [`Phrase`]: crate::grouping::Phrase
    EndOfPhrase,

    /// A single digit in the range `0` to `9`.
    /// The string contents of [`Token`]s with this type should be single characters.
    Digit,

    /// An unknown symbol or character, used when no other enum value
    /// describes this character.
    /// The string contents of corresponding [`Token`]s should be single characters.
    Unknown,
}

impl std::fmt::Display for TokenType {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        write!(f, "{:?}", self)
    }
}

/// A token appearing in a text
#[derive(PartialEq, Hash, Clone)]
pub struct Token {
    token_type: TokenType,
    string: String,
}

impl Token {
    /// Constructs a new [`Token`] from it's type and the string contents.
    ///
    /// NOTE: It is expected that `string` is ALWAYS in all lowercase, but
    /// this is not enforced.
    pub fn new(token_type: TokenType, string: String) -> Token {
        Token { token_type, string }
    }

    /// Gets the [`TokenType`] of this token
    pub fn get_type(&self) -> TokenType {
        self.token_type.clone()
    }

    /// Gets the string contents of the token, which can variably be
    /// a word, a single character, or empty depending on the [`TokenType`].
    ///
    /// The string returned by this function SHOULD be in all lowercase
    pub fn contents(&self) -> &str {
        &self.string
    }
}

impl std::fmt::Display for Token {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        write!(f, "{}[\"{}\"]", self.token_type, self.string)
    }
}
