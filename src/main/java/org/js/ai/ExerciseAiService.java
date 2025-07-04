package org.js.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.js.*;
import org.js.Set;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ExerciseAiService {
    private ExerciseService exerciseService;
    private ChatClient client;

    public ExerciseAiService(ExerciseService exerciseService, ChatClient.Builder builder) {
        this.exerciseService = exerciseService;
        this.client = builder.build();
    }

    public WorkoutStructure identifyRequirement(String userPrompt,String userLevel){
        String promptOne = """
                You are a physical fitness interpreter, your task is to take a user prompt and extract vital details from it which will be passed to a personal trainer so that they can prepare a workout plan from the details. 
                Based on user prompt, extract below key elements to plan a workout for user
                1]user level(always lower case, if no context set "beginner", other options are "intermediate" and "advances")
                2]objective for workout
                3]focus muscles for the workout
                4]any secondary muscles that user needs to be involved
                4]equipment available
                5]additional notes 
                
                User prompt: %s
                User level : %s
                """;
        promptOne = String.format(promptOne,userPrompt,userLevel);
        System.out.println("prompt one input :" + promptOne);
        WorkoutStructure structure = client.prompt(promptOne).call().entity(WorkoutStructure.class);
        System.out.println("prompt one output :" + structure.toString());
        return structure;
    }

    public Map<String,Object> getWorkout(WorkoutStructure structure){
        System.out.println("Start get workout");
        List<Exercise> plyo = exerciseService.getExercises(null,structure.getPrimaryMuscleRequired(),null,structure.getUserFitnessLevel(),null,"plyometrics",null,null);
        List<Exercise> strength = exerciseService.getExercises(null,structure.getPrimaryMuscleRequired(),null,structure.getUserFitnessLevel(),null,"strength",null,null);
        List<Exercise> finalList = new ArrayList<>();
        finalList.addAll(plyo);
        finalList.addAll(strength);
        System.out.println("Start get workout prompt");
        /*String promptTwo = """
                You are a certified fitness coach.
                Based on the structured information about a user's target muscles, fitness level, equipment,goals and notes, generate a detailed workout plan that targets the primary and secondary muscles.
                Instructions:
                Prepare a workout routine for muscle group %s refer to available exercises below(retain exact exercise name in the prepared plan)
                Create 4–6 exercises
                Tailor difficulty to the user’s fitness level.
                
                Workout requirement:%s
                Use only the exercises listed here(retain exact exercise name from the list in the plan):%s
                
                """;*/
        String promptTwo= """
                System prompt:
                You are a certified fitness coach.
                Task:
                Based on the structured information below (target muscles, fitness level, equipment, goals, and notes), create a detailed workout plan.
                
                Instructions:
                The workout must target the primary and secondary muscles specified.
                Create exactly 4–6 exercises.
                Tailor the difficulty level to the user’s fitness level.
                Use only exercises provided in the list—do not create or rename any exercises. Retain exact exercise names exactly as they appear.
                
                Each exercise should include:
                Exercise name (verbatim from the list)
                Sets and reps suitable for the fitness level
                
                Do not include any exercises not on the list.
                
                Structured Workout Requirements:
                
                Muscle Group: %s
                Fitness Level: %s
                Workout Requirements: %s
                Available Exercises (use these only, no substitutions):%s
                
                Example:
                Workout Plan:
                1. Bench Press
                   - Sets: 4
                   - Reps: 8–10
                
                2. Incline Dumbbell Fly
                   - Sets: 3
                   - Reps: 10–12
                
                3. Push-Up
                   - Sets: 3
                   - Reps: 12–15
                
                4. Tricep Dips
                   - Sets: 3
                   - Reps: 10–12
                
                Checklist Before Responding:
                
                All exercises from the provided list?
                Tailored difficulty?
                Exercise names unchanged?
                
                """;

        promptTwo = String.format(promptTwo,structure.getPrimaryMuscleRequired().toString(),structure.getUserFitnessLevel(),structure.getUserObjective(),finalList.toString());
        System.out.println(promptTwo);
        Exercises plan = client.prompt(promptTwo).call().entity(Exercises.class);
        System.out.println(plan.toString());
        Map<String,Object> fp = getFinalPlan(plan,finalList);

        /*For mocking single exercise update
        org.js.Set mockSet = new Set();
        mockSet.setReps("12");
        mockSet.setSets("3");
        WorkoutExercise mockExercise1 = new WorkoutExercise();
        mockExercise1.setName("barbell one arm snatch");
        mockExercise1.setSet(mockSet);
        WorkoutExercise mockExercise2 = new WorkoutExercise();
        mockExercise2.setName("barbell one arm snatch");
        mockExercise2.setSet(mockSet);
        Exercises mockPlan = new Exercises();
        mockPlan.setPlan(List.of(mockExercise1,mockExercise2));
        List<Exercise> mockList = exerciseService.getExercises("barbell",null,null,null,null,null,null,null);
        Map<String,Object> fp = getFinalPlan(mockPlan,mockList);*/
        return fp;

    }

    public Map<String,Object> getFinalPlan(Exercises exercises,List<Exercise> finalList){
        Map<String,Object> finalPlan = new HashMap<>();
        finalPlan.put("plan",new ArrayList<Object>());
        List<Object> plan = (List<Object>) finalPlan.get("plan");
        for (WorkoutExercise ex:exercises.getPlan()){
            finalList.stream()
                    .filter(e -> e.getName().equals(ex.getName()))
                    .findFirst()
                    .ifPresent(e -> {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("name", e.getName());
                                obj.put("pMuscle",e.getPrimaryMuscles());
                                obj.put("equipment", e.getEquipment());
                                obj.put("difficulty", e.getLevel());
                                obj.put("instructions", e.getInstructions());
                                obj.put("gifHash", e.getGifHash());
                                obj.put("set", ex.getSet());
                                plan.add(obj);
        });
        }
        System.out.println(finalPlan.toString());
        return finalPlan;
    }

    public Exercise getNewExerciseForPlan (Map<String,Object> planToBeUpdated){
        String existingPlan = (String) planToBeUpdated.get("existingExercises");
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> existingPlanJson = mapper.readValue(
                    existingPlan,
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );
            List<Map<String, Object>> mappedPlan = existingPlanJson.stream()
                    .map(obj -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", obj.get("name"));
                        map.put("level", obj.get("difficulty"));
                        map.put("set", obj.get("set"));
                        map.put("target_muscle",obj.get("pMuscle"));
                        return map;
                    })
                    .collect(Collectors.toList());
            //Map<String, Object> existingExerciseJson = new HashMap<>();
            List<String> targetMuscles = mappedPlan.stream()
                    .map(obj -> (List<?>) obj.get("target_muscle"))
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> existingExercises = mappedPlan.stream()
                    .map(obj -> obj.get("name"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .distinct()
                    .collect(Collectors.toList());
            String level = mappedPlan.get(0).get("level").toString();
            Map<String,Object> set = (Map<String,Object>) mappedPlan.get(0).get("set");

            // Pick a random exercise
            List<Exercise> exercises = exerciseService.getExercises(null, Muscle.convertToMuscleList(targetMuscles),null,level,null,null,null,null);
            if (exercises.isEmpty()) {
                throw new IllegalStateException("No exercises found");
            }
            int randomIndex = ThreadLocalRandom.current().nextInt(exercises.size());

            Exercise rando = exercises.get(randomIndex);
            while (existingExercises.contains(rando.getName())){
                rando = exercises.get(randomIndex);
            }
            return rando;
        }
        catch (Exception e){
            return new Exercise();

        }
    }
}
