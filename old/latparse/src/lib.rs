use crate::nouns::common::NounAdjectiveForm;

pub mod verbs {
    pub mod common;
}

pub mod nouns {
    pub mod common;
    pub mod first_declension;
}

pub fn remove_macrons(x: &str) -> String {
    x.replace('ā', "a")
        .replace('ē', "e")
        .replace('ī', "i")
        .replace('ō', "o")
        .replace('ū', "u")
        .replace('Ā', "A")
        .replace('Ē', "E")
        .replace('Ī', "I")
        .replace('Ō', "O")
        .replace('Ū', "U")
}

pub fn lower_no_macrons(x: &str) -> String {
    remove_macrons(x).to_lowercase()
}

#[derive(Clone, Copy)]
pub enum Number {
    Singular = 0,
    Plural = 1,
}

#[derive(Clone, Copy)]
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

#[derive(Clone, Copy)]
pub enum Declension {
    Irregular,
    First = 1,
    Second = 2,
    Third = 3,
    Fourth = 4,
    Fifth = 5,
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

    #[test]
    fn it_works() {
        let result = 2 + 2;
        assert_eq!(result, 4);
    }
}
