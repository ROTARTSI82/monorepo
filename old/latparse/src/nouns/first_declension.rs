use crate::nouns::common::{RegularDecliner, RegularNoun};
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
            ["ae/āī@poet/ās@arch", "ārum/um"],
            ["ae", "īs/ābus@dis"],
            ["am", "ās"],
            ["ā", "īs/ābus@dis"],
            ["a", "ae"],
        ],
        form,
    )
}

/// Syntax:
///     - / denotes multiple possible endings
///     - ?i denotes that the syncopated and non-syncopated forms are both valid
///         (The syncopated form is archaic)
///     - !e_ī is used for 2nd declension vocatives, indicating that whenever possible
///       ii MUST be syncopated to ī, and otherwise it's a normal -e ending
///     - @arch denotes an archaic form
///     - @<TAG> some tag
fn decline_from_endings(
    decliner: &impl RegularDecliner,
    entry: &RegularNoun,
    form: &NounAdjectiveForm,
    stem: &str,
) -> Option<String> {
    decliner.get_ending(entry, form).map(|x| {
        x.split('/')
            .map(|e| {
                if e.is_empty() {
                    return entry.nom.to_owned();
                }

                let stem = stem.to_owned();
                let is_candidate = lower_no_macrons(&stem).ends_with('i');

                // remove the ? and ! decoration if we can't syncopate anyways
                let e = if !is_candidate && e.starts_with('?') {
                    &e[1..]
                } else if !is_candidate && e.starts_with('!') {
                    "e" // for 2nd declension vocative
                } else {
                    e
                };

                if e == "?ī" && is_candidate {
                    stem[..stem.len() - 1].to_owned() + "ī@arch/" + &stem + &e[1..]
                } else if e == "!e_ī" && is_candidate {
                    stem[..stem.len() - 1].to_owned() + "ī"
                } else {
                    stem + e
                }
            })
            .collect::<Vec<String>>()
            .join("/")
    })
}

impl RegularDecliner for FirstDeclension {
    fn get_ending(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
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

    fn decline(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings(self, entry, form, &entry.gen[..entry.gen.len() - 2])
    }
}

pub struct SecondDeclension();

fn simple_second_ending(form: &NounAdjectiveForm, entry: &RegularNoun) -> Option<&'static str> {
    let nom = lower_no_macrons(&entry.nom);
    // "!e_ī" means any occurrence of "ii" MUST be syncopated to a "ī", otherwise it's a
    //        normal ending -e
    // "?i" means any occurrence of "ii" MAY be syncopated to a "ī" (both are valid)

    let chart = if entry.is_greek {
        [
            ["us/os", "ī/oe@prop"],
            ["?ī", "ōrum/ōn@prop"],
            ["ō", "īs"],
            ["um/on", "ōs"],
            ["ō", "īs"],
            [
                if nom.ends_with("on") || nom.ends_with("um") {
                    "on/um" // greek neuter on/um ending
                } else {
                    "e" // greek os/us ending
                },
                "ī",
            ],
            ["ī", "īs"],
        ]
    } else {
        [
            ["us", "ī"],
            ["?ī", "ōrum"],
            ["ō", "īs"],
            ["um", "ōs"],
            ["ō", "īs"],
            ["!e_ī", "ī"],
            ["ī", "īs"],
        ]
    };

    chart_lookup(&chart, form)
}

impl RegularDecliner for SecondDeclension {
    fn get_ending(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
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

    fn decline(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings(self, entry, form, &entry.gen[..entry.gen.len() - 1])
    }
}

pub struct ThirdDeclension();

impl RegularDecliner for ThirdDeclension {
    fn get_ending(&self, _entry: &RegularNoun, _form: &NounAdjectiveForm) -> Option<&'static str> {
        None
    }

    fn decline(&self, _entry: &RegularNoun, _form: &NounAdjectiveForm) -> Option<String> {
        None
    }
}

pub struct FourthDeclension();

impl RegularDecliner for FourthDeclension {
    fn get_ending(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
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

    fn decline(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings(
            self,
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

impl RegularDecliner for FifthDeclension {
    fn get_ending(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<&'static str> {
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

    fn decline(&self, entry: &RegularNoun, form: &NounAdjectiveForm) -> Option<String> {
        decline_from_endings(self, entry, form, &entry.nom[..entry.nom.len() - 2])
    }
}
