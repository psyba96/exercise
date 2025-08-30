package org.js.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@DynamicUpdate
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String name;
    private String force;
    private String level;
    private String mechanic;
    private String equipment;
    private String category;
    private String gifHash;
    @Enumerated(EnumType.STRING)
    private List<Muscle> primaryMuscles;
    @Enumerated(EnumType.STRING)
    private List<Muscle> secondaryMuscles;
    @ElementCollection
    @CollectionTable(name = "exercise_instructions", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "instructions",length = 1000)
    private List<String> instructions;


    @Version
    private int version;

    public Exercise() {
    }

    public String getGifHash() {
        return gifHash;
    }

    public void setGifHash(String gifHash) {
        this.gifHash = gifHash;
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

    public String toString() {
        return "Exercise [name=" + name + ", equipment=" + equipment + ", primaryMuscles=" + primaryMuscles + ",]";
    }

    public void copyFrom(Exercise other) {
        this.setPrimaryMuscles(other.getPrimaryMuscles());
        this.setMechanic(other.getMechanic());
        this.setSecondaryMuscles(other.getSecondaryMuscles());
        this.setLevel(other.getLevel());
        this.setForce(other.getForce());
        this.setInstructions(other.getInstructions());
        this.setName(other.getName());
        this.setCategory(other.getCategory());
        this.setEquipment(other.getEquipment());
        this.setGifHash(other.getGifHash());
    }

}
