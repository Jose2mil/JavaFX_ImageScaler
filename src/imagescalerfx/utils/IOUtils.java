package imagescalerfx.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

/**
 *  Class in charge of get the information from the images to be processed,
 *  and scale the images to different sizes.
 *  @author Jose Valera
 *  @version 1.0
 *  @since 14/11/2020
 */
public class IOUtils
{
    private  static final String[] supportedFormats = {"JPEG", "JPG", "PNG", "BMP", "WEBMP", "GIF"};

    private static void resize(String inputImagePath,
                               String outputImagePath, int scaledWidth, int scaledHeight){
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        try {
            ImageIO.write(outputImage, formatName, new File(outputImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * It scale an input image to a given percentage.
     * @param inputImagePath Image to scale.
     * @param outputImagePath Resulting image, already scaled.
     * @param percent Scaling percentage.
     */
    public static void resize(String inputImagePath,
                              String outputImagePath, double percent) {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }

    /**
     * Recursively deletes the directory referenced by the given path,
     * including every files and folders that it may have.
     * @param path Directory to delete.
     */
    public static void deleteDirectory(Path path) {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new directory if the directory passed by reference
     * does not exist.
     * @param path Directory to be created.
     */
    public static void createDirectory(Path path) {
        if(! Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtains the images compatible with the scaler that are direct children of the folder passed by parameter
     * @param folderPath Directory from which you want to extract the images.
     * @return List of images.
     */
    public static List<ImageData> getImages(Path folderPath) {
        if(! Files.exists(folderPath))
            return new ArrayList<>();

        return Arrays.asList(
                    new File(String.valueOf(folderPath)).list())
                            .stream()
                            .map(p -> new ImageData(Path.of(folderPath + "\\" + p)))
                            .filter(i -> isSupportedImage(i))
                            .collect(Collectors.toList());
    }

    private static boolean isSupportedImage(ImageData imageData) {
        if(!Files.isDirectory(imageData.getPath()))
        {
            String extension =
                    imageData.getFileName()
                             .substring(imageData.getFileName().lastIndexOf(".") + 1)
                             .toUpperCase();
            if(Arrays.asList(supportedFormats).contains(extension))
                return true;
        }

        return false;
    }
}
