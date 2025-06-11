import java.util.stream.IntStream;

public class ImageEncryption {
    static byte[] encrypt(byte binaryBits[], int width, int height, int T) {
        byte mBits[][] = new byte[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mBits[y][x] = (byte) (binaryBits[y * width + x] >>> 7);
            }
        }

        byte bits[][] = addPadding(mBits);

        int h = bits.length;
        int w = bits[0].length;

        IntStream.iterate(0, y -> y < h - 1, y -> y + 2).parallel().forEach(y -> {
            for (int i = 0; i < T; i++) {
                byte temp[] = new byte[w - 1];
                for (int x = 1; x < w - 1; x++) {
                    temp[x - 1] = (byte) ((bits[y + 1][x - 1] + bits[y + 1][x + 1] + bits[y][x]) % 2);
                }

                for (int x = 0; x < w; x++) {
                    bits[y][x] = bits[y + 1][x];
                }

                for (int x = 1; x < w - 1; x++) {
                    bits[y + 1][x] = temp[x - 1];
                }
            }
        });

        return ArrayHelpers.flatten2DByteArray(bits);
    }

    static byte[][] addPadding(byte[][] mBits) {
        int h = mBits.length;
        int w = mBits[0].length;
        byte padded[][] = new byte[h + 2][w + 2];

        for (int y = 0; y < h; y++) {
            System.arraycopy(mBits[y], 0, padded[y + 1], 1, w);
        }
        return padded;
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