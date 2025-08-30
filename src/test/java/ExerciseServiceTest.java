import org.js.dto.NewExerciseDTO;
import org.js.exception.ResourceNotFoundException;
import org.js.repository.ExerciseRepository;
import org.js.service.ExerciseService;
import org.js.model.Exercise;
import org.js.model.Muscle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {


    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Bench Press");
        exercise.setPrimaryMuscles(Arrays.asList(Muscle.CHEST));
    }

    @Test
    void testCreateExercise() {
        NewExerciseDTO dto = new NewExerciseDTO();
        dto.setName("Bench Press");

        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise created = exerciseService.createExercise(dto);

        assertNotNull(created);
        assertEquals("Bench Press", created.getName());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void testUpdateExercise() {
        Map<String,Object> updatedExercise = new HashMap<>();
        updatedExercise.put("name", "Updated Bench Press");
        Exercise mock = new Exercise();
        mock.setName("Updated Bench Press");
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(mock);

        Exercise result = exerciseService.updateExercise(1, updatedExercise);

        assertNotNull(result);
        assertEquals("Updated Bench Press", result.getName());
        verify(exerciseRepository).findById(1);
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void testUpdateExercise_NotFound() {
        when(exerciseRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                exerciseService.updateExercise(999, new HashMap<String,Object>())
        );
    }

    @Test
    void testGetExercises() {
        List<Exercise> exercises = Arrays.asList(exercise);
        when(exerciseRepository.findAll(any())).thenReturn(exercises);

        List<Exercise> result = exerciseService.getExercises(
                "Bench Press",
                Arrays.asList(Muscle.CHEST),
                null,
                "Intermediate",
                "Push",
                null,
                null,
                null
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(exerciseRepository).findAll(any());
    }
}