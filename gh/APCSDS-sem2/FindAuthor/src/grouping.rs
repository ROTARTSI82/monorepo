//! Defines groupings of [`Token`]s in order to form
//! a hierarchy from a text. The [`GenericGroup`]ings
//! can be used to form a tree from the [`Token`]s.
//!
//! [`Token`]: crate::token::Token

use crate::token::Token;
use std::any::TypeId;
use std::fmt::Display;

/// A generic wrapping a vector of some object `T`
#[derive(Clone)]
pub struct GenericGroup<T: 'static + Display + Clone> {
    children: Vec<T>,
}

impl<T: 'static + Display + Clone> GenericGroup<T> {
    /// Constructs a new empty [`GenericGroup`] containing nothing.
    pub fn new() -> GenericGroup<T> {
        GenericGroup::<T> {
            children: Vec::<T>::new(),
        }
    }

    /// Adds an element to this group at the end of the vector in amortized `O(1)`
    pub fn add(&mut self, child: T) {
        self.children.push(child)
    }

    /// Returns a deep copy of the internal vector in `O(n)`, with `n`
    /// being the total size of the vector.
    #[allow(dead_code)]
    pub fn copy(&self) -> Vec<T> {
        self.children.clone()
    }

    /// Gets an immutable reference to the internal vector for
    /// direct manipulation
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
        )?;

        let mut first = true;
        for x in &self.children {
            if !first {
                write!(f, ", ")?;
            }

            x.fmt(f)?;
            first = false;
        }

        write!(f, "]")
    }
}

/// A [`Phrase`] is a [`GenericGroup`] of [`Token`]s with
/// [`TokenType::Word`][`crate::token::TokenType::Word`].
/// [`Phrase`]s are components of [`Sentence`]s and [`Document`]s.
///
/// [`Sentence`]: crate::grouping::Sentence
/// [`Document`]: crate::document::Document
pub type Phrase = GenericGroup<Token>;

/// A [`Sentence`] is a [`GenericGroup`] of [`Phrase`]s.
/// [`Sentence`]s are components of [`Document`]s.
///
/// [`Document`]: crate::document::Document
pub type Sentence = GenericGroup<Phrase>;
