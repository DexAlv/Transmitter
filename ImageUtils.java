import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

public class ImageUtils {

    public static byte[] matToByteArr(Mat mat) {
        int width = mat.width(), height = mat.height(), channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        return targetPixels;
    }

    public static void saveImage(BufferedImage img, String path) {
        try {
            ImageIO.write(img, "png", new File(path));
        } catch (Exception e) {
            System.out.println("Couldn't save image");
            e.printStackTrace();
            return;
        }
    }
}
