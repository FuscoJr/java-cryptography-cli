# Java Cryptography CLI

An educational Java command-line project exploring a custom reversible text transformation.

This project was created to practise Java, strings, deterministic pseudo-random sequences and separating responsibilities across classes.

**This is not secure encryption. Do not use it to protect passwords, personal information or other sensitive data.**

## How it works

A password is converted into a numeric seed. For each character in the input, the program creates a 26-character block containing random filler and the original character.

Using the same password reproduces the sequence of positions needed to recover the original text.

The transformation is deterministic and increases the text length substantially. It does not authenticate the result or reliably detect an incorrect password or modified input.

## Requirements

- A JDK with `java` and `javac` available in your terminal.
- No external libraries.

The commands below were verified with JDK 21.

## Compile and run

From the repository root:

```sh
javac -encoding UTF-8 -d out src/com/raffa/crypto/*.java
java -cp out com.raffa.crypto.App
```

The current menu is in German:

- `1`: encode text using a password.
- `2`: decode text using the same password.
- `0`: exit.

## Structure

- `App`: terminal menu and user input.
- `CipherConfig`: block size and filler alphabet.
- `KeyDerivation`: converts the password into a seed.
- `RaffaCipher`: encoding and decoding logic.

## Known limitations

- The custom transformation provides no cryptographic security.
- Malformed encoded input can terminate the CLI with an exception.
- The repository does not yet contain automated tests.

## Licence

MIT. See [LICENSE](LICENSE).
