package com.mx.util.library.image;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class ResizeImage {

    private static final int IMG_WIDTH = 600;
    private static final int IMG_HEIGHT = 600;
    private static final int MAX_IMG_SIZE = 1;

    public static String resize(String imageBase64) throws IOException {

        byte [] image = Base64.getDecoder().decode(imageBase64.getBytes(StandardCharsets.ISO_8859_1.name()));

        float imgSize = ((image.length / 1024f) / 1024f);
        if (imgSize > MAX_IMG_SIZE) {
            throw new IOException("Image size " + imgSize + " can't be greater than "+ MAX_IMG_SIZE +" MB");
        }

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image));
        String base64 = imageBase64;

        if (originalImage.getWidth() > IMG_WIDTH || originalImage.getHeight() > IMG_HEIGHT) {
            BufferedImage resizeImage = resizeImage(originalImage);
            base64 = imgToBase64String(resizeImage, "png");
        }

        return "data:image/png;base64,".concat(base64);
    }

    private static String imgToBase64String(RenderedImage img, String formatName) throws IOException{
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            throw new IOException(ioe);
        }
    }

    private static BufferedImage resizeImage(Image originalImage) {

        // scale image if needed
        Dimension dimension = getScaledDimension(
                originalImage.getWidth(null),
                originalImage.getHeight(null),
                IMG_WIDTH, IMG_HEIGHT
        );

        // resize with scaled dimension
        BufferedImage resizedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, dimension.width, dimension.height);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        AffineTransform scaleTransform = AffineTransform.getScaleInstance(
                dimension.width / (double) originalImage.getWidth(null),
                dimension.height /(double)  originalImage.getHeight(null)
        );

        g.setComposite(AlphaComposite.Src);
        g.drawImage(originalImage, scaleTransform, null);

        g.dispose();

        return resizedImage;
    }

    private static Dimension getScaledDimension(int originalWith, int originalHeight, int boundWith, int boundHeight) {

        int original_width = originalWith;
        int original_height = originalHeight;
        int bound_width = boundWith;
        int bound_height = boundHeight;
        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
