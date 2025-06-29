package org.js.ai;

import org.js.Exercise;
import java.util.List;

public class ExercisesFinal {

    private List<Exercise> mainExercises;
    private List<Exercise> warmUpExercises;
    private List<Exercise> coolDownExercises;

    public ExercisesFinal() {
    }

    public List<Exercise> getMainExercises() {
        return mainExercises;
    }

    public void setMainExercises(List<Exercise> mainExercises) {
        this.mainExercises = mainExercises;
    }

    public List<Exercise> getWarmUpExercises() {
        return warmUpExercises;
    }

    public void setWarmUpExercises(List<Exercise> warmUpExercises) {
        this.warmUpExercises = warmUpExercises;
    }

    public List<Exercise> getCoolDownExercises() {
        return coolDownExercises;
    }

    public void setCoolDownExercises(List<Exercise> coolDownExercises) {
        this.coolDownExercises = coolDownExercises;
    }

    @Override
    public String toString() {
        return "Exercises{" +
                "mainExercises=" + mainExercises +
                ", warmUpExercises=" + warmUpExercises +
                ", coolDownExercises=" + coolDownExercises +
                '}';
    }
}
