package genealogy.visualizer.watcher;

import genealogy.visualizer.listener.FileEvent;
import genealogy.visualizer.listener.FileListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Реализация наблюдателя за папкой, при изменении файлов в папке вызывает события лисенеров
 */
public class FileWatcher implements Watcher {
    private static final Logger LOGGER = LogManager.getLogger(FileWatcher.class);

    private final List<FileListener> listeners;
    private final File folder;

    public FileWatcher(File folder, List<FileListener> listeners) {
        this.folder = folder;
        this.listeners = listeners;
    }

    @Override
    public void watch() {
        LOGGER.info("Watching file {}", folder.getAbsolutePath());
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(folder.getAbsolutePath());
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            LOGGER.error(e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());
        }
        return key.reset();
    }

    private void notifyListeners(WatchEvent.Kind<?> kind, File file) {
        FileEvent event = new FileEvent(file);
        for (FileListener listener : listeners) {
            if (listener.check(event)) {
                if (ENTRY_CREATE.equals(kind)) {
                    listener.onCreated(event);
                } else if (ENTRY_MODIFY.equals(kind)) {
                    listener.onModified(event);
                } else if (ENTRY_DELETE.equals(kind)) {
                    listener.onDeleted(event);
                }
            }
        }
    }
}