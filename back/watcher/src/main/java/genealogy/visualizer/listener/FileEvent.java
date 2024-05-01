package genealogy.visualizer.listener;

import java.io.File;
import java.util.EventObject;

public class FileEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public FileEvent(File source) {
        super(source);
    }

    public File getFile() {
        return (File) getSource();
    }
}
