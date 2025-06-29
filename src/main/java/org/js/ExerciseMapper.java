package org.js;

import org.js.ai.PlanExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseMapper INSTANCE = Mappers.getMapper(ExerciseMapper.class);
    NewExerciseDTO toDTO(Exercise user);
    Exercise toEntity(NewExerciseDTO dto);

    @Mapping(target = "id", ignore = true) // Don't copy ID for new entities
    @Mapping(target = "sets", source = "sets")
    @Mapping(target = "reps", source = "reps")
    PlanExercise exerciseToPlan(Exercise parent, String sets, String reps);
}
