package org.js.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.js.model.Exercise;

public class NoIdOnInsertValidator implements ConstraintValidator<NoIdOnInsert, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof Exercise) {
            Exercise entity = (Exercise) value;
            return entity.getId() == null;
        }
        return true;
    }
}

