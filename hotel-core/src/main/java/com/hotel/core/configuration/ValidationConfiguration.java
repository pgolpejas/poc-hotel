package com.hotel.core.configuration;

import com.hotel.core.application.validator.ScriptExpression;
import com.hotel.core.application.validator.ScriptExpressionValidator;
import jakarta.validation.ConstraintValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class ValidationConfiguration {

  @Bean
  public ConstraintValidator<ScriptExpression, Object> scriptExpressionProducer(final SpelExpressionParser parser) {
    return new ScriptExpressionValidator(parser);
  }

  @Bean
  public SpelExpressionParser spelExpressionParser() {
    return new SpelExpressionParser();
  }

}
