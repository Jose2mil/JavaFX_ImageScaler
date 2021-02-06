package imagescalerfx.utils;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class that check every second how many threads have finished in the
 * executor, and update bottom status label with current result.
 * @author Jose Valera
 * @version 1.0
 * @since 14/11/2020
 */
public class StatusService extends ScheduledService<Boolean> {
    private ThreadPoolExecutor executor;
    private Label labelStatus;
    private Control[] controlsToBlock;

    /**
     * Constructor that initializes a service from the executor that it
     * inspects, the label that it updates, the controls that it blocks,
     * and the delay and period.
     * @param executor Executor that it inspects.
     * @param labelStatus Label that it updates.
     * @param controlsToBlock Control to block array.
     * @param delay Delay duration.
     * @param period Period duration.
     */
    public StatusService(ThreadPoolExecutor executor, Label labelStatus, Control[] controlsToBlock, Duration delay, Duration period) {
        this.executor = executor;
        this.labelStatus = labelStatus;
        this.controlsToBlock = controlsToBlock;

        setDelay(delay); // Will start after 0.5s
        setPeriod(period); // Runs every second after

        setOnSucceeded(e -> {
            if (getValue()) {
                // Executor finished
                cancel(); // Cancel service (stop it).
                Arrays.stream(controlsToBlock).forEach(c -> c.setDisable(false));
            }
        });
    }

    /**
     * Set a new executor.
     * @param executor New Executor.
     */
    public void setNewExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected Task<Boolean> createTask()
    {
        return new Task<Boolean>()
        {
            @Override
            protected Boolean call() throws Exception
            {
                Platform.runLater(() -> {
                    labelStatus.setText(executor.getCompletedTaskCount()
                            + " of "
                            + executor.getTaskCount()
                            + " tasks finished.");
                });

                return executor.isTerminated();
            }
        };
    }
}
