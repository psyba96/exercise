package org.js;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

public interface ExerciseRepository extends ListCrudRepository<Exercise, Integer>, JpaSpecificationExecutor<Exercise> {}
