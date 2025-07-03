package org.js;

import jakarta.validation.Valid;
import org.js.ai.ExerciseAiService;
import org.js.ai.WorkoutStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/psyba/api/exercise") // Base URL for this controller
@Validated
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseAiService aiService;

    public ExerciseController(ExerciseService exerciseService, ExerciseAiService aiService) {
        this.exerciseService = exerciseService;
        this.aiService = aiService;
    }

    @PostMapping("/prompt")
    public ResponseEntity<Map<String,Object>> getResponse(@RequestBody Map<String, String> request) {
        //return exerciseService.getAllExercisesWithAI(request.get("muscle"),request.get("level"),request.get("secondary"),request.get("prompt"));
        WorkoutStructure ws = aiService.identifyRequirement(request.get("prompt"),request.get("level"));
        return ResponseEntity.ok(aiService.getWorkout(ws));
    }

    // GET: Retrieve all records
    @GetMapping("")
    List<Exercise> findAll(@RequestParam(required = false) String name,@RequestParam(required = false) String level,@RequestParam(required = false) String force,
                           @RequestParam(required = false) String mechanic,@RequestParam(required = false) String category,@RequestParam(required = false) String equipment,
                           @RequestParam(required = false) List<Muscle> pMuscle,@RequestParam(required = false) List<Muscle> sMuscle) {
        return this.exerciseService.getExercises(name,pMuscle,sMuscle,level,force,category,equipment,mechanic);
    }

    // GET: Retrieve a specific record by ID
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getById(@PathVariable int id) {
        return exerciseService.getExerciseById(id)
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
        Optional<Exercise> upsertExerciseResponse =exerciseService.upsertExercise(id,newDetails);
        if (upsertExerciseResponse.isEmpty()){
            return create(ExerciseMapper.INSTANCE.toDTO(newDetails));
        }
        return upsertExerciseResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Exercise> update(
            @PathVariable int id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(this.exerciseService.updateExercise(id, updates));
    }

    // DELETE: Delete a record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (exerciseService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/singleExercise")
    public ResponseEntity<Map<String,Object>> getSingleExerciseForPlan(@RequestBody Map<String,Object> inputFromUi){
        return ResponseEntity.ok(aiService.getNewExerciseForPlan(inputFromUi));
    }
}
