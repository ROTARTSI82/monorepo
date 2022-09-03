use crate::nouns::common::{RegularDeclinor, RegularNoun};
use crate::Number::{Plural, Singular};
use crate::{lower_no_macrons, Case, NounAdjectiveForm, Number};

// Note: this code assumes the nominative/genitive are always
// the singular forms! This ~should~ work alright? idk.

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

fn decline_from_endings<T: RegularDeclinor>(
    entry: &RegularNoun,
    form: &NounAdjectiveForm,
    stem: &str,
) -> Option<String> {
    T::get_ending(entry, form).map(|x| {
        x.split('/')
            .map(|e| {
                if e.is_empty() {
                    return entry.nom.to_owned();
                }

                let stem = stem.to_owned();
                let is_candidate = lower_no_macrons(&stem).ends_with('i');
                if e == "?ī" && is_candidate {
                    stem[..stem.len() - 1].to_owned() + "ī" + "/" + &stem + e
                } else if e == "!ī" && is_candidate {
                    stem[..stem.len() - 1].to_owned() + "ī"
                } else {
                    stem + e
                }
            })
            .collect::<Vec<String>>()
            .join("/")
    })
}

impl RegularDeclinor for FirstDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        let greek_possibilities = [
            nom.ends_with('e') && gen.ends_with("es"),
            nom.ends_with("es") && gen.ends_with("ae"),
            nom.ends_with("as") && gen.ends_with("ae"),
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
        decline_from_endings::<FirstDeclension>(entry, form, &entry.gen[..entry.gen.len() - 2])
    }
}

pub struct SecondDeclension();

fn simple_second_ending(form: &NounAdjectiveForm, entry: &RegularNoun) -> Option<&'static str> {
    let nom = lower_no_macrons(&entry.nom);
    // "!i" means any occurrence of "ii" MUST be syncopated to a "ī"
    // "?i" means any occurrence of "ii" MAY be syncopated to a "ī" (both are valid)
    chart_lookup(
        &[
            [if entry.is_greek { "os/us" } else { "us" }, "ī"],
            ["?ī", "ōrum"],
            ["ō", "īs"],
            [if entry.is_greek { "on/um" } else { "um" }, "ōs"],
            ["ō", "īs"],
            [
                if entry.is_greek {
                    if nom.ends_with("on") || nom.ends_with("um") {
                        "on/um" // greek neuter on/um ending
                    } else {
                        "e" // greek os/us ending
                    }
                } else {
                    "e/!ī" // normal ending
                },
                "ī", // plural ending
            ],
            ["ī", "īs"],
        ],
        form,
    )
}

impl RegularDeclinor for SecondDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        let mut recognized = nom.ends_with('r');

        if recognized
            && matches!(form.number, Number::Singular)
            && matches!(form.case, Case::Nominative | Case::Vocative)
        {
            return Some(""); // same as nominative passed in
        }

        if (nom.ends_with("um") || nom.ends_with("on")) && gen.ends_with('i') {
            recognized = true;
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
                return Some(if entry.is_greek { "on/um" } else { "um" });
            }
        }

        recognized |= (nom.ends_with("us") || nom.ends_with("os")) && gen.ends_with('i');

        if recognized {
            simple_second_ending(form, entry)
        } else {
            None
        }
    }

    fn decline(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings::<SecondDeclension>(entry, form, &entry.gen[..entry.gen.len() - 1])
    }
}

pub struct ThirdDeclension();

impl RegularDeclinor for ThirdDeclension {
    fn get_ending(_entry: &RegularNoun, _form: &NounAdjectiveForm) -> Option<&'static str> {
        todo!()
    }

    fn decline(_entry: &RegularNoun, _form: &NounAdjectiveForm) -> Option<String> {
        todo!()
    }
}

pub struct FourthDeclension();

impl RegularDeclinor for FourthDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        let mut recognized = nom.ends_with("us") && gen.ends_with("us");

        if nom.ends_with('u') && (gen.ends_with('u') || gen.ends_with("us")) {
            return chart_lookup(
                &[
                    ["ū", "ua"],
                    ["ūs/(ū)", "uum"],
                    ["ū/(ūį)", "ibus"],
                    ["ū", "ua"],
                    ["ū", "ibus"],
                    ["ū", "ua"],
                ],
                form,
            );
        } else if nom.ends_with('o') && gen.ends_with("us") {
            // greek form
            recognized = true;
            if matches!(form.number, Singular) {
                return ["ō", "ūs", "ō", "ō/ōn/ūn", "ō", "ō"]
                    .get(form.case as usize)
                    .copied();
            }
        }

        if recognized {
            chart_lookup(
                &[
                    ["us", "ūs"],
                    ["ūs", "uum"],
                    ["uī", "ibus"],
                    ["um", "ūs"],
                    ["ū", "ibus"],
                    ["us", "ūs"],
                ],
                form,
            )
        } else {
            None
        }
    }

    fn decline(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings::<FourthDeclension>(
            entry,
            form,
            &entry.gen[..entry.gen.len()
                - if lower_no_macrons(&entry.gen).ends_with('u') {
                    1
                } else {
                    2
                }],
        )
    }
}

pub struct FifthDeclension();

impl RegularDeclinor for FifthDeclension {
    fn get_ending(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
        let nom = lower_no_macrons(&entry.nom);
        let gen = lower_no_macrons(&entry.gen);

        // check it's actually 5th declension
        if !(nom.ends_with("es") && gen.ends_with("ei")) {
            return None;
        }

        let gen_dat = if nom.ends_with("ies") { "ēi" } else { "eī" };

        chart_lookup(
            &[
                ["ēs", "ēs"],
                [gen_dat, "ērum"],
                [gen_dat, "ēbus"],
                ["em", "ēs"],
                ["ē", "ēbus"],
                ["ēs", "ēs"],
                ["ē", "ēbus"],
            ],
            form,
        )
    }

    fn decline(entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings::<FifthDeclension>(entry, form, &entry.nom[..entry.nom.len() - 2])
    }
}
