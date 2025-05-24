package org.js;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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
        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getExercises(String name, List<Muscle> pMuscle, List<Muscle> sMuscle, String level, String force) {
        Specification<Exercise> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(ExerciseSpecification.byName(name));
        }
        if (level != null) {
            spec = spec.and(ExerciseSpecification.byLevel(level));
        }
        if (force != null) {
            spec = spec.and(ExerciseSpecification.byForce(force));
        }
        List<Exercise> filteredSet = this.exerciseRepository.findAll(spec);
        if (pMuscle != null) {
            for (Muscle m : pMuscle) {
                filteredSet = filteredSet.stream()
                        .filter(e -> e.getPrimaryMuscles().contains(m))
                        .collect(Collectors.toList());
            }}
        if (sMuscle != null) {
            for (Muscle m : sMuscle) {
                filteredSet = filteredSet.stream()
                        .filter(e -> e.getSecondaryMuscles().contains(m))
                        .collect(Collectors.toList());
            }}
        return filteredSet;
    }
}
