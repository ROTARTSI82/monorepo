use crate::nouns::nouns::NounAdjectiveForm;

pub mod verbs {
    pub mod verbs;
}

pub mod nouns {
    pub mod first_declension;
    pub mod nouns;
}

fn remove_macrons(x: &str) -> String {
    x.replace("ā", "a")
        .replace("ē", "e")
        .replace("ī", "i")
        .replace("ō", "o")
        .replace("ū", "u")
        .replace("Ā", "A")
        .replace("Ē", "E")
        .replace("Ī", "I")
        .replace("Ō", "O")
        .replace("Ū", "U")
}

fn lower_no_macrons(x: &str) -> String {
    remove_macrons(x).to_lowercase()
}

#[derive(Clone, Copy)]
pub enum Number {
    Singular = 0,
    Plural = 1,
}

pub enum Gender {
    Common,
    Masculine,
    Feminine,
    Neuter,
    Any, // used for adjectives
}

pub enum Person {
    First,
    Second,
    Third,
}

#[derive(Clone, Copy)]
pub enum Case {
    Nominative = 0,
    Genitive = 1,
    Dative = 2,
    Accusative = 3,
    Ablative = 4,
    Vocative = 5,
    Locative = 6,
}

pub enum Mood {
    Indicative,
    Subjunctive,
    Imperative,
}

pub enum Voice {
    Active,
    Passive,
}

pub enum Conjugation {
    Irregular,
    First,
    Second,
    Third,
    ThirdIO,
    Fourth,
}

pub enum Declension {
    Irregular,
    First,
    Second,
    Third,
    Fourth,
    Fifth,
}

pub enum Tense {
    Present,
    Imperfect,
    Perfect,
    Pluperfect,
    FuturePerfect,
    Future,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = 2 + 2;
        assert_eq!(result, 4);
    }
}
