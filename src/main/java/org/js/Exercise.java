package org.js;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@DynamicUpdate
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String force;
    private String level;
    private String mechanic;
    private String equipment;
    @Enumerated(EnumType.STRING)
    private List<Muscle> secondaryMuscles;


    @ElementCollection
    @CollectionTable(name = "exercise_instructions", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "instructions")
    private List<String> instructions;
    private String category;
    @Enumerated(EnumType.STRING)
    private List<Muscle> primaryMuscles;

    @Version
    private int version;

    public Exercise() {
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMechanic() {
        return mechanic;
    }

    public void setMechanic(String mechanic) {
        this.mechanic = mechanic;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<Muscle> getPrimaryMuscles() {
        return primaryMuscles;
    }

    public void setPrimaryMuscles(List<Muscle> primaryMuscles) {
        this.primaryMuscles = primaryMuscles;
    }

    public List<Muscle> getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public void setSecondaryMuscles(List<Muscle> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    // Getters and setters (required for JPA to access fields)
    public Integer getId() {
        return  id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
