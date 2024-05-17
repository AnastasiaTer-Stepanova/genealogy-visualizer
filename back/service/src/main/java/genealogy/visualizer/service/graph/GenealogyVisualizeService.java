package genealogy.visualizer.service.graph;

import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;

public interface GenealogyVisualizeService {

    GenealogyVisualizeGraph getGenealogyVisualizeGraph(GenealogyVisualizeRq genealogyVisualizeRq);
}
