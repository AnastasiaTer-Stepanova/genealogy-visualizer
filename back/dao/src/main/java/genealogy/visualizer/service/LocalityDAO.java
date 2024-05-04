package genealogy.visualizer.service;

import genealogy.visualizer.entity.Locality;

public interface LocalityDAO {

    Locality saveOrFindIfExist(Locality archiveDocument);

}
