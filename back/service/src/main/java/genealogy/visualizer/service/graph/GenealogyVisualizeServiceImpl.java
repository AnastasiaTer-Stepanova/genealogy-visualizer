package genealogy.visualizer.service.graph;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.api.model.GraphLinks;
import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.PersonDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenealogyVisualizeServiceImpl implements GenealogyVisualizeService {

    private final PersonDAO personDAO;
    private final EasyPersonMapper easyPersonMapper;

    public GenealogyVisualizeServiceImpl(PersonDAO personDAO, EasyPersonMapper easyPersonMapper) {
        this.personDAO = personDAO;
        this.easyPersonMapper = easyPersonMapper;
    }

    @Override
    public GenealogyVisualizeGraph getGenealogyVisualizeGraph(GenealogyVisualizeRq genealogyVisualizeRq) {
        if (genealogyVisualizeRq != null && (genealogyVisualizeRq.getSort() != null || genealogyVisualizeRq.getFilter() != null)) {
            //TODO После добавления фильтрации изменить реализацию
            throw new BadRequestException("Фильтрация сейчас невозможна");
        }
        List<Person> entities;
        try {
            entities = personDAO.filter(new PersonFilterDTO(List.of("Person.withPartners", "Person.withChildren", "Person.withParents")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Family members not found");
        }
        Set<EasyPerson> easyPersonSet = easyPersonMapper.toSetDTOs(entities);
        return new GenealogyVisualizeGraph().persons(easyPersonSet).links(graphLinksSet(entities));
    }

    private Set<GraphLinks> graphLinksSet(List<Person> entities) {
        Set<GraphLinks> links = new HashSet<>();
        for (Person person : entities) {
            List<Person> parents = person.getParents();
            if (parents != null && !parents.isEmpty()) {
                parents.forEach(parent -> {
                    links.add(new GraphLinks().source(person.getId()).target(parent.getId()));
                    links.add(new GraphLinks().source(parent.getId()).target(person.getId()));
                });
            }
            List<Person> partners = person.getPartners();
            if (partners != null && !partners.isEmpty()) {
                partners.forEach(partner -> {
                    links.add(new GraphLinks().source(person.getId()).target(partner.getId()));
                    links.add(new GraphLinks().source(partner.getId()).target(person.getId()));
                });
            }
            List<Person> children = person.getChildren();
            if (children != null && !children.isEmpty()) {
                children.forEach(child -> {
                    links.add(new GraphLinks().source(person.getId()).target(child.getId()));
                    links.add(new GraphLinks().source(child.getId()).target(person.getId()));
                });
            }
        }
        return links;
    }
}
