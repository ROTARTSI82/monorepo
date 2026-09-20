#!/bin/bash
source env.sh
cargo check && cargo fix --allow-dirty --allow-staged && cargo clippy --fix --allow-dirty --allow-staged
cargo fmt
cargo build
cargo run
