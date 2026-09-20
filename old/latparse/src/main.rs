use latparse::lower_no_macrons;
use latparse::nouns::common::{NounAdjectiveForm, RegularDecliner, RegularNoun};
use latparse::nouns::first_declension::{
    FifthDeclension, FirstDeclension, FourthDeclension, SecondDeclension, ThirdDeclension,
};
use latparse::Case::{Ablative, Accusative, Dative, Genitive, Locative, Nominative, Vocative};
use latparse::Gender::Masculine;
use latparse::Number::{Plural, Singular};

use latparse::{Declension, Gender};

use std::io;

// is_greek = entry.is_greek ||

fn main() -> Result<(), io::Error> {
    let args = std::env::args().collect::<Vec<String>>();
    let err = || io::Error::new(io::ErrorKind::Other, "Wrong number of args");

    let gen_str = args.get(3).ok_or_else(err)?;
    let mut noun = RegularNoun {
        declension: Declension::Irregular,
        gender: if gen_str == "masc" {
            Gender::Masculine
        } else if gen_str == "fem" {
            Gender::Feminine
        } else if gen_str == "neu" {
            Gender::Neuter
        } else {
            return Err(io::Error::new(io::ErrorKind::Other, "Invalid gender"));
        },
        is_greek: false,
        nom: args.get(1).ok_or_else(err)?.to_string(),
        gen: args.get(2).ok_or_else(err)?.to_string(),
    };

    let mut elem: Option<String> = None;
    let declensions: Vec<(Box<dyn RegularDecliner>, Declension)> = vec![
        (Box::new(FirstDeclension()), Declension::First),
        (Box::new(SecondDeclension()), Declension::Second),
        (Box::new(ThirdDeclension()), Declension::Third),
        (Box::new(FourthDeclension()), Declension::Fourth),
        (Box::new(FifthDeclension()), Declension::Fifth),
    ];

    for decl in &declensions {
        elem = decl.0.decline(
            &noun,
            &NounAdjectiveForm {
                gender: Masculine,
                case: Nominative,
                number: Singular,
            },
        );

        if elem.is_some() {
            noun.declension = decl.1;
            break;
        }
    }

    elem.ok_or_else(|| io::Error::new(io::ErrorKind::Other, "Unknown declension"))?;

    {
        let nom = lower_no_macrons(&noun.nom);

        // is_greek is only checked by 2nd declension for now.
        // this will need to be updated when more code relies on is_greek
        noun.is_greek = matches!(noun.declension, Declension::Second)
            && (nom.ends_with("os") || nom.ends_with("on"));
    }

    for case in [
        Nominative, Genitive, Dative, Accusative, Ablative, Vocative, Locative,
    ] {
        for number in [Singular, Plural] {
            let form = NounAdjectiveForm {
                gender: noun.gender,
                case,
                number,
            };

            elem = declensions
                .get(noun.declension as usize - 1)
                .and_then(|x| x.0.decline(&noun, &form));
            print!("{}\t", elem.unwrap_or_else(|| "--".to_owned()));
        }
        println!();
    }

    Ok(())
}
