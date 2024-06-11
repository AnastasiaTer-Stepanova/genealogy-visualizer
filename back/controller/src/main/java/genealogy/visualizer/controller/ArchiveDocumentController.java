package genealogy.visualizer.controller;

import genealogy.visualizer.api.ArchiveDocumentApi;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.service.archive.ArchiveDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArchiveDocumentController implements ArchiveDocumentApi {

    private final ArchiveDocumentService archiveDocumentService;

    public ArchiveDocumentController(ArchiveDocumentService archiveDocumentService) {
        this.archiveDocumentService = archiveDocumentService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        archiveDocumentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyArchiveDocument>> filter(ArchiveDocumentFilter archiveDocumentFilter) {
        return ResponseEntity.ok(archiveDocumentService.filter(archiveDocumentFilter));
    }

    @Override
    public ResponseEntity<ArchiveDocument> getById(Long id) {
        return ResponseEntity.ok(archiveDocumentService.getById(id));
    }

    @Override
    public ResponseEntity<ArchiveDocument> save(ArchiveDocument archiveDocument) {
        return ResponseEntity.ok(archiveDocumentService.save(archiveDocument));
    }

    @Override
    public ResponseEntity<ArchiveDocument> update(ArchiveDocument archiveDocument) {
        return ResponseEntity.ok(archiveDocumentService.update(archiveDocument));
    }
}
