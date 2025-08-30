package org.js.controller;

import jakarta.validation.Valid;
import org.js.model.Exercise;
import org.js.model.Muscle;
import org.js.dto.NewExerciseDTO;
import org.js.service.ExerciseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/psyba/api/exercise") // Base URL for this controller
@Validated
public class ExerciseCrudController {

    private final ExerciseService exerciseService;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseCrudController.class);

    public ExerciseCrudController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String force,
            @RequestParam(required = false) String mechanic,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String equipment,
            @RequestParam(required = false) List<Muscle> pMuscle,
            @RequestParam(required = false) List<Muscle> sMuscle
    ) {
        logger.info("GET /psyba/api/exercise - Fetching exercises with params: name={}, primaryMuscles={}, secondaryMuscles={}, level={}, force={}, category={}, equipment={}, mechanic={}",
                name, pMuscle, sMuscle, level, force, category, equipment, mechanic);
        List<Exercise> exercises = exerciseService.getExercises(
                name,
                pMuscle,
                sMuscle,
                level,
                force,
                category,
                equipment,
                mechanic
        );
        exercises.sort(Comparator.comparing(Exercise::getName));
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        logger.info("GET /psyba/api/exercise/{} - Fetching exercise by ID",id);
        return exerciseService.getExerciseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid NewExerciseDTO exercise) {
        logger.info("POST /psyba/api/exercise - Creating new exercise with data: {}", exercise);
        Exercise savedExercise = exerciseService.createExercise(exercise);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedExercise.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedExercise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> upsert(
            @PathVariable int id,
            @RequestBody @Valid NewExerciseDTO newDetails
    ) {
        logger.info("PUT /psyba/api/exercise/{} - Upserting exercise with data: {}", id, newDetails);
        Optional<Exercise> upsertResponse = exerciseService.updateIfPresent(id, newDetails);
        if (upsertResponse.isEmpty()) {
            return create(newDetails);
        }
        return ResponseEntity.ok(upsertResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody Map<String, Object> updates
    ) {
        logger.info("PATCH /psyba/api/exercise/{} - Updating exercise with fields: {}", id, updates);
        Exercise updated = exerciseService.updateExercise(id, updates);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        logger.info("DELETE /psyba/api/exercise/{} - Attempting to delete exercise", id);
        if (exerciseService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
