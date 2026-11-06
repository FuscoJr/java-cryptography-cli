package com.raffa.crypto;

import java.io.PrintStream;
import java.util.Scanner;

public class App
{
    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner input, PrintStream out) {
        while (true) {
            out.println("************************************");
            out.println("------------Cryptograph-------------");
            out.println("************************************");
            out.println("1) Verschlüsseln");
            out.println("2) Entschlüsseln");
            out.println("0) Schliessen");
            out.println("Auswahl: ");
            String opt = readLine(input);

            if (opt == null || opt.equals("0")) return;

            if (!opt.equals("1") && !opt.equals("2")) {
                out.println("Ungültige Auswahl.");
                continue;
            }

            out.println("Password: ");
            String key = readLine(input);
            if (key == null) return;

            if (opt.equals("1")) {
                out.println("Nachricht: ");
                String plain = readLine(input);
                if (plain == null) return;
                out.println("\nVERSCHLÜSSELT:\n" + RaffaCipher.encode(plain,key));
            } else {
                out.println("Verschlüsselte Nachricht: ");
                String cipher = readLine(input);
                if (cipher == null) return;
                try {
                    out.println("\nEntschlüsselt\n" + RaffaCipher.decode(cipher,key));
                } catch (IllegalArgumentException e) {
                    out.println("\n" + e.getMessage());
                }
            }
        }
    }

    // Liefert die Zeile unverändert (ohne trim) oder null, wenn die Eingabe zu Ende ist.
    private static String readLine(Scanner input) {
        return input.hasNextLine() ? input.nextLine() : null;
    }
}
