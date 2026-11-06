package texttransform;

import java.util.Random;

public final class TextObfuscator {
    private TextObfuscator() {}

    public static String encode(String plain, String key) {
        long seed = KeyDerivation.seedFromKey(key);
        Random rng = new Random(seed);

        StringBuilder out = new StringBuilder(plain.length() * CipherConfig.BLOCK_LEN);

        for (int i = 0; i < plain.length(); i++) {
            char ch = plain.charAt(i);

            // Entscheiden Ort der echter Char sein wird (0..25)
            int specialPos = rng.nextInt(CipherConfig.BLOCK_LEN);

            // Block erfinden und mit Random-Char fühlen.
            char[] block = new char[CipherConfig.BLOCK_LEN];
            for (int j = 0; j < CipherConfig.BLOCK_LEN; j++) {
                block[j] = randomFillChar(rng);
            }

            // Real Char in Spezieller Position hinfügen.
            block[specialPos] = ch;

            // Gesamte Block an Result addieren
            out.append(block);
        }
        return out.toString();
    }

    public static String decode(String cipher, String key) {
        if (cipher.length() % CipherConfig.BLOCK_LEN != 0) {
            throw new IllegalArgumentException("Ungültige verschlüsselte Nachricht: die Länge muss ein Vielfaches von " + CipherConfig.BLOCK_LEN + " sein.");
        }

        long seed = KeyDerivation.seedFromKey(key);
        Random rng = new Random(seed);

        int blocks = cipher.length() / CipherConfig.BLOCK_LEN;
        StringBuilder out = new StringBuilder(blocks);

        for (int i = 0; i < blocks; i++) {
            // Gleiche Spezial position berechnen als beim Verschlüsseln
            int specialPos = rng.nextInt(CipherConfig.BLOCK_LEN);

            //WICHTIG: RNG gleich benutzen, als bei Verschlüsselung um Sync zu haben.
            //Bei Verschlüsselung, für jeden block wird randomFillChar() 26 mal gerufen.
            //Die Char sind hier nicht nötig aber die 26 Mal rufen.
            for (int j = 0; j < CipherConfig.BLOCK_LEN; j++) {
                rng.nextInt(CipherConfig.ALPHABET.length());
            }

            // Block i lesen.
            int start = i * CipherConfig.BLOCK_LEN;
            int end = start + CipherConfig.BLOCK_LEN;
            String blockStr = cipher.substring(start, end);

            //Echter Char von Spezial position rausholen.
            out.append(blockStr.charAt(specialPos));
        }
        return out.toString();

    }

    private static char randomFillChar(Random rng) {
        int idx = rng.nextInt(CipherConfig.ALPHABET.length());
        return CipherConfig.ALPHABET.charAt(idx);
    }
}
