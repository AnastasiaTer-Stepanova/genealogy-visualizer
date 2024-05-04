package genealogy.visualizer.service;

import genealogy.visualizer.entity.GodParent;

import java.util.List;

public interface GodParentDAO {

   List<GodParent> saveBatch(Iterable<GodParent> godParents);
}
