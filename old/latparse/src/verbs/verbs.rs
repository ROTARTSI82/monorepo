use crate::{Conjugation, Mood, Number, Person, Tense, Voice};

pub enum VerbType {
    Normal,
    Deponent,
    SemiDeponent,
    Defective,
    Irregular,
}

pub struct RegularVerb {
    conjugation: Conjugation,
    verb_type: VerbType,

    present: String,
    infinitive: String,
    perfect: String,
    passive: String,
}

pub enum VerbEntry {
    Regular(RegularVerb),
    Irregular(),
}

pub struct VerbForm {
    pub tense: Tense,   // 6 tenses
    pub voice: Voice,   // 2 voices
    pub mood: Mood,     // 3 moods
    pub person: Person, // 3 persons
    pub number: Number, // 2 numbers
}

pub trait Conjugater {}
