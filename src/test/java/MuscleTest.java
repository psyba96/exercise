import org.js.Muscle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MuscleTest {

    @Test
    void testFromValue_WithEnumName() {
        assertEquals(Muscle.CHEST, Muscle.fromValue("CHEST"));
        assertEquals(Muscle.BACK, Muscle.fromValue("BACK"));
    }

    @Test
    void testFromValue_WithDescription() {
        assertEquals(Muscle.CHEST, Muscle.fromValue("Pectorals"));
        assertEquals(Muscle.TRICEPS, Muscle.fromValue("Triceps"));
    }

    @Test
    void testFromValue_CaseInsensitive() {
        assertEquals(Muscle.CHEST, Muscle.fromValue("chest"));
        assertEquals(Muscle.CHEST, Muscle.fromValue("pectorals"));
    }

    @Test
    void testFromValue_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () ->
                Muscle.fromValue("NonExistentMuscle")
        );
    }

    @Test
    void testGetDescription() {
        assertEquals("Pectorals", Muscle.CHEST.getDescription());
        assertEquals("Triceps", Muscle.TRICEPS.getDescription());
    }
}