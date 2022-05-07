mod document;
mod grouping;
mod scanner;
mod token;

use crate::grouping::{Phrase, Sentence};
use document::*;
use scanner::Scanner;
use std::fs::File;
use std::io::ErrorKind::Other;
use std::io::{BufReader, Error, ErrorKind};

fn main() -> Result<(), std::io::Error> {
    let args = std::env::args().collect::<Vec<String>>();
    if args.len() != 2 {
        eprintln!("ERROR! Usage: FindAuthor [file]");
        return Err(std::io::Error::new(ErrorKind::Other, "Invalid arguments"));
    }

    let weights = [11.0, 33.0, 50.0, 0.4, 4.0];
    let doc = Document::new(Scanner::new(Box::new(File::open(args.get(1).unwrap())?)));
    let feats = doc.get_features();
    let id = feats.accumulate(&weights);

    // println!("{}", doc);
    println!("Trying to identify: {:?} = {}\n", feats, id);

    let best = vec![
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
            eprintln!("Failed to load database: {}", e);
            false
        }
        _ => true,
    })
    .map(|x| {
        let ret = x.unwrap();
        let score = ret.accumulate(&weights);
        let diff = score - id;

        println!("{:?} = {} (delta {})", ret, score, diff);

        (ret, score, diff.abs())
    })
    .reduce(|a, b| if a.2 < b.2 { a } else { b })
    .ok_or_else(|| Error::new(Other, "reduce() failed??"))?;

    println!("\nBEST MATCH: {:?} = {} (diff {})", best.0, best.1, best.2);

    Ok(())
}
