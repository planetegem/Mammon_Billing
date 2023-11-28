package be.planetegem.mammon.util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JResizedImage {

    public BufferedImage source;
    public int width;
    public int height;
    public Image resized;

    public JResizedImage(String imagePath, int targetWidth, int targetHeight) throws IOException {
        this.source = ImageIO.read(new File(imagePath));

        // Resize image
        double imageWidth = this.source.getWidth();
        double imageHeight = this.source.getHeight();

        // Check what needs to be resized the most: width or height
        double widthRatio = targetWidth/imageWidth;
        double heightRatio = targetHeight/imageHeight;
        double finalRatio = Math.min(widthRatio, heightRatio);

        // Apply final ratio
        this.width = (int) Math.round(imageWidth*finalRatio);
        this.height = (int) Math.round(imageHeight*finalRatio);

        // Apply new width & height
        this.resized = this.source.getScaledInstance(this.width, this.height, java.awt.Image.SCALE_SMOOTH);
    }

    // Resize logic separated as static function; returns Dimension
    public static Dimension fitImage(BufferedImage image, int targetWidth, int targetHeight){
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double widthRatio = targetWidth/imageWidth;
        double heightRatio = targetHeight/imageHeight;
        double finalRatio = Math.min(widthRatio, heightRatio);

        int finalWidth = (int) Math.round(imageWidth*finalRatio);
        int finalHeight = (int) Math.round(imageHeight*finalRatio);

        Dimension result = new Dimension(finalWidth, finalHeight);
        return result;
    }

}
