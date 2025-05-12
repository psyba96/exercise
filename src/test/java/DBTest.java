import org.js.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DBTest {

    @Autowired
    private ExerciseRepository repository;

    @Test
    void testDatabaseConnection() {
        // Query the repository to check DB connectivity
        long count = repository.count();

        // Assert if the table exists
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}