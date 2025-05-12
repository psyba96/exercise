package org.js;

import jakarta.persistence.Entity;

@NoIdOnInsert(message = "Exercise id is not allowed to be set on insert")
public class NewExerciseDTO  extends Exercise{
}
