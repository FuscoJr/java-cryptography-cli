package texttransform;

import java.nio.charset.StandardCharsets;

public final class KeyDerivation
{
    private KeyDerivation() {}

    public static long seedFromKey(String key) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);

        long seed = 1469598103934665603L;
        for (byte b : bytes) {
            seed ^= (b & 0xff);
            seed *= 1099511628211L;
        }
        return seed;
    }
}
