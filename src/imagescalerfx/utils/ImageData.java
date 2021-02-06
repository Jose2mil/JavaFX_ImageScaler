package imagescalerfx.utils;

import java.nio.file.Path;

/**
 * Class in charge of storing image data.
 * @author Jose Valera
 * @version 1.0
 * @since 14/11/2020
 */
public class ImageData {
    private String fileName;
    private Path path;

    private ImageData(String fileName, Path path) {
        this.fileName = fileName;
        this.path = path;
    }

    /**
     * Initialize an ImageData with the path passed by reference,
     * extracting the image number from the same.
     * @param path Path object containing the full path to the image.
     */
    public ImageData(Path path) {
        this(path.toString().substring(path.toString().lastIndexOf("\\") + 1),
             path);
    }

    /**
     * Return a String containing a the file name of the image, such as
     * picture.jpg, for instance.
     * @return file name of the image.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Return a Path object containing the full path to the image.
     * @return full path to the image.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Return image name (for the list views).
     * @return image name.
     */
    @Override
    public String toString() {
        return fileName;
    }
}
