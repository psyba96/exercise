package org.js;

import ch.qos.logback.core.net.SyslogOutputStream;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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

