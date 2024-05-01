package genealogy.visualizer.config;

import genealogy.visualizer.listener.ExcelFileListener;
import genealogy.visualizer.listener.FileListener;
import genealogy.visualizer.parser.FileParser;
import genealogy.visualizer.watcher.FileWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Collections;

@Configuration
@ComponentScan
public class WatcherConfig {

    @Value("${params.path.file.excel}")
    String pathExcelFile;

    @Bean
    public FileListener excelFileListener(@Autowired FileParser archiveDocumentExcelParser) {
        return new ExcelFileListener(archiveDocumentExcelParser);
    }

    @Bean
    public FileWatcher pathWatcher(FileListener excelFileListener) {
        return new FileWatcher(new File(pathExcelFile), Collections.singletonList(excelFileListener));
    }
}
