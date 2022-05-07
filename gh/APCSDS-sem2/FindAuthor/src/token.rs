#[derive(Debug, PartialEq, Hash, Clone)]
pub enum TokenType {
    Word,
    EndOfSentence,
    EndOfFile,
    EndOfPhrase,
    Digit,
    Unknown,
}

impl std::fmt::Display for TokenType {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        write!(f, "{:?}", self)
    }
}

#[derive(PartialEq, Hash, Clone)]
pub struct Token {
    token_type: TokenType,
    string: String,
}

impl Token {
    pub fn new(token_type: TokenType, string: String) -> Token {
        Token { token_type, string }
    }

    pub fn get_type(&self) -> TokenType {
        self.token_type.clone()
    }

    pub fn contents(&self) -> &str {
        &self.string
    }
}

impl std::fmt::Display for Token {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        write!(f, "Token<{}>[\"{}\"]", self.token_type, self.string)
    }
}
