use crate::{Case, Declension, Gender, Number};

pub struct RegularNoun {
    pub declension: Declension,
    pub gender: Gender,
    pub is_greek: bool,

    pub nom: String,
    pub gen: String,
}

pub enum NounAdjectiveEntry {
    Regular(RegularNoun),
    Irregular(),
}

pub struct NounAdjectiveForm {
    // gender is duplicated
    pub gender: Gender,
    // 4 genders
    pub case: Case,
    // 7 cases
    pub number: Number, // 2 numbers
}

impl NounAdjectiveEntry {
    fn decline(&self, _form: &NounAdjectiveForm) -> Option<String> {
        todo!()
    }
}

pub trait RegularDeclinor {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str>;
    fn decline(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String>;
}
