package com.mx.util.library.image;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
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

    private static final int IMG_WIDTH = 184;
    private static final int IMG_HEIGHT = 184;
    private static final int MAX_IMG_SIZE = 1;

    public static String resize(String imageBase64, String imageExtension) throws IOException {

        byte [] image = Base64.getDecoder().decode(imageBase64.getBytes(StandardCharsets.ISO_8859_1.name()));

        float imgSize = ((image.length / 1024f) / 1024f);
        if (imgSize > MAX_IMG_SIZE) {
            throw new IOException("Image size " + imgSize + " can't be greater than 1MB");
        }

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image));
        int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizeImage = resizeImage(originalImage,type);
        String base64 = imgToBase64String(resizeImage, imageExtension);

        return "data:image/".concat(imageExtension).concat(";base64,").concat(base64);
    }

    private static String imgToBase64String(final RenderedImage img, final String formatName) throws IOException{
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            throw new IOException(ioe);
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }
}
