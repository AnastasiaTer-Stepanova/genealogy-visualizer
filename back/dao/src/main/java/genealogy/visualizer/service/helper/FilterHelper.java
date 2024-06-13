package genealogy.visualizer.service.helper;

import genealogy.visualizer.dto.FullNameFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {

    public static List<Predicate> addFullNameFilter(CriteriaBuilder cb, Root<?> root, FullNameFilterDTO fullName, String fieldName) {
        List<Predicate> predicates = new ArrayList<>();
        if (fullName != null) {
            if (fullName.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get(fieldName).get("name")), "%" + fullName.getName().toLowerCase() + "%"));
            }
            if (fullName.getLastName() != null) {
                predicates.add(cb.like(cb.lower(root.get(fieldName).get("lastName")), "%" + fullName.getLastName().toLowerCase() + "%"));
            }
            if (fullName.getSurname() != null) {
                predicates.add(cb.like(cb.lower(root.get(fieldName).get("surname")), "%" + fullName.getSurname().toLowerCase() + "%"));
            }
        }
        return predicates;
    }

    public static <T> Predicate addArchiveDocumentIdFilter(CriteriaBuilder cb, Root<T> root, Long archiveDocumentId) {
        if (archiveDocumentId != null) {
            Join<T, ArchiveDocument> join = root.join("archiveDocument", JoinType.LEFT);
            return cb.equal(join.get("id"), archiveDocumentId);
        }
        return null;
    }
}