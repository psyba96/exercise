package org.js.ai;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.js.Exercise;
import org.springframework.ai.chat.client.ChatClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

@Service
public class WorkoutAIService {
    private static final Logger logger = Logger.getLogger(WorkoutAIService.class.getName());
    private final ChatClient chatClient;
    private final SystemService systemService;
    private final ResourceLoader resourceLoader;
    private final AIConfig aiConfig;


    public WorkoutAIService(SystemService systemService, ResourceLoader resourceLoader, AIConfig aiConfig) {
        this.systemService = systemService;
        this.resourceLoader = resourceLoader;
        this.aiConfig = aiConfig;
        this.chatClient = aiConfig.getChatClient();

    }

    public String loadWorkoutDirectives() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompts/Plan directives for LLM.txt");
        return Files.readString(Paths.get(resource.getURI()));
    }

    public WorkoutStructure analyzeWorkoutPrompt(String muscleGroup, String level) {
        String prompt = String.format(
                "Identify the user level, focus area for workout, and the focus muscles for the workout based on user prompt: %s , Level input: %s",
                muscleGroup, level
        );
        logger.info("Sending prompt to AI: " + prompt);
        WorkoutStructure structure = chatClient
                .prompt(prompt)
                .call()
                .entity(WorkoutStructure.class);
        logger.info("Structure from AI: " + structure.toString());
        return structure;
    }

    public Optional<ExercisesFinal> generateWorkoutPlan(WorkoutStructure structure) {
        try {

            //Step 1
            List<Exercise> exercises = systemService.getExercises(null,structure.getFocusMuscles(),null,null,null);
            String directives = loadWorkoutDirectives();
            String stepOnePrompt = String.format("""
                            CONTEXT: %s
                            USER REQUEST:
                            - available exercises: %s
                            - Client Goal: %s
                            - Fitness Level: %s
                            - Focus Muscle/s: %s
                            
                            Based on the provided workout directives, create a detailed workout session plan that follows the structured approach outlined in the context.
                            IMPORTANT: Only include exercises from the list of available exercises, return exact names from the list in the output plan
                            
                            """,
                    directives,
                    exercises.toString(),
                    structure.getFocus(),
                    structure.getLevel(),
                    structure.getFocusMuscles()

            );
            logger.info("Sending prompt to AI for plan with exercises ");
            String plan1 = chatClient.prompt(stepOnePrompt).call().content();
            logger.info("Response 1:" + plan1);
            String validationPrompt = """
                You are a fitness trainer advising on a workout routine based on a finite list of exercises, check provided workout plan for all sections including warm up,cool down,main to identify if there are any exercises that are not from the list(names should be an exact match,"cable_hip_adduction" does not match exactly with "Cable Hip Adduction"), if there are any such occurrences, replace those occurrences with a suitable exercise from the list.
                Make sure the plan does not include similar exercises for example if a plan includes dumbbell goblet squat and kettlebell goblet squat replace kettlebell goblet squat with something else.
                Ensure the workout plan targets the intended muscle group through movements across multiple planes of motion, avoiding exercises that train the muscle in the same plane repeatedly.
                Ensure that warm-up, main workout, and stretching (cool-down) exercises are organized into three distinct arrays, each corresponding to their specific phase.   
                provided workout plan : %s
                finite list of exercises : %s
                """.formatted(plan1,exercises.toString());
            //logger.info("validation prompt:" + validationPrompt);
            String validationOutput = chatClient.prompt(validationPrompt).call().content();
            logger.info("Response valid:" + validationOutput);
            String promptTwo = String.format("Convert the following string into a structured entity with appropriate fields and values , input : %s",validationOutput);
            Exercises plan2 = chatClient.prompt(promptTwo).call().entity(Exercises.class);
            logger.info("Response 2:" + plan2.toString());

            return Optional.of(systemService.convertToFinal(plan2));
            //return Optional.of(plan1);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load workout directives", e);
        }
    }
}

