package org.js;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Muscle {

    CHEST("Pectorals"),
    BACK("Latissimus Dorsi, Trapezius"),
    SHOULDERS("Deltoids"),
    TRICEPS("Triceps"),
    BICEPS("Biceps Brachii"),
    FOREARMS("Flexors, Extensors"),
    ABDOMINALS("Rectus Abdominis, Obliques"),
    LOWER_BACK("lower back"),
    GLUTES("Gluteus Maximus, Medius"),
    QUADRICEPS("Rectus Femoris, Vastus Group"),
    HAMSTRINGS("Biceps Femoris, Semitendinosus, Semimembranosus"),
    CALVES("Gastrocnemius, Soleus"),
    CARDIO("Cardiovascular System"),
    NECK("Neck, Neck and Back"),
    ADDUCTORS("Adductors"),
    ABDUCTORS("Abductors"),
    LATS("Lats, Latissimus Dorsi, Trapezius"),
    TRAPS("Traps"),
    MIDDLE_BACK("Middle Back"),
    OBLIQUES("Obliques"),
    HIP_FLEXORS("Psoas major and iliacus,Rectus femoris,Sartorius,Pectineus,Tensor fasciae latae (TFL)")
    ;
    private final String description;

    Muscle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Muscle fromValue(String value) {
        for (Muscle muscle : Muscle.values()) {
            if (muscle.name().equalsIgnoreCase(value)) {
                return muscle;
            } else if (muscle.description.equalsIgnoreCase(value)) {
                return muscle;
            }
        }
        throw new IllegalArgumentException("Unknown muscle: " + value);
    }

    public static List<Muscle> convertToMuscleList(String muscleGroup) {
        if (muscleGroup == null || muscleGroup.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(muscleGroup.split(","))
                .map(String::trim)
                .map(muscle -> muscle.toUpperCase().replace(" ", "_"))
                .map(Muscle::valueOf)
                .collect(Collectors.toList());
    }

    public static String getAllEnumValues() {
        return Arrays.stream(Muscle.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

}
