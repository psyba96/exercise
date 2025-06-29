package org.js;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.js.ai.ExercisesFinal;
import org.js.ai.WorkoutAIService;
import org.js.ai.WorkoutStructure;
import org.springframework.ai.chat.client.ChatClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class ExerciseService {

    private static final Logger logger = Logger.getLogger(ExerciseService.class.getName());
    private final ExerciseRepository exerciseRepository;
    private final ChatClient chatClient;
    private final WorkoutAIService workoutAIService;

    final String finalJsonSchema= """
                    {
                     "warm up": [{...}, {...}],
                     "workout": [{...}, {...}],
                     "cool down": [{...}, {...}]
                  }
                    """;
    final String planValidator = """
                    Validate following workout plan against following rules: 
                    primary target muscles: %s
                    workout plan: %s
                    Rules:
                    1.Primary muscles and workout objective is covered by at least one exercise in the workout plan.
                    2.Exercises are structured in the recommended order.
                    3.No exercise is duplicated across sections.
                    4.Structure is as per json schema: %s 
                    Response is always expected to be with structure {"bool": true/false,"reason":<reason for evaluation response>} so that machine can validate the response.No markdown, comments or explanations.Do not include any non machine readable elements like \"`\",\"json\"
                    """;
    final String reworkPrompt = """
        Please revise the workout plan with strict adherence to these requirements:
        %s
        Previous plan that needs revision:
        %s
        Previous plan invalidation reasons:
        %s
        """;
    final String systemPromptForPlan = """
                        You are a professional fitness trainer specialized in creating time-efficient workouts.
                        Your task is to create an optimized workout routine with these strict requirements:
                        Requirements:
                        1. Duration: Maximum 60 minutes
                        2. Focus: Maximum muscle damage per repetition
                        3. Format: Clear, structured workout plan
                        4. Maximum 6 exercises per workout excluding warm up and stretches
                        5. Limit barbell exercises in beginner workouts and prefer more bodyweight movements 
                        6. Use ONLY exercises from the provided list,include exact exercise name as the list provided, in the routine
                        7. Specify sets, reps, and rest periods
                        8. Focus on compound exercises first
                        9. List exercises in execution order
                        """;
    final String contextForPlan = """
                        Target muscle groups: %s
                        User gym experience: %s
                        Available exercises:
                        %s
                        Previous invalidation reasons based on the required rules:%s
                        """;
    final String systemPromptMuscleIdentifier = """
                You are a muscle group identifier and classifier. 
                Your task is to match user input to valid muscle enum values.
                Valid muscles: %s
                Rules:
                - Return ONLY comma-separated muscle names
                - Allow for max 2 min rest time between sets 
                - Use ONLY valid enum values
                - NO explanations or additional text
                - Convert common names to proper enum values
                - Skip invalid muscles
                - Use uppercase with underscores
                Example input: "legs"
                Example output: HAMSTRINGS,QUADRICEPS,CALVES,GLUTES
                """;
    final String userPromptMuscleIdentifier = """
                Convert this to valid muscle names: %s
                """;
    final String schema = """
            {[{"name": "string", "duration": "string", "sets": "string", "reps": "string"}]}
            """;
    final String promptTemplate = """
            You are a data formatting assistant. Convert to JSON array only.
            Format the following workout data strictly as JSON array matching the following schema:
            %s
            No markdown, comments or explanations.
            Do not include any non machine readable elements like \"`\",\"json\"
            Input:
            %s""";
    final String restructuringPrompt = """
                You are tasked with organizing workout exercises into three structured categories: 'warm up', 'workout', and 'cool down'. 
                Each category should contain properly assigned exercises. The input is provided as JSON with exercise details.
                Format Requirements:
                - The output should strictly follow the nesting structure:%s                  
                - Each key must contain an array of objects, with each object representing one exercise.
                - Do not change any attributes oj objects, ONLY group and divide exercises into categories.
                - Ensure no exercise is duplicated across sections.
                Input:
                %s
                Rules:
                - Assign short-duration, low-intensity exercises to 'warm up'.
                - Assign compound lifts or primary muscle-targeting exercises to 'workout'.
                - Assign stretching or light recovery exercises to 'cool down'.
                - Do not add explanatory text or modify keys. Only respond with a properly formatted JSON object.
                - Do not include any non machine readable elements like \"`\",\"json\":
                """;
    public ExerciseService(ExerciseRepository exerciseRepository, ChatClient.Builder builder, WorkoutAIService workoutAIService) {
        this.exerciseRepository = exerciseRepository;
        this.chatClient = builder.build();
        this.workoutAIService = workoutAIService;
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
        if (exerciseRepository.existsByName(newExerciseDTO.getName())) {
            throw new IllegalArgumentException("Exercise with the same name already exists.");
        }
        Exercise exercise = ExerciseMapper.INSTANCE.toEntity(newExerciseDTO);
        return exerciseRepository.save(exercise);
    }

    public String generateWorkoutPlan(String muscleInput,String userLevel,List<Exercise> exercises,List<String> previousInvalidationReasons) {
        String rawAiResponse = aiPlanGenrator(muscleInput, userLevel, exercises, previousInvalidationReasons);
        //String promptForSecondary = secondary != null ? (" should engage these secondary muscles: " + secondary):"";
        String jsonResponse = formatPlan(rawAiResponse);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            List<String> returnedExercises = rootNode.findValuesAsText("name");
            System.out.println("ai returned exercsies:"+returnedExercises);
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
            String restructuredJson = getRestructuredJson(rootNode);
            return restructuredJson;
        } catch (Exception e) {

            return null;
        }

    }

    private String getRestructuredJson(JsonNode rootNode) {
        String formattedJsonRestructuringPrompt = String.format(restructuringPrompt, finalJsonSchema, rootNode.toString());
        System.out.println("input for restructing:"+formattedJsonRestructuringPrompt);
        String restructuredJson = chatClient.prompt(formattedJsonRestructuringPrompt).call().content();
        System.out.println("output for restructing:"+restructuredJson);
        return restructuredJson;
    }

    private String formatPlan(String rawAiResponse) {
        String formattedPrompt = String.format(promptTemplate, schema, rawAiResponse);
        System.out.println("input for json formatter:"+formattedPrompt);
        String jsonResponse = chatClient.prompt(formattedPrompt).call().content();
        System.out.println("output for json formatter:"+jsonResponse);
        return jsonResponse;
    }

    private String aiPlanGenrator(String muscleInput, String userLevel, List<Exercise> exercises, List<String> previousInvalidationReasons) {
        String finalPromptForPlan = systemPromptForPlan + String.format(contextForPlan, muscleInput, userLevel, exercises.toString(), previousInvalidationReasons != null ? previousInvalidationReasons.toString() : "None");
        System.out.println("input for plan generator:"+finalPromptForPlan);
        String rawAiResponse = chatClient.prompt(finalPromptForPlan).call().content();
        System.out.println("output for plan generator:"+rawAiResponse);
        return rawAiResponse;
    }


    public ResponseEntity getAllExercisesWithAI(String muscleGroup, String muscle, String level, String secondary) {

        try {
            String userLevel = level != null ? level.trim().toUpperCase() : "BEGINNER";
            String muscleInput;
            List<Exercise> exercises;
            if ((muscle == null || muscle.trim().isEmpty()) && (muscleGroup == null || muscleGroup.trim().isEmpty())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Muscle or muscle group cannot be empty"));
            }

            if(muscleGroup != null && !muscleGroup.trim().isEmpty()) {
                String muscleGroupsFromAi = getMuscleGroupFromPrompt(muscleGroup,userLevel);
                List<Muscle> muscleGroups = Muscle.convertToMuscleList(muscleGroupsFromAi);
                exercises = getExercises(null,muscleGroups,null,null,null);
                muscleInput = muscleGroup;
            }
            else{
                exercises = getExercises(null,List.of(Muscle.fromValue(muscle)),null,null,null);
                muscleInput = muscle;
            }
            String restructuredJson = generateWorkoutPlan(muscleInput,userLevel,exercises,null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode validationResult = mapper.readTree(validateByLlm(restructuredJson, muscleInput));
            List<String> invalidationReasons = new ArrayList<>();
            while("false".equals(validationResult.get("bool").asText()) && !"true".equals(validationResult.get("bool").asText())) {
                //System.out.println("LLM failed to validate the restructured json, restructuring again");
                invalidationReasons.add(validationResult.get("reason").asText());
                /*String promptForRework = String.format(reworkPrompt, planValidator, restructuredJson,invalidationReasons.toString());
                String reworkedJson = chatClient.prompt(promptForRework).call().content();*/

                restructuredJson = generateWorkoutPlan(muscleInput,userLevel,exercises,invalidationReasons);
                validationResult = mapper.readTree(validateByLlm(restructuredJson, muscleInput));
            }

            return ResponseEntity.ok(restructuredJson);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process request: " + e.getMessage()));
        }
    }



    private String getMuscleGroupFromPrompt(String muscleGroup,String level) {

        WorkoutStructure workout = workoutAIService.analyzeWorkoutPrompt(muscleGroup,level);

        String finalPrompt = String.format(systemPromptMuscleIdentifier, Muscle.getAllEnumValues()) +
        String.format(userPromptMuscleIdentifier, muscleGroup);
        System.out.println("input for muscle idetifier"+finalPrompt);
        String muscleGroupsFromAi = chatClient.prompt(finalPrompt).call().content();
        System.out.println("output for muscle idetifier"+muscleGroupsFromAi);
        return muscleGroupsFromAi;
    }

    public Optional<ExercisesFinal> testAiOptions(String muscle, String level){

        WorkoutStructure structure =  workoutAIService.analyzeWorkoutPrompt(muscle, level);
        Optional<ExercisesFinal> plan = workoutAIService.generateWorkoutPlan(structure);
        return plan;
    }

    public String validateByLlm(String toBeValidated,String muscles){
        String formattedValidatorPrompt = String.format(planValidator,muscles,toBeValidated,finalJsonSchema);
        System.out.println("input for validator:"+formattedValidatorPrompt);
        String validatorResponse = chatClient.prompt(formattedValidatorPrompt).call().content();
        System.out.println("output for validator:"+validatorResponse);

        return validatorResponse;
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
            filteredSet = filteredSet.stream()
                        .filter(e -> e.getPrimaryMuscles().stream().anyMatch(pMuscle::contains))
                        .collect(Collectors.toList());
            }
        if (sMuscle != null) {
            filteredSet = filteredSet.stream()
                    .filter(e -> e.getSecondaryMuscles().stream().anyMatch(sMuscle::contains))
                    .collect(Collectors.toList());
        }
        return filteredSet;
    }
}
