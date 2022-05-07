use crate::token::Token;
use std::any::TypeId;
use std::fmt::Display;

#[derive(Clone)]
pub struct GenericGroup<T: 'static + Display + Clone> {
    children: Vec<T>,
}

impl<T: 'static + Display + Clone> GenericGroup<T> {
    pub fn new() -> GenericGroup<T> {
        GenericGroup::<T> {
            children: Vec::<T>::new(),
        }
    }

    pub fn add(&mut self, child: T) {
        self.children.push(child)
    }

    pub fn copy(&self) -> Vec<T> {
        self.children.clone()
    }

    pub fn borrow_mut(&mut self) -> &mut Vec<T> {
        &mut self.children
    }

    pub fn borrow(&self) -> &Vec<T> {
        &self.children
    }
}

impl<T: 'static + Display + Clone> Display for GenericGroup<T> {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        let id = TypeId::of::<T>();

        write!(
            f,
            "{}",
            if id == TypeId::of::<Phrase>() {
                "Sentence["
            } else if id == TypeId::of::<Token>() {
                "Phrase["
            } else if id == TypeId::of::<Sentence>() {
                "Document["
            } else {
                "UNKNOWN"
            }
            .to_string()
        )?;

        let mut first = true;
        for x in &self.children {
            if !first {
                write!(f, ", ");
            }

            x.fmt(f)?;
            first = false;
        }

        write!(f, "]")
    }
}

pub type Phrase = GenericGroup<Token>;
pub type Sentence = GenericGroup<Phrase>;
