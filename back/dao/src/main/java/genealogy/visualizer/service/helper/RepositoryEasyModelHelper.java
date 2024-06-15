package genealogy.visualizer.service.helper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Используется только для облегченных моделей, у которых нет вложенных entity
 *
 * @param <E> entity
 */
public class RepositoryEasyModelHelper<E> {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public E saveEntityIfNotExist(E entity, Long id, JpaRepository<E, Long> repository) {
        return id != null ?
                repository.findById(id).orElseThrow() :
                repository.save(entity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<E> saveEntitiesIfNotExist(List<E> entities, JpaRepository<E, Long> repository, Function<E, Long> getIdFunction) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<E> result = new ArrayList<>();
        for (E entity : entities) {
            result.add(saveEntityIfNotExist(entity, getIdFunction.apply(entity), repository));
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<E> updateEntities(Long parentId, List<E> entities, List<E> newEntities, Function<E, Long> getIdFunction,
                                  JpaRepository<E, Long> repository, BiConsumer<Long, Long> updateIdFunction) {
        newEntities = saveEntitiesIfNotExist(newEntities, repository, getIdFunction);
        if (entities == null || entities.isEmpty()) {
            newEntities.forEach(entity -> updateIdFunction.accept(getIdFunction.apply(entity), parentId));
            return newEntities;
        }
        Set<Long> newIds = newEntities != null ?
                newEntities.stream().map(getIdFunction).collect(Collectors.toSet()) :
                Collections.emptySet();
        Set<Long> existIds = entities.stream().map(getIdFunction).collect(Collectors.toSet());

        Set<Long> idsForDelete = existIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
        idsForDelete.forEach(id -> updateIdFunction.accept(id, null));

        List<E> result = entities.stream()
                .filter(entity -> !idsForDelete.contains(getIdFunction.apply(entity)))
                .collect(Collectors.toList());
        if (newEntities != null) {
            newEntities.stream()
                    .filter(entity -> newIds.contains(getIdFunction.apply(entity)) && !existIds.contains(getIdFunction.apply(entity)))
                    .forEach(entity -> {
                        updateIdFunction.accept(getIdFunction.apply(entity), parentId);
                        result.add(entity);
                    });
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<E> updateEntity(Long parentId, E entity, E newEntity, Function<E, Long> getIdFunction,
                                    JpaRepository<E, Long> repository, BiConsumer<Long, Long> updateIdFunction) {
        if (newEntity != null) {
            newEntity = saveEntityIfNotExist(newEntity, getIdFunction.apply(newEntity), repository);
        }
        Long newId = newEntity != null ? getIdFunction.apply(newEntity) : null;
        Long existId = entity != null ? getIdFunction.apply(entity) : null;

        if (!Objects.equals(existId, newId)) {
            if (existId != null && updateIdFunction != null) {
                updateIdFunction.accept(existId, null);
            }
            if (newId != null && updateIdFunction != null) {
                updateIdFunction.accept(newId, parentId);
            }
        }

        E result = newEntity != null ? newEntity : entity;
        return result != null ? Optional.of(result) : Optional.empty();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<List<E>> updateEntitiesWithLinkTable(Long parentId, List<E> entities, List<E> newEntities, Function<E, Long> getIdFunction,
                                                         JpaRepository<E, Long> repository, BiConsumer<Long, Long> deleteLinkFunction,
                                                         BiConsumer<Long, Long> insertLinkFunction) {
        newEntities = saveEntitiesIfNotExist(newEntities, repository, getIdFunction);
        if (entities == null || entities.isEmpty()) {
            newEntities.forEach(entity -> insertLinkFunction.accept(getIdFunction.apply(entity), parentId));
            return Optional.of(newEntities);
        }

        Set<Long> newIds = newEntities != null ? newEntities.stream().map(getIdFunction).collect(Collectors.toSet()) : Collections.emptySet();
        Set<Long> existIds = entities.stream().map(getIdFunction).collect(Collectors.toSet());

        Set<Long> idsForDelete = existIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
        idsForDelete.forEach(id -> deleteLinkFunction.accept(id, parentId));

        List<E> result = entities.stream().filter(entity -> !idsForDelete.contains(getIdFunction.apply(entity))).collect(Collectors.toList());

        if (newEntities != null) {
            newEntities.stream().filter(entity -> newIds.contains(getIdFunction.apply(entity)) && !existIds.contains(getIdFunction.apply(entity)))
                    .forEach(entity -> {
                        insertLinkFunction.accept(getIdFunction.apply(entity), parentId);
                        result.add(entity);
                    });
        }
        return Optional.of(result);
    }
}
