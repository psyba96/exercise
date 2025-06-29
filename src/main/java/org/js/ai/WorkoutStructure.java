package org.js.ai;

import org.js.Muscle;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.logging.Logger;

public class WorkoutStructure {
    private String level;
    private String focus;
    private List<Muscle> focusMuscles;
    private static final Logger logger = Logger.getLogger(WorkoutStructure.class.getName());

    public List<Muscle> getFocusMuscles() {
        return focusMuscles;
    }

    public void setFocusMuscles(List<Muscle> focusMuscles) {
        this.focusMuscles = focusMuscles;
    }

    @Override
    public String toString() {
        return "WorkoutStructure{" +
                "level='" + level + '\'' +
                ", focus='" + focus + '\'' +
                ", focusMuscles=" + focusMuscles +
                '}';
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    }

