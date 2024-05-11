package genealogy.visualizer.service.graph;

import genealogy.visualizer.api.model.GenealogyVisualizeResponse;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;

public interface GenealogyVisualizeService {

    GenealogyVisualizeResponse getGenealogyVisualizeGraph(GenealogyVisualizeRq genealogyVisualizeRq);
}
