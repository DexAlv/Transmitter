import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.*;
import java.net.*;

public class Sequence {

    static final int T = 100;

    private static final byte[][] BIT_LOOKUP = new byte[256][8];

    static {
        for (int val = 0; val < 256; val++) {
            for (int i = 0; i < 8; i++) {
                BIT_LOOKUP[val][i] = (byte) -((val >> (7 - i)) & 1);
            }
        }
    }

    public static CompletableFuture<byte[]> encryptChannelAsync(byte channel[], int width, int height, int T) {
        return CompletableFuture.supplyAsync(() -> ImageEncryption.encrypt(channel, width, height, T));
    }

    public static void main(String[] args) throws IOException {
        System.load("/home/angel/Descargas/opencv-4.11.0/build/lib/libopencv_java4110.so");

        Socket socket = new Socket("localhost", 5000);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        VideoCapture cap = new VideoCapture(0);

        if (!cap.isOpened()) {
            System.out.println("Camera isn't available");
            socket.close();
            return;
        }

        int height = (int) cap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int width = (int) cap.get(Videoio.CAP_PROP_FRAME_WIDTH);

        dos.writeInt(T);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.flush();

        int binWidth = width * 8;

        byte[] blueBin = new byte[binWidth * height];
        byte[] greenBin = new byte[binWidth * height];
        byte[] redBin = new byte[binWidth * height];

        Merger enc = new Merger(binWidth + 2, height + 2);
        FrameViewer originalViewer = null;
        FrameViewer encViewer = null;

        Mat frame = new Mat();
        while (true) {
            if (cap.read(frame)) {
                //long start = System.nanoTime();
                byte data[] = ImageUtils.matToByteArr(frame);

                if (originalViewer == null) {
                    originalViewer = new FrameViewer("Original", width, height);
                }

                originalViewer.updateFrame(ArrayHelpers.byteArrayToColorImage(data, width, height));

                IntStream.range(0, height).parallel().forEach(y -> {
                    for (int x = 0; x < width; x++) {
                        int index = (y * width + x) * 3;
                        int b = data[index] & 0xFF;
                        int g = data[index + 1] & 0xFF;
                        int r = data[index + 2] & 0xFF;

                        byte bb[] = BIT_LOOKUP[b];
                        byte gg[] = BIT_LOOKUP[g];
                        byte rr[] = BIT_LOOKUP[r];

                        int bitOffset = y * binWidth + x * 8;
                        for (int i = 0; i < 8; i++) {
                            blueBin[bitOffset + i] = bb[i];
                            greenBin[bitOffset + i] = gg[i];
                            redBin[bitOffset + i] = rr[i];
                        }
                    }
                });

                CompletableFuture<byte[]> blueFuture = encryptChannelAsync(blueBin, binWidth, height, T);
                CompletableFuture<byte[]> greenFuture = encryptChannelAsync(greenBin, binWidth, height, T);
                CompletableFuture<byte[]> redFuture = encryptChannelAsync(redBin, binWidth, height, T);

                CompletableFuture.allOf(blueFuture, greenFuture, redFuture).join();

                byte blueEnc[] = null;
                byte greenEnc[] = null;
                byte redEnc[] = null;

                try {
                    blueEnc = blueFuture.get();
                    greenEnc = greenFuture.get();
                    redEnc = redFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                dos.writeInt(blueEnc.length);
                dos.write(blueEnc);
                dos.write(greenEnc);
                dos.write(redEnc);
                dos.flush();

                enc.update(blueEnc, greenEnc, redEnc);

                if (encViewer == null) {
                    encViewer = new FrameViewer("Encrypted", binWidth + 2, height + 2);
                }
                encViewer.updateFrame(enc.image);

                /* long end = System.nanoTime();
                System.out.printf("Tiempo en procesar -> %fms\n", (end - start) /
                        1_000_000.0); */

            } else {
                break;
            }
        }
        cap.release();
        dos.close();
        socket.close();
    }
}