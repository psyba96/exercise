
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import org.js.model.Exercise;
import org.js.model.Muscle;
import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise();
    }

    @Test
    void testExerciseCreation() {
        assertNotNull(exercise);
        assertNull(exercise.getId());
        assertEquals(0, exercise.getVersion());
    }

    @Test
    void testSetAndGetName() {
        String name = "Bench Press";
        exercise.setName(name);
        assertEquals(name, exercise.getName());
    }

    @Test
    void testSetAndGetPrimaryMuscles() {
        List<Muscle> muscles = Arrays.asList(Muscle.CHEST, Muscle.SHOULDERS);
        exercise.setPrimaryMuscles(muscles);
        assertEquals(muscles, exercise.getPrimaryMuscles());
    }

    @Test
    void testSetAndGetSecondaryMuscles() {
        List<Muscle> muscles = Arrays.asList(Muscle.TRICEPS, Muscle.FOREARMS);
        exercise.setSecondaryMuscles(muscles);
        assertEquals(muscles, exercise.getSecondaryMuscles());
    }

    @Test
    void testSetAndGetInstructions() {
        List<String> instructions = Arrays.asList(
                "Lie on bench",
                "Grip the bar",
                "Lower to chest",
                "Push up"
        );
        exercise.setInstructions(instructions);
        assertEquals(instructions, exercise.getInstructions());
    }

    @Test
    void testSetAndGetForce() {
        String force = "Push";
        exercise.setForce(force);
        assertEquals(force, exercise.getForce());
    }

    @Test
    void testSetAndGetLevel() {
        String level = "Intermediate";
        exercise.setLevel(level);
        assertEquals(level, exercise.getLevel());
    }

    @Test
    void testSetAndGetMechanic() {
        String mechanic = "Compound";
        exercise.setMechanic(mechanic);
        assertEquals(mechanic, exercise.getMechanic());
    }

    @Test
    void testSetAndGetEquipment() {
        String equipment = "Barbell";
        exercise.setEquipment(equipment);
        assertEquals(equipment, exercise.getEquipment());
    }

    @Test
    void testSetAndGetCategory() {
        String category = "Strength";
        exercise.setCategory(category);
        assertEquals(category, exercise.getCategory());
    }

    @Test
    void testVersionIncrement() {
        int initialVersion = exercise.getVersion();
        exercise.setVersion(initialVersion + 1);
        assertEquals(initialVersion + 1, exercise.getVersion());
    }
}
