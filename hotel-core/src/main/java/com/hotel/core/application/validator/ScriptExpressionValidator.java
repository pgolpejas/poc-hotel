package com.hotel.core.application.validator;

import java.util.Objects;

import com.hotel.core.application.validator.ScriptExpression;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ScriptExpressionValidator implements ConstraintValidator<ScriptExpression, Object> {

  private final SpelExpressionParser parser;

  private Expression expression;

  public ScriptExpressionValidator(@NotNull final SpelExpressionParser spelExpressionParser) {
    this.parser = spelExpressionParser;
  }

  @Override
  public void initialize(final ScriptExpression annotation) {

    final String rawExpression = annotation.value();

    if (!Objects.nonNull(rawExpression)) {
      throw new IllegalArgumentException(ScriptExpression.class.getSimpleName() + " must not be null");
    }

    this.expression = this.parser.parseExpression(rawExpression);
  }

  @Override
  public boolean isValid(final Object obj, final ConstraintValidatorContext constraintValidatorContext) {
    if (obj == null) {
      return true;
    }

    return Boolean.TRUE.equals(this.expression.getValue(obj, Boolean.class));
  }

}
