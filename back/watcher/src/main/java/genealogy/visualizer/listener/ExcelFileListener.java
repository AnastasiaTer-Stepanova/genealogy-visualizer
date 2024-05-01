package genealogy.visualizer.listener;

import genealogy.visualizer.parser.FileParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Лисенер excel файлов, если пришел excel файл пытается его спарсить
 */
public class ExcelFileListener implements FileListener {

    private static final Logger LOGGER = LogManager.getLogger(ExcelFileListener.class);

    private final FileParser archiveDocumentExcelParser;

    public ExcelFileListener(FileParser archiveDocumentExcelParser) {
        this.archiveDocumentExcelParser = archiveDocumentExcelParser;
    }

    @Override
    public void onCreated(FileEvent event) {
        File f = event.getFile();
        try {
            archiveDocumentExcelParser.parse(f);
        } catch (Exception e) {
            LOGGER.error("Error parsing file {}", f.getAbsolutePath(), e);
        }
    }

    @Override
    public void onModified(FileEvent event) {
    }

    @Override
    public void onDeleted(FileEvent event) {
    }

    @Override
    public boolean check(FileEvent event) {
        File file = event.getFile();
        if (!file.getName().endsWith(".xlsx")) {
            LOGGER.info("File {} is not a XLSX file", file.getAbsolutePath());
            return false;
        }
        return true;
    }
}
