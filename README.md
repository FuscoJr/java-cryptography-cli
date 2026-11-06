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
javac -encoding UTF-8 -d out src/texttransform/*.java
java -cp out texttransform.App
```

The current menu is in German:

- `1`: encode text using a password.
- `2`: decode text using the same password.
- `0`: exit.

Invalid menu choices are rejected before a password is requested. Encoded input with the wrong length shows an error message and returns to the menu. The program exits cleanly when input ends, for example at the end of piped input. Messages and passwords are used exactly as typed, including leading and trailing spaces.

## Regression checks

The checks are a small program without external libraries. From the repository root:

```sh
javac -encoding UTF-8 -d out src/texttransform/*.java test/texttransform/*.java
java -cp out texttransform.RegressionChecks
```

Each check prints one line. The program exits with status 1 if any check fails.

## Structure

All classes are in the package `texttransform`.

- `src/texttransform/App`: terminal menu and user input.
- `src/texttransform/CipherConfig`: block size and filler alphabet.
- `src/texttransform/KeyDerivation`: converts the password into a seed.
- `src/texttransform/TextObfuscator`: encoding and decoding logic.
- `test/texttransform/RegressionChecks`: dependency-free checks for the cipher and the CLI.

## Known limitations

- The custom transformation provides no cryptographic security.
- Only encoded input with the wrong length is detected. Other edits to the encoded text go unnoticed, and a wrong password silently produces unrelated text.
- Characters outside `a-z`, including uppercase letters, digits, spaces and punctuation, remain visible in the encoded output.

## Licence

MIT. See [LICENSE](LICENSE).
