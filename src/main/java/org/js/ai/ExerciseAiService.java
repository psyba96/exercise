package org.js.ai;

import org.js.Exercise;
import org.js.ExerciseService;
import org.js.Exercises;
import org.js.WorkoutExercise;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

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
}
