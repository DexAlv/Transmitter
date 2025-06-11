import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ArrayHelpers {
    public static BufferedImage byteArrayToGreyImage(byte data[], int width, int height) {
        if (data.length != width * height) {
            throw new IllegalArgumentException("Data size does not match dimensions");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(data, 0, target, 0, data.length);

        return image;
    }

    public static BufferedImage byteArrayToColorImage(byte data[], int width, int height) {
        if (data.length != width * height * 3) {
            throw new IllegalArgumentException("Data size does not match dimensions");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(data, 0, target, 0, data.length);

        return image;
    }

    public static BufferedImage MatrixToGreyImage(byte bits[][]) {
        int height = bits.length;
        int width = bits[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y * width + x] = bits[y][x];
            }
        }
        return image;
    }

    public static byte[] flatten2DByteArray(byte array2D[][]) {
        int height = array2D.length;
        int width = array2D[0].length;
        byte[] flat = new byte[height * width];

        for (int y = 0; y < height; y++) {
            System.arraycopy(array2D[y], 0, flat, y * width, width);
        }

        return flat;
    }

    public static BufferedImage int2DArrayToGrayImage(int bits[][]) {
        int height = bits.length;
        int width = bits[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = (byte) (bits[y][x] == 0 ? 0 : 255);
            }
        }

        return image;
    }

    public static BufferedImage mergeFromBinaryBytes(byte[] blueBits, byte[] greenBits, byte[] redBits, int width,
            int height) {
        int binWidth = width * 8;
        byte[] mergedData = new byte[width * height * 3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int byteOffset = (y * width + x) * 3;
                int bitOffset = (y * binWidth) + (x * 8);

                int blue = 0;
                int green = 0;
                int red = 0;

                for (int i = 0; i < 8; i++) {
                    blue = (blue << 1) | (blueBits[bitOffset + i] & 1);
                    green = (green << 1) | (greenBits[bitOffset + i] & 1);
                    red = (red << 1) | (redBits[bitOffset + i] & 1);
                }

                mergedData[byteOffset] = (byte) blue;
                mergedData[byteOffset + 1] = (byte) green;
                mergedData[byteOffset + 2] = (byte) red;
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] imageData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(mergedData, 0, imageData, 0, mergedData.length);

        return image;
    }

    public static byte[] reconstructChannelFromBits(byte[] bits, int width, int height) {
        if (bits.length != width * height) {
            throw new IllegalArgumentException("Bit array size does not match expected dimensions");
        }

        int origWidth = width / 8;
        byte[] channel = new byte[origWidth * height];

        for (int y = 0; y < height; y++) {
            for (int xByte = 0; xByte < origWidth; xByte++) {
                int val = 0;
                int bitIndex = y * width + xByte * 8;

                for (int i = 0; i < 8; i++) {
                    int bit = bits[bitIndex + i] == -1 ? 1 : 0;
                    val |= (bit << (7 - i));
                }

                channel[y * origWidth + xByte] = (byte) val;
            }
        }

        return channel;
    }

}