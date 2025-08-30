package org.js.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.js.dto.ExerciseMapper;
import org.js.dto.NewExerciseDTO;
import org.js.exception.ResourceNotFoundException;
import org.js.model.Exercise;
import org.js.model.Muscle;
import org.js.repository.ExerciseRepository;
import org.js.repository.ExerciseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    private final ExerciseRepository exerciseRepository;
    Executor executor = Executors.newFixedThreadPool(5);

    @Autowired
    private ObjectMapper objectMapper;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }




    @Transactional
    public Exercise updateExercise(int id, Map<String, Object> updates) {
        logger.info("START updateExercise - id: {}", id);
        logger.debug("updateExercise - update map: {}", updates);

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        updates.forEach((k, v) -> applyUpdate(exercise, k, v));

        Exercise saved = exerciseRepository.save(exercise);

        logger.info("END updateExercise - id: {}", id);
        return saved;
    }

    private void applyUpdate(Exercise e, String field, Object value) {
        logger.debug("applyUpdate - field: {}, value: {}", field, value);
        switch (field) {
            case "name" -> e.setName((String) value);
            case "instructions" -> e.setInstructions(objectMapper.convertValue(value, new TypeReference<List<String>>() {}));
            case "force" -> e.setForce((String) value);
            case "level" -> e.setLevel((String) value);
            case "category" -> e.setCategory((String) value);
            case "equipment" -> e.setEquipment((String) value);
            case "mechanic" -> e.setMechanic((String) value);
            case "gifHash" -> e.setGifHash((String) value);
            case "primaryMuscles" -> e.setPrimaryMuscles(Muscle.convertToMuscleList(value));
            case "secondaryMuscles" -> e.setSecondaryMuscles(Muscle.convertToMuscleList(value));
            default -> throw new IllegalArgumentException("Invalid field: " + field);
        }
    }

    @Transactional
    public Exercise createExercise(NewExerciseDTO newExerciseDTO) {
        logger.info("START createExercise - name: {}", newExerciseDTO.getName());

        if (exerciseRepository.existsByName(newExerciseDTO.getName())) {
            logger.warn("createExercise - Exercise already exists: {}", newExerciseDTO.getName());
            throw new IllegalArgumentException("Exercise with the same name already exists.");
        }

        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        Exercise saved = exerciseRepository.save(exercise);

        logger.info("END createExercise - id: {}", saved.getId());
        return saved;
    }

    public List<Exercise> getExercises(
            String name,
            List<Muscle> pMuscle,
            List<Muscle> sMuscle,
            String level,
            String force,
            String category,
            String equipment,
            String mechanic) {

        logger.info("START getExercises - name: {}", name);

        Specification<Exercise> spec = Specification.where(null);

        if (name != null) spec = spec.and(ExerciseSpecification.byName(name));
        if (level != null) spec = spec.and(ExerciseSpecification.byLevel(level));
        if (force != null) spec = spec.and(ExerciseSpecification.byForce(force));
        if (category != null) spec = spec.and(ExerciseSpecification.byCategory(category));
        if (mechanic != null) spec = spec.and(ExerciseSpecification.byMechanic(mechanic));
        if (equipment != null) spec = spec.and(ExerciseSpecification.byEquipment(equipment));

        List<Exercise> filtered = exerciseRepository.findAll(spec);
        logger.debug("getExercises - after spec filter: {} exercises", filtered.size());

        if (pMuscle != null && !pMuscle.isEmpty()) {
            logger.debug("Filtering by primary muscles: {}", pMuscle);
            filtered = filtered.stream()
                    .filter(e -> !Collections.disjoint(e.getPrimaryMuscles(), pMuscle))
                    .collect(Collectors.toList());
        }

        if (sMuscle != null && !sMuscle.isEmpty()) {
            logger.debug("Filtering by secondary muscles: {}", sMuscle);
            filtered = filtered.stream()
                    .filter(e -> !Collections.disjoint(e.getSecondaryMuscles(), sMuscle))
                    .collect(Collectors.toList());
        }

        logger.info("END getExercises - result size: {}", filtered.size());
        return filtered;
    }

    public Optional<Exercise> getExerciseById(int id) {
        logger.info("getExerciseById - id: {}", id);
        return exerciseRepository.findById(id);
    }



    @Transactional
    public Optional<Exercise> updateIfPresent(int id, Exercise newExercise) {
        logger.info("START updateIfPresent - id: {}", id);
        Optional<Exercise> optional = exerciseRepository.findById(id);

        if (optional.isEmpty()) {
            logger.warn("updateIfPresent - Exercise not found for id: {}", id);
            return optional;
        }

        Exercise existing = optional.get();
        existing.copyFrom(newExercise);
        Exercise saved = exerciseRepository.save(existing);

        logger.info("END updateIfPresent - id: {}", id);
        return Optional.of(saved);
    }

    @Transactional
    public boolean delete(int id) {
        logger.info("START delete - id: {}", id);
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            logger.info("END delete - Exercise deleted id: {}", id);
            return true;
        } else {
            logger.warn("delete - Exercise not found id: {}", id);
            return false;
        }
    }
}
