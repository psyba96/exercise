package org.js;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Transactional
    public Exercise updateExercise(int id, Exercise updatedExercise) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with ID " + id + " not found"));

        if (updatedExercise.getName() != null) {
            existingExercise.setName(updatedExercise.getName());
        }
        if (updatedExercise.getCategory() != null) {
            existingExercise.setCategory(updatedExercise.getCategory());
        }
        if (updatedExercise.getEquipment() != null) {
            existingExercise.setEquipment(updatedExercise.getEquipment());
        }
        if (updatedExercise.getVersion() != 0) {
            existingExercise.setVersion(updatedExercise.getVersion());
        }
        if (updatedExercise.getForce() != null) {
            existingExercise.setForce(updatedExercise.getForce());
        }
        if (updatedExercise.getInstructions() != null) {
            existingExercise.setInstructions(updatedExercise.getInstructions());
        }
        if (updatedExercise.getLevel() != null) {
            existingExercise.setLevel(updatedExercise.getLevel());
        }
        if (updatedExercise.getMechanic() != null) {
            existingExercise.setMechanic(updatedExercise.getMechanic());
        }
        if (updatedExercise.getPrimaryMuscles() != null) {
            existingExercise.setPrimaryMuscles(updatedExercise.getPrimaryMuscles());
        }
        if (updatedExercise.getSecondaryMuscles() != null) {
            existingExercise.setSecondaryMuscles(updatedExercise.getSecondaryMuscles());
        }

        return exerciseRepository.save(existingExercise);
    }

    public Exercise createExercise(NewExerciseDTO newExerciseDTO) {
        // Map NewExerciseDTO fields to Exercise entity
      /*  Exercise exercise = new Exercise();
        exercise.setName(newExerciseDTO.getName());
        exercise.setCategory(newExerciseDTO.getCategory());
        exercise.setEquipment(newExerciseDTO.getEquipment());
        exercise.setLevel(newExerciseDTO.getLevel());
        exercise.setMechanic(newExerciseDTO.getMechanic());
        exercise.setForce(newExerciseDTO.getForce());
        exercise.setVersion(newExerciseDTO.getVersion());
        exercise.setInstructions(newExerciseDTO.getInstructions());
        exercise.setPrimaryMuscles(newExerciseDTO.getPrimaryMuscles());
        exercise.setSecondaryMuscles(newExerciseDTO.getSecondaryMuscles());*/
        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        return exerciseRepository.save(exercise);
    }


}
