import java.util.stream.IntStream;

public class ImageDecryption {
    static byte[][] decrypt(byte bits[][], int T) {
        int h = bits.length;
        int w = bits[0].length;
        byte[][] dBits = new byte[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                dBits[y][x] = bits[y][x];
            }
        }

        IntStream.iterate(0, y -> y < h - 1, y -> y + 2).parallel().forEach(y -> {
            byte[] temp = new byte[w - 1];
            for (int i = 0; i < T; i++) {
                for (int x = 1; x < w - 1; x++) {
                    temp[x - 1] = (byte) ((dBits[y][x - 1] + dBits[y][x + 1] + dBits[y + 1][x]) % 2);
                }

                for (int x = 0; x < w; x++) {
                    dBits[y + 1][x] = dBits[y][x];
                }

                for (int x = 1; x < w - 1; x++) {
                    dBits[y][x] = temp[x - 1];
                }
            }
        });

        return removePadding(dBits);
    }

    static byte[][] removePadding(byte[][] padded) {
        int h = padded.length - 2;
        int w = padded[0].length - 2;
        byte[][] original = new byte[h][w];

        for (int y = 0; y < h; y++) {
            System.arraycopy(padded[y + 1], 1, original[y], 0, w);
        }

        return original;
    }
}