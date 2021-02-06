package imagescalerfx.utils;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Thread that processes the scaling of an image.
 * @author Jose Valera
 * @version 1.0
 * @since 14/11/2020
 */
public class ThreadScaler extends Thread{
    private ImageData image;
    private String folderPath;
    private ListView<ImageData> listViewImages;
    private long requiredMilliseconds;

    /**
     * Initialize a thread from the image and the list that it will
     * update when finished.
     * @param image Image to be scaled.
     * @param listViewImages List to be updated.
     */
    public ThreadScaler(ImageData image, ListView<ImageData> listViewImages) {
        this.image = image;
        this.listViewImages = listViewImages;
        folderPath = image.getPath().toString().substring(
                0, image.getPath().toString().lastIndexOf('.'));
        requiredMilliseconds = -1;
    }

    /**
     * Returns the image that scales.
     * @return Image that scales.
     */
    public ImageData getImage() {
        return image;
    }

    /**
     * Returns the milliseconds it took for the scales.
     * @return Milliseconds it took for the scales.
     */
    public long getRequiredMilliseconds() {
        return requiredMilliseconds;
    }

    /**
     * Look for a subfolder (inside images folder) with the same name than
     * its associated image and create again, the folder previously deleted.<br/>
     * Then, scale the image to different sizes: 10%, 20%, 30%, 40%, 50%,
     * 60%, 70%, 80% and 90% of original size.<br/>
     * Each resulting image must be stored in the associated subfolder,
     * with a prefix indicating the scaling factor. After all add the image’s
     * original filename to the list view.
     */
    @Override
    public void run() {
        if(new File(folderPath).exists())
            IOUtils.deleteDirectory(Path.of(folderPath));

        IOUtils.createDirectory(Path.of(folderPath));

        LocalDateTime startTime = LocalDateTime.now();

        for(int i = 10; i <= 90; i += 10)
            IOUtils.resize(image.getPath().toString(),
              folderPath + "\\" + i + "_" + image.getFileName(),
                           i / 100.0);

        requiredMilliseconds = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);

        Platform.runLater(() -> listViewImages.getItems().add(image));
    }
}
