package org.js;

import org.springframework.data.jpa.domain.Specification;

public class ExerciseSpecification {
    public static Specification<Exercise> byName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    public static Specification<Exercise> byLevel(String level) {
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }
    public static Specification<Exercise> byForce(String force) {
        return (root, query, cb) -> cb.equal(root.get("force"), force);
    }
    public static Specification<Exercise> byCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }
    public static Specification<Exercise> byMechanic(String mechanic) {
        return (root, query, cb) -> cb.equal(root.get("mechanic"), mechanic);
    }
    public static Specification<Exercise> byEquipment(String equipment) {
        return (root, query, cb) -> cb.equal(root.get("equipment"), equipment);
    }

}

