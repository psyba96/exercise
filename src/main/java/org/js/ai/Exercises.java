package org.js.ai;

import java.util.List;

public class Exercises {

    private List<ExerciseNew> mainExercises;
    private List<ExerciseNew> warmUpExercises;
    private List<ExerciseNew> coolDownExercises;

    public Exercises() {
    }

    public Exercises(List<ExerciseNew> warmUp, List<ExerciseNew> main, List<ExerciseNew> stretching) {
        this.warmUpExercises = warmUp;
        this.mainExercises = main;
        this.coolDownExercises = stretching;
    }

    public List<ExerciseNew> getMainExercises() {
        return mainExercises;
    }

    public void setMainExercises(List<ExerciseNew> mainExercises) {
        this.mainExercises = mainExercises;
    }

    public List<ExerciseNew> getWarmUpExercises() {
        return warmUpExercises;
    }

    public void setWarmUpExercises(List<ExerciseNew> warmUpExercises) {
        this.warmUpExercises = warmUpExercises;
    }

    public List<ExerciseNew> getCoolDownExercises() {
        return coolDownExercises;
    }

    public void setCoolDownExercises(List<ExerciseNew> coolDownExercises) {
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