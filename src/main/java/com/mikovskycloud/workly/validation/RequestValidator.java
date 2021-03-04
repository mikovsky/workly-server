package com.mikovskycloud.workly.validation;

import com.mikovskycloud.workly.exceptions.WorklyBadRequestException;
import one.util.streamex.StreamEx;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequestValidator {

    public void throwIfRequestIsInvalid(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> validationResults = new HashMap<>();

            Map<String, List<FieldError>> errorsByFieldName = StreamEx.of(bindingResult.getFieldErrors())
                    .groupingBy(FieldError::getField);

            for (String fieldName : errorsByFieldName.keySet()) {
                List<FieldError> fieldErrors = errorsByFieldName.get(fieldName);

                List<String> messages = StreamEx.of(fieldErrors)
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();

                String joinedMessage = Strings.join(messages, ',');
                validationResults.put(fieldName, joinedMessage);
            }

            throw WorklyBadRequestException.of(validationResults);
        }
    }

}
