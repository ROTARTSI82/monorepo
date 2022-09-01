use crate::Case::{Ablative, Accusative, Dative, Genitive, Locative, Nominative, Vocative};
use crate::Number::{Plural, Singular};

#[derive(Copy, Clone)]
pub enum Number {
    Singular = 0,
    Plural = 1,
}

impl Number {
    fn from_int(i: i32) -> Option<Number> {
        match i {
            0 => Some(Singular),
            1 => Some(Plural),
            _ => None,
        }
    }
}

#[derive(Copy, Clone)]
pub enum Gender {
    Common = 0,
    Masculine = 1,
    Feminine = 2,
    Neuter = 3,
}

#[derive(Copy, Clone)]
pub enum Person {
    First = 1,
    Second = 2,
    Third = 3,
}

#[derive(Copy, Clone)]
pub enum Case {
    Nominative = 0,
    Vocative = 1,
    Genitive = 2,
    Dative = 3,
    Accusative = 4,
    Ablative = 5,
    Locative = 6,
}

impl Case {
    fn from_int(i: i32) -> Option<Case> {
        match i {
            0 => Some(Nominative),
            1 => Some(Vocative),
            2 => Some(Genitive),
            3 => Some(Dative),
            4 => Some(Accusative),
            5 => Some(Ablative),
            6 => Some(Locative),
            _ => None,
        }
    }
}

#[derive(Copy, Clone)]
pub enum Mood {
    Indicative = 0,
    Subjunctive = 1,
    Imperative = 2,
}

#[derive(Copy, Clone)]
pub enum Voice {
    Active = 0,
    Passive = 1,
}

#[derive(Copy, Clone)]
pub enum Conjugation {
    Irregular = 0,
    First = 1,
    Second = 2,
    Third = 3,
    ThirdIO = 5,
    Fourth = 4,
}

#[derive(Copy, Clone)]
pub enum Declension {
    Irregular = 0,
    First = 1,
    Second = 2,
    Third = 3,
    Fourth = 4,
    Fifth = 5,
}

#[derive(Copy, Clone)]
pub enum Tense {
    Present = 0,
    Imperfect = 1,
    Perfect = 2,
    Pluperfect = 3,
    FuturePerfect = 4,
    Future = 5,
}

pub enum NounInfo {
    Regular {
        declension: Declension,
        gender: Gender,
    },
    Irregular(Box<Chart>),
}

pub enum AdjectiveInfo {
    Regular(Declension),
    Irregular(Box<Chart>),
}

trait Keyable {
    fn index(&self) -> usize;
}

pub struct NounForm {
    pub case: Case,     // 7 cases
    pub number: Number, // 2 numbers
}

impl Keyable for NounForm {
    fn index(&self) -> usize {
        2 * (self.case as usize) + (self.number as usize)
    }
}

pub struct AdjectiveForm {
    pub gender: Gender, // 4 genders
    pub case: Case,     // 7 cases
    pub number: Number, // 2 numbers
}

impl Keyable for AdjectiveForm {
    fn index(&self) -> usize {
        14 * (self.gender as usize) + 2 * (self.case as usize) + (self.number as usize)
    }
}

pub enum VerbInfo {
    Regular(Conjugation),
    Irregular(Box<Chart>),
}

pub struct VerbForm {
    pub tense: Tense,   // 6 tenses
    pub voice: Voice,   // 2 voices
    pub mood: Mood,     // 3 moods
    pub person: Person, // 3 persons
    pub number: Number, // 2 numbers
}

impl Keyable for VerbForm {
    fn index(&self) -> usize {
        24 * (self.tense as usize)
            + 12 * (self.voice as usize)
            + 6 * (self.mood as usize)
            + 2 * (self.person as usize - 1)
            + (self.number as usize)
    }
}

type VerbLookup = fn() -> String;
type NounLookup = fn(nom: &String, gen: &String, gender: Gender);

#[derive(Default)]
pub struct Chart {
    data: Vec<fn() -> String>, // make this a function that transforms roots + form -> thing
}

impl Chart {
    fn lookup<T: Keyable>(&self, form: &T, ) -> String {
        self.data[form.index()]
    }
}

pub struct GlobalData {
    declensions: [Chart; 5],
    conjugations: [Chart; 5],
}

impl GlobalData {
    fn add_decl_chart(&mut self, decl: Declension, chart: [[&'static str; 2]; 7]) {
        for (row, case) in chart.into_iter().zip(0..7) {
            for (form, number) in row.into_iter().zip(0..2) {
                let chart = self.declensions.get_mut(decl as usize - 1).unwrap();
                let ind = NounForm {
                    case: Case::from_int(case).unwrap(),
                    number: Number::from_int(number).unwrap(),
                }.index();

                if ind > chart.data.len() {
                    chart.data.resize(ind + 1, "");
                }

                chart.data[ind] = form;
            }
        }
    }

    pub fn construct() -> GlobalData {
        let mut ret = GlobalData {
            declensions: Default::default(),
            conjugations: Default::default(),
        };

        ret.add_decl_chart(Declension::First, [
            ["a", "ae"], // nom
            ["a", "ae"], // voc
            ["ae", "ārum"], // gen
            ["ae", "īs"], // dat
            ["am", "ās"], // acc
            ["ā", "īs"], // abl
            ["ae", "is"] // loc - dubious
        ]);

        ret.add_decl_chart(Declension::Second, [
            ["us", "ī"],
            ["e", "ī"], // implement the -ī rule for -ius nouns
            ["ī", "ōrum"],
            ["o", "īs"],
            ["um", "os"],
            ["o", "īs"],
            ["ī", "īs"]
        ]);

        ret
    }
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
