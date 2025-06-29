package org.js.ai;

import org.js.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemService {
    private final ExerciseRepository exerciseRepository;

    public SystemService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
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
            filteredSet = filteredSet.stream()
                    .filter(e -> e.getPrimaryMuscles().stream().anyMatch(pMuscle::contains))
                    .collect(Collectors.toList());
        }
        if (sMuscle != null) {
            filteredSet = filteredSet.stream()
                    .filter(e -> e.getSecondaryMuscles().stream().anyMatch(sMuscle::contains))
                    .collect(Collectors.toList());
        }
        return filteredSet;
    }

    public Exercise getExerciseFromName(ExerciseNew e1){
        System.out.println("tessss");
        List<Exercise> e = getExercises(e1.getName(),null,null,null,null);
        try {
            return e.get(0);
        }
        catch (Exception ex){
            return new PlanExercise();
        }
    }

    public ExercisesFinal convertToFinal(Exercises ee1) {
        List<Exercise> warmUp = new ArrayList<>();
        for (ExerciseNew e1 : ee1.getWarmUpExercises()) {
            Exercise finalized  = getExerciseFromName(e1);
            warmUp.add(finalized);
        }
        List<Exercise> mainWorkout = new ArrayList<>();
        for (ExerciseNew e1 : ee1.getMainExercises()) {
            Exercise finalized  =  getExerciseFromName(e1);
            mainWorkout.add(finalized);
        }
        List<Exercise> coolDown = new ArrayList<>();
        for (ExerciseNew e1 : ee1.getCoolDownExercises()) {
            Exercise finalized  =  getExerciseFromName(e1);
            coolDown.add(finalized);
        }
        ExercisesFinal finalPlan = new ExercisesFinal();
        finalPlan.setWarmUpExercises(warmUp);
        finalPlan.setMainExercises(mainWorkout);
        finalPlan.setCoolDownExercises(coolDown);
        return finalPlan;
    }
}
