package genealogy.visualizer.listener;

import java.util.EventListener;

/**
 * Интерфейс лисенера для обработки событий происходящих с файлом в зависимости от ивента
 */
public interface FileListener extends EventListener {

    void onCreated(FileEvent event);

    void onModified(FileEvent event);

    void onDeleted(FileEvent event);

    boolean check(FileEvent event);
}
