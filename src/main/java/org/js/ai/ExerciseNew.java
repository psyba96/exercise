package org.js.ai;

public class ExerciseNew {
    private String name;


    public ExerciseNew(){}

    public ExerciseNew(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExerciseNew{" +
                "name='" + name + '}';
    }


}
