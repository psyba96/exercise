package org.js;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.client.ChatClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExerciseService {


    private final ExerciseRepository exerciseRepository;
    private final ChatClient chatClient;

    public ExerciseService(ExerciseRepository exerciseRepository, ChatClient.Builder builder) {
        this.exerciseRepository = exerciseRepository;
        this.chatClient = builder.build();
    }

    @Transactional
    public Exercise updateExercise(int id, Map<String, Object> updates) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with ID " + id + " not found"));
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    existingExercise.setName((String) value);
                    break;
                case "force":
                    existingExercise.setForce((String) value);
                    break;
                case "level":
                    existingExercise.setLevel((String) value);
                    break;
                case "category":
                    existingExercise.setCategory((String) value);
                    break;
                case "equipment":
                    existingExercise.setEquipment((String) value);
                    break;
                case "mechanic":
                    existingExercise.setMechanic((String) value);
                    break;
                case "primaryMuscles":
                    existingExercise.setPrimaryMuscles(Muscle.convertToMuscleList(value));
                    break;
                case "secondaryMuscles":
                    existingExercise.setSecondaryMuscles(Muscle.convertToMuscleList(value));
                    break;
                case "instructions":
                    existingExercise.setInstructions((List<String>) value);
                    break;
                default:
                    throw new IllegalArgumentException("Field " + key + " not allowed to be updated.");
            }
        });
        return exerciseRepository.save(existingExercise);
    }

    public Exercise createExercise(NewExerciseDTO newExerciseDTO) {
        if (exerciseRepository.existsByName(newExerciseDTO.getName())) {
            throw new IllegalArgumentException("Exercise with the same name already exists.");

        }
        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        return exerciseRepository.save(exercise);
    }

    public ResponseEntity getAllExercisesWithAI(String muscle,String level,String secondary,String prompt) {



        try {
            if ((muscle == null || muscle.trim().isEmpty())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Prompt cannot be empty"));
            }
            List<Exercise> exercises = getExercises(null,List.of(Muscle.fromValue(muscle)),null,null,null,null,null,null);
            String promptForMuscle = "Prepare a workout routine for muscle group " + muscle + " refer to available exercises below(retain exact exercise name in the prepared plan):" + exercises.toString();
            String promptForLevel = level != null ? (" catered for experience level: " + level):"";
            String promptForSecondary = secondary != null ? (" should engage these secondary muscles: " + secondary):"";
            String augmentedPrompt = promptForMuscle + promptForLevel + promptForSecondary;
            String schema = """
            {[{"name": "string", "duration": "string", "sets": "number", "reps": "string"}]}
            """;
            String response1 = chatClient.prompt(augmentedPrompt).call().content();
            String response2 = chatClient.prompt("format as a list for app code consumption,do not include any non machine readable elements like \"`\",\"json\",(return a json structure, with keys:name,sets,reps,duration) for text:"+response1+"as per json schema:"+schema).call().content();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response2);
            List<String> returnedExercises = rootNode.findValuesAsText("name");
            List<Exercise> fetchedexercises = exercises.stream().filter(exercise -> returnedExercises.contains(exercise.getName())).toList();
            // Add a new field to rootNode JSON based on matches from fetchedexercises
            for (JsonNode exerciseNode : rootNode) {
                String exerciseName = exerciseNode.get("name").asText();
                for (Exercise exercise : fetchedexercises) {
                    if (exercise.getName().equals(exerciseName)) {
                        ((ObjectNode) exerciseNode).put("equipment", exercise.getEquipment());
                        ((ObjectNode) exerciseNode).put("instructions", mapper.valueToTree(exercise.getInstructions()));
                    }
                }
            }
            return ResponseEntity.ok(rootNode);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process request: " + e.getMessage()));
        }
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

        Specification<Exercise> spec =  Specification.where(null);
        if (name != null) {
            spec = spec.and(ExerciseSpecification.byName(name));
        }
        if (level != null) {
            spec = spec.and(ExerciseSpecification.byLevel(level));
        }
        if (force != null) {
            spec = spec.and(ExerciseSpecification.byForce(force));
        }
        if (category != null) {
            spec = spec.and(ExerciseSpecification.byCategory(category));
        }
        if (mechanic != null) {
            spec = spec.and(ExerciseSpecification.byMechanic(mechanic));
        }
        if (equipment != null) {
            spec = spec.and(ExerciseSpecification.byEquipment(equipment));
        }
        List<Exercise> filtered = exerciseRepository.findAll(spec);

        // Filter primary muscles if provided
        if (pMuscle != null && !pMuscle.isEmpty()) {
            System.out.println(pMuscle);
            filtered = filtered.stream()
                    .filter(e -> !Collections.disjoint(e.getPrimaryMuscles(), pMuscle))
                    .collect(Collectors.toList());
        }

        // Filter secondary muscles if provided
        if (sMuscle != null && !sMuscle.isEmpty()) {

            filtered = filtered.stream()
                    .filter(e -> !Collections.disjoint(e.getSecondaryMuscles(), sMuscle))
                    .collect(Collectors.toList());
        }

        return filtered;
    }


    public Optional<Exercise> getExerciseById(int id) {
        return exerciseRepository.findById(id);
    }

    public Optional<Exercise> upsertExercise(int id,Exercise newExercise){
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        if (optionalExercise.isEmpty()) {
            return (optionalExercise);
        }
        Exercise existingExercise = optionalExercise.get();
        existingExercise.copyFrom(newExercise);
        Exercise updatedExercise = exerciseRepository.save(existingExercise);
        return Optional.of(updatedExercise);
    }

    public boolean delete(int id){
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
