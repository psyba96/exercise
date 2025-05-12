package org.js;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Muscle {
    // Upper Body
    CHEST("Pectorals"),
    BACK("Latissimus Dorsi, Trapezius"),
    SHOULDERS("Deltoids"),

    TRICEPS("arms"),
    BICEPS("arms"),
    FOREARMS("Flexors, Extensors"),

    // Core
    ABS("Rectus Abdominis, Obliques"),
    LOWER_BACK("Erector Spinae"),

    // Lower Body
    GLUTES("Gluteus Maximus, Medius"),
    QUADRICEPS("Rectus Femoris, Vastus Group"),
    HAMSTRINGS("Biceps Femoris, Semitendinosus, Semimembranosus"),
    CALVES("Gastrocnemius, Soleus"),

    // Full Body
    CARDIO("Cardiovascular System");

    private final String description;

    Muscle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
