mod document;
mod grouping;
mod scanner;
mod token;

use document::*;
use scanner::Scanner;
use std::fs::File;
use std::io::ErrorKind::Other;
use std::io::{Error, ErrorKind};

fn main() -> Result<(), Error> {
    if std::env::args().len() < 2 {
        eprintln!("ERROR! Usage: FindAuthor [files]");
        eprintln!("Must have 1 or more arguments");
        return Err(std::io::Error::new(ErrorKind::Other, "Invalid arguments"));
    }

    let weights = [11.0, 33.0, 50.0, 0.4, 4.0];

    let database = vec![
        "agatha.christie",
        "alexandre.dumas",
        "brothers.grim",
        "charles.dickens",
        "emily.bronte",
        "fyodor.dostoevsky",
        "james.joyce",
        "jane.austen",
        "lewis.caroll",
        "mark.twain",
        "sir.arthur.conan.doyle",
        "william.shakespeare",
    ]
    .into_iter()
    .map(|x| LinguisticFeatures::from_file(format!("./SignatureFiles/{}.stats", x).as_str()))
    .filter(|x| match x {
        Err(e) => {
            eprintln!("Failed to load database element: {}", e);
            false
        }
        _ => true,
    });

    for result in std::env::args()
        .skip(1)
        .map(|x| -> Result<f64, Error> {
            let doc = Document::new(Scanner::new(Box::new(File::open(x)?)));
            let feats = doc.get_features();
            let id = feats.accumulate(&weights);

            println!("Trying to identify: {:?} = {}\n", feats, id);
            Ok(id)
        })
        .filter(|x| match x {
            Err(e) => {
                eprintln!("Failed to load file: {}", e);
                false
            }
            Ok(_) => true,
        })
        .map(|id| -> Result<(), Error> {
            let id = id.unwrap();

            let best = database
                .clone()
                .map(|x| {
                    let ret = x.unwrap();
                    let score = ret.accumulate(&weights);
                    let diff = score - id;

                    println!("Candidate {:?} = {} (delta {})", ret, score, diff);

                    (ret, score, diff.abs())
                })
                .reduce(|a, b| if a.2 < b.2 { a } else { b })
                .ok_or_else(|| Error::new(Other, "reduce() failed??"))?;

            println!(
                "\nBEST MATCH: {:?} = {} (diff {})\n\n",
                best.0, best.1, best.2
            );
            Ok(())
        })
    {
        result?;
    }

    Ok(())
}
