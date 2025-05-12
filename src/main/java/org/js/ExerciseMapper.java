package org.js;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseMapper INSTANCE = Mappers.getMapper(ExerciseMapper.class);

    NewExerciseDTO toDTO(Exercise user);
    Exercise toEntity(NewExerciseDTO dto);
}
