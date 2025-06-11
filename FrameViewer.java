import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FrameViewer {
    private JFrame frame;
    private JLabel label;

    public FrameViewer(String title, int width, int height) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateFrame(BufferedImage image) {
        if (label.getIcon() == null || ((ImageIcon) label.getIcon()).getImage() != image) {
            label.setIcon(new ImageIcon(image));
        } else {
            label.repaint();
        }
        frame.repaint();
    }

}