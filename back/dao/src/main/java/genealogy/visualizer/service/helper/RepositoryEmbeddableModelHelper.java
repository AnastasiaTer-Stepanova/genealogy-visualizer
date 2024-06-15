package genealogy.visualizer.service.helper;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Используется только для Embeddable моделей
 *
 * @param <E> Embeddable entity
 */
public class RepositoryEmbeddableModelHelper<E> {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<E> updateEmbeddable(Long parentId, List<E> newData, Consumer<E> setFunction,
                                    Consumer<Long> deleteFunction, BiConsumer<Long, E> insertFunction) {
        if (newData == null || newData.isEmpty()) {
            if (parentId != null) {
                deleteFunction.accept(parentId);
            }
            return Collections.emptyList();
        }
        newData.forEach(setFunction);
        if (parentId != null) {
            deleteFunction.accept(parentId);
            newData.forEach(d -> insertFunction.accept(parentId, d));
        }
        return newData;
    }
}
