package org.js.ai;

import org.js.Muscle;

import java.util.List;

public class WorkoutStructure {
    private List<Muscle> primaryMuscleRequired;
    private String equipmentAvailable;
    private String userFitnessLevel;
    private String userObjective;
    private String notes;

    public WorkoutStructure() {
    }

    public String getUserObjective() {
        return userObjective;
    }

    public void setUserObjective(String userObjective) {
        this.userObjective = userObjective;
    }

    public String getUserFitnessLevel() {
        return userFitnessLevel;
    }

    public void setUserFitnessLevel(String userFitnessLevel) {
        this.userFitnessLevel = userFitnessLevel;
    }

    public String getEquipmentAvailable() {
        return equipmentAvailable;
    }

    public void setEquipmentAvailable(String equipmentAvailable) {
        this.equipmentAvailable = equipmentAvailable;
    }

    public List<Muscle> getPrimaryMuscleRequired() {
        return primaryMuscleRequired;
    }

    public void setPrimaryMuscleRequired(List<Muscle> primaryMuscleRequired) {
        this.primaryMuscleRequired = primaryMuscleRequired;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "WorkoutStructure{" +
                "primaryMuscleRequired=" + primaryMuscleRequired +
                ", equipmentAvailable='" + equipmentAvailable + '\'' +
                ", userFitnessLevel='" + userFitnessLevel + '\'' +
                ", userObjective='" + userObjective + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
