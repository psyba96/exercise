package org.js.dto;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.js.model.Exercise;
import org.js.model.Muscle;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:26:42+1000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class ExerciseMapperImpl implements ExerciseMapper {

    @Override
    public NewExerciseDTO toDTO(Exercise user) {
        if ( user == null ) {
            return null;
        }

        NewExerciseDTO newExerciseDTO = new NewExerciseDTO();

        newExerciseDTO.setGifHash( user.getGifHash() );
        newExerciseDTO.setForce( user.getForce() );
        newExerciseDTO.setLevel( user.getLevel() );
        newExerciseDTO.setMechanic( user.getMechanic() );
        newExerciseDTO.setEquipment( user.getEquipment() );
        List<Muscle> list = user.getPrimaryMuscles();
        if ( list != null ) {
            newExerciseDTO.setPrimaryMuscles( new ArrayList<Muscle>( list ) );
        }
        List<Muscle> list1 = user.getSecondaryMuscles();
        if ( list1 != null ) {
            newExerciseDTO.setSecondaryMuscles( new ArrayList<Muscle>( list1 ) );
        }
        List<String> list2 = user.getInstructions();
        if ( list2 != null ) {
            newExerciseDTO.setInstructions( new ArrayList<String>( list2 ) );
        }
        newExerciseDTO.setCategory( user.getCategory() );
        newExerciseDTO.setVersion( user.getVersion() );
        if ( user.getId() != null ) {
            newExerciseDTO.setId( user.getId() );
        }
        newExerciseDTO.setName( user.getName() );

        return newExerciseDTO;
    }

    @Override
    public Exercise toEntity(NewExerciseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Exercise exercise = new Exercise();

        exercise.setGifHash( dto.getGifHash() );
        exercise.setForce( dto.getForce() );
        exercise.setLevel( dto.getLevel() );
        exercise.setMechanic( dto.getMechanic() );
        exercise.setEquipment( dto.getEquipment() );
        List<Muscle> list = dto.getPrimaryMuscles();
        if ( list != null ) {
            exercise.setPrimaryMuscles( new ArrayList<Muscle>( list ) );
        }
        List<Muscle> list1 = dto.getSecondaryMuscles();
        if ( list1 != null ) {
            exercise.setSecondaryMuscles( new ArrayList<Muscle>( list1 ) );
        }
        List<String> list2 = dto.getInstructions();
        if ( list2 != null ) {
            exercise.setInstructions( new ArrayList<String>( list2 ) );
        }
        exercise.setCategory( dto.getCategory() );
        exercise.setVersion( dto.getVersion() );
        if ( dto.getId() != null ) {
            exercise.setId( dto.getId() );
        }
        exercise.setName( dto.getName() );

        return exercise;
    }
}
