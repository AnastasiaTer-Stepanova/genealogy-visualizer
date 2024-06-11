package genealogy.visualizer.controller;

import genealogy.visualizer.api.ArchiveApi;
import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.service.archive.ArchiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArchiveController implements ArchiveApi {

    private final ArchiveService archiveService;

    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        archiveService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyArchive>> filter(ArchiveFilter archiveFilter) {
        return ResponseEntity.ok(archiveService.filter(archiveFilter));
    }

    @Override
    public ResponseEntity<Archive> getById(Long id) {
        return ResponseEntity.ok(archiveService.getById(id));
    }

    @Override
    public ResponseEntity<Archive> save(Archive archive) {
        return ResponseEntity.ok(archiveService.save(archive));
    }

    @Override
    public ResponseEntity<Archive> update(Archive archive) {
        return ResponseEntity.ok(archiveService.update(archive));
    }
}
