use crate::nouns::common::{RegularDeclinor, RegularNoun};
use crate::Gender::{Feminine, Masculine, Neuter};
use crate::Number::{Plural, Singular};
use crate::{lower_no_macrons, Case, NounAdjectiveForm, Number};

pub struct FirstDeclension();

fn chart_lookup<const H: usize>(
    chart: &[[&'static str; 2]; H],
    form: &NounAdjectiveForm,
) -> Option<&'static str> {
    chart
        .get(form.case as usize)
        .and_then(|r| r.get(form.number as usize).copied())
}

fn simple_first_ending(form: &NounAdjectiveForm) -> Option<&'static str> {
    chart_lookup(
        &[
            ["a", "ae"],
            ["ae", "ārum"],
            ["ae", "īs"],
            ["am", "ās"],
            ["ā", "īs"],
            ["a", "ae"],
        ],
        form,
    )
}

impl RegularDeclinor for FirstDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        let greek_possibilities = [
            nom.ends_with('e') && gen.ends_with("es") && matches!(form.gender, Feminine),
            nom.ends_with("es") && gen.ends_with("ae") && matches!(form.gender, Masculine),
            nom.ends_with("as") && gen.ends_with("ae") && matches!(form.gender, Masculine),
        ];

        if nom.ends_with('a') && gen.ends_with("ae")
            || matches!(form.number, Plural) && greek_possibilities.iter().any(|&x| x)
        {
            return simple_first_ending(form);
        }

        if greek_possibilities[0] {
            Some(["ē", "ēs", "ae", "ēn", "ē", "ē", "ae"])
        } else if greek_possibilities[1] {
            Some(["ēs", "ae", "ae", "ēn", "ē", "ē", "ae"])
        } else if greek_possibilities[2] {
            // slash denotes alternate form
            Some(["ās", "ae", "ae", "ān/am", "ā", "ā", "ae"])
        } else {
            None
        }
        .and_then(|x| x.get(form.case as usize).copied())
    }

    fn decline(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        // todo: handle the / properly
        FirstDeclension::get_ending(entry, form)
            .map(|x| (&entry.gen[..entry.gen.len() - 2]).to_owned() + x)
    }
}

pub struct SecondDeclension();

fn simple_second_ending(form: &NounAdjectiveForm) -> Option<&'static str> {
    // "!i" means any occurrence of "ii" MUST be syncopated to a "ī"
    // "?i" means any occurrence of "ii" MAY be syncopated to a "ī" (both are valid)
    chart_lookup(
        &[
            ["us", "ī"],
            ["?ī", "ōrum"],
            ["ō", "īs"],
            ["um", "ōs"],
            ["ō", "īs"],
            ["e/!ī", "ī"],
            ["ī", "īs"],
        ],
        form,
    )
}

impl RegularDeclinor for SecondDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        if nom.ends_with('r')
            && matches!(entry.gender, Masculine)
            && matches!(form.number, Number::Singular)
            && matches!(form.case, Case::Nominative | Case::Vocative)
        {
            return Some(""); // same as nominative passed in
        }

        if nom.ends_with("um") && gen.ends_with('i') && matches!(entry.gender, Neuter) {
            if matches!(form.number, Plural)
                && matches!(
                    form.case,
                    Case::Nominative | Case::Accusative | Case::Vocative
                )
            {
                return Some("a");
            } else if matches!(form.number, Singular)
                && matches!(form.case, Case::Nominative | Case::Vocative)
            {
                return Some("um");
            }
        }

        if nom.ends_with("us") && gen.ends_with('i') && matches!(entry.gender, Masculine) {}

        simple_second_ending(form)
    }

    fn decline(_entry: &RegularNoun, _form: &NounAdjectiveForm) -> Option<String> {
        todo!()
    }
}
