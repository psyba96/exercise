package org.js.dto;

import org.js.annotation.NoIdOnInsert;
import org.js.model.Exercise;

@NoIdOnInsert(message = "Exercise id is not allowed to be set on insert")
public class NewExerciseDTO  extends Exercise {
}
