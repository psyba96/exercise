package org.js.repository;
import org.js.model.Exercise;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

public interface ExerciseRepository extends ListCrudRepository<Exercise, Integer>, JpaSpecificationExecutor<Exercise> {
    boolean existsByName(String name);
}
