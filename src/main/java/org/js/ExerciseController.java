package org.js;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/psyba/api/exercise") // Base URL for this controller
@Validated
public class ExerciseController {

    @Autowired
    private final ExerciseRepository exerciseRepository;
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseRepository exerciseRepository, ExerciseService exerciseService) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseService = exerciseService;
    }

    // GET: Retrieve all records
    @GetMapping("")
    List<Exercise> findAll(@RequestParam(required = false) String name,@RequestParam(required = false) String level,@RequestParam(required = false) String force,
                           @RequestParam(required = false) List<Muscle> pMuscle,@RequestParam(required = false) List<Muscle> sMuscle) {
        return this.exerciseService.getExercises(name,pMuscle,sMuscle,level,force);
    }

    // GET: Retrieve a specific record by ID
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getById(@PathVariable int id) {
        return exerciseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create a new record
    @PostMapping
    public ResponseEntity<Exercise> create(@RequestBody @Valid NewExerciseDTO exercise) {
        Exercise savedExercise = exerciseService.createExercise(exercise);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedExercise.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedExercise);
    }

    // PUT: Update an existing record
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> upsert(
            @PathVariable int id, @RequestBody Exercise newDetails) {
        return ResponseEntity.ok(exerciseRepository.save(newDetails));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Exercise> update(
            @PathVariable int id, @RequestBody Exercise newDetails) {
        return ResponseEntity.ok(this.exerciseService.updateExercise(id, newDetails));
    }

    // DELETE: Delete a record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
