package org.js;

import com.fasterxml.jackson.annotation.JsonCreator;

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


}
