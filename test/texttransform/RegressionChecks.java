package texttransform;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Einfache Regressionstests ohne externe Bibliotheken.
// Kompilieren und starten: siehe README ("Regression checks").
public final class RegressionChecks
{
    private RegressionChecks() {}

    private static int failures = 0;

    public static void main(String[] args) {
        cipherCompatibility();
        cipherRoundTrips();
        cipherRejectsMalformedInput();
        cliMalformedInputThenValidOperation();
        cliInvalidOptionDoesNotAskForPassword();
        cliEndOfInputAtEveryPrompt();
        cliPreservesWhitespace();

        if (failures > 0) {
            System.out.println(failures + " Check(s) fehlgeschlagen.");
            System.exit(1);
        }
        System.out.println("Alle Checks bestanden.");
    }

    // Ausgabe der Version vor den CLI-Fixes: der Algorithmus darf sich nicht ändern.
    private static void cipherCompatibility() {
        String expected = "cvfijxzcwtwfwzlebg onayyazgenrvuHlkwmfpuzwruxonbieizkecwdjjcxzeoaneiomeidcnxvcl!pyzqdqpimhjpjwqnuveoepukzxhdwkowkp rheiiajbwfpbsgi";
        check("encode bleibt kompatibel", TextObfuscator.encode(" Hi! ", " pw ").equals(expected));
        check("decode bleibt kompatibel", TextObfuscator.decode(expected, " pw ").equals(" Hi! "));
    }

    private static void cipherRoundTrips() {
        String[] texts = {"", "hello", "Hallo Welt 42!", "  vorne und hinten  ", "\tTab\t", "äöü ß"};
        String[] keys = {"", " ", "pw", " pw "};
        for (String text : texts) {
            for (String key : keys) {
                String back = TextObfuscator.decode(TextObfuscator.encode(text, key), key);
                check("Round trip [" + text + "] mit Passwort [" + key + "]", back.equals(text));
            }
        }
    }

    private static void cipherRejectsMalformedInput() {
        try {
            TextObfuscator.decode("abc", "pw");
            check("decode lehnt falsche Länge ab", false);
        } catch (IllegalArgumentException e) {
            check("decode lehnt falsche Länge ab", true);
        }
    }

    private static void cliMalformedInputThenValidOperation() {
        String out = runCli("2\npw\nabc\n1\npw\nhi\n0\n");
        check("CLI zeigt Fehlermeldung bei falscher Länge",
                out.contains("Ungültige verschlüsselte Nachricht: die Länge muss ein Vielfaches von 26 sein."));
        check("CLI verschlüsselt danach weiter", lineAfter(out, "VERSCHLÜSSELT:").equals(TextObfuscator.encode("hi", "pw")));
    }

    private static void cliInvalidOptionDoesNotAskForPassword() {
        String out = runCli("5\n0\n");
        check("CLI meldet ungültige Auswahl", out.contains("Ungültige Auswahl."));
        check("CLI fragt nach ungültiger Auswahl kein Passwort", !out.contains("Password:"));

        out = runCli("5\n1\npw\nhi\n0\n");
        check("CLI nimmt nach ungültiger Auswahl die nächste Auswahl an",
                lineAfter(out, "VERSCHLÜSSELT:").equals(TextObfuscator.encode("hi", "pw")));
    }

    private static void cliEndOfInputAtEveryPrompt() {
        String[] inputs = {
                "",          // Auswahl
                "5\n",       // Auswahl nach ungültiger Auswahl
                "1\n",       // Passwort (Verschlüsseln)
                "1\npw\n",   // Nachricht
                "2\n",       // Passwort (Entschlüsseln)
                "2\npw\n",   // verschlüsselte Nachricht
        };
        for (String input : inputs) {
            try {
                runCli(input);
                check("CLI endet sauber bei Eingabeende nach [" + input.replace("\n", "\\n") + "]", true);
            } catch (RuntimeException e) {
                check("CLI endet sauber bei Eingabeende nach [" + input.replace("\n", "\\n") + "]: " + e, false);
            }
        }
    }

    private static void cliPreservesWhitespace() {
        String text = "  Hallo Welt  ";
        String key = " pw ";

        String encoded = lineAfter(runCli("1\n" + key + "\n" + text + "\n0\n"), "VERSCHLÜSSELT:");
        check("CLI behält Leerzeichen in Nachricht und Passwort", encoded.equals(TextObfuscator.encode(text, key)));

        String decoded = lineAfter(runCli("2\n" + key + "\n" + encoded + "\n0\n"), "Entschlüsselt");
        check("CLI Round trip mit Leerzeichen", decoded.equals(text));

        String windows = lineAfter(runCli("2\r\n" + key + "\r\n" + encoded + "\r\n0\r\n"), "Entschlüsselt");
        check("CLI Round trip mit Windows-Zeilenenden", windows.equals(text));

        String trimmedKey = lineAfter(runCli("2\n" + key.trim() + "\n" + encoded + "\n0\n"), "Entschlüsselt");
        check("CLI kürzt das Passwort nicht", !trimmedKey.equals(text));
    }

    private static String runCli(String input) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        App.run(new Scanner(input), out);
        return bytes.toString(StandardCharsets.UTF_8);
    }

    // Zeile direkt nach der ersten Zeile, die genau marker ist.
    private static String lineAfter(String output, String marker) {
        String[] lines = output.split("\\R", -1);
        for (int i = 0; i + 1 < lines.length; i++) {
            if (lines[i].equals(marker)) return lines[i + 1];
        }
        return "<" + marker + " nicht gefunden>";
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "OK    " : "FEHLER") + " " + name);
        if (!ok) failures++;
    }
}
