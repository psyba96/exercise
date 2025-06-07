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
import java.util.List;
import java.util.Map;
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
    public Exercise updateExercise(int id, Exercise updatedExercise) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with ID " + id + " not found"));

        if (updatedExercise.getName() != null) {
            existingExercise.setName(updatedExercise.getName());
        }
        if (updatedExercise.getCategory() != null) {
            existingExercise.setCategory(updatedExercise.getCategory());
        }
        if (updatedExercise.getEquipment() != null) {
            existingExercise.setEquipment(updatedExercise.getEquipment());
        }
        if (updatedExercise.getVersion() != 0) {
            existingExercise.setVersion(updatedExercise.getVersion());
        }
        if (updatedExercise.getForce() != null) {
            existingExercise.setForce(updatedExercise.getForce());
        }
        if (updatedExercise.getInstructions() != null) {
            existingExercise.setInstructions(updatedExercise.getInstructions());
        }
        if (updatedExercise.getLevel() != null) {
            existingExercise.setLevel(updatedExercise.getLevel());
        }
        if (updatedExercise.getMechanic() != null) {
            existingExercise.setMechanic(updatedExercise.getMechanic());
        }
        if (updatedExercise.getPrimaryMuscles() != null) {
            existingExercise.setPrimaryMuscles(updatedExercise.getPrimaryMuscles());
        }
        if (updatedExercise.getSecondaryMuscles() != null) {
            existingExercise.setSecondaryMuscles(updatedExercise.getSecondaryMuscles());
        }

        return exerciseRepository.save(existingExercise);
    }

    public Exercise createExercise(NewExerciseDTO newExerciseDTO) {
        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        return exerciseRepository.save(exercise);
    }

    public ResponseEntity getAllExercisesWithAI(String muscle,String level,String secondary) {
        try {
            if ((muscle == null || muscle.trim().isEmpty())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Prompt cannot be empty"));
            }
            List<Exercise> exercises = getExercises(null,List.of(Muscle.fromValue(muscle)),null,null,null);
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

    public List<Exercise> getExercises(String name, List<Muscle> pMuscle, List<Muscle> sMuscle, String level, String force) {
        Specification<Exercise> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(ExerciseSpecification.byName(name));
        }
        if (level != null) {
            spec = spec.and(ExerciseSpecification.byLevel(level));
        }
        if (force != null) {
            spec = spec.and(ExerciseSpecification.byForce(force));
        }
        List<Exercise> filteredSet = this.exerciseRepository.findAll(spec);
        if (pMuscle != null) {
            for (Muscle m : pMuscle) {
                filteredSet = filteredSet.stream()
                        .filter(e -> e.getPrimaryMuscles().contains(m))
                        .collect(Collectors.toList());
            }}
        if (sMuscle != null) {
            for (Muscle m : sMuscle) {
                filteredSet = filteredSet.stream()
                        .filter(e -> e.getSecondaryMuscles().contains(m))
                        .collect(Collectors.toList());
            }}
        return filteredSet;
    }
}
