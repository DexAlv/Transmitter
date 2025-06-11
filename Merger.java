import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Merger {
    private byte blueBits[];
    private byte greenBits[];
    private byte redBits[];
    private int width, height;
    public byte merged[];
    BufferedImage image = null;

    public Merger(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void update(byte[] blue, byte[] green, byte[] red) {
        this.blueBits = blue;
        this.greenBits = green;
        this.redBits = red;

        this.merged = mergeToArr();
        this.image = mergeToImage();
    }

    /* private byte[] mergeToArr() {
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

        return mergedData;
    } */

    private byte[] mergeToArr() {
        int size = width * height * 3;
        byte output[] = new byte[size];

        for (int i = 0; i < width * height; i++) {
            output[i * 3] = blueBits[i];
            output[i * 3 + 1] = greenBits[i];
            output[i * 3 + 2] = redBits[i];
        }

        return output;
    }

    private BufferedImage mergeToImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte buffer[] = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(merged, 0, buffer, 0, merged.length);
        return image;
    }
}
