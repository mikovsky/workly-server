package com.mikovskycloud.workly.validation;

import com.mikovskycloud.workly.exceptions.WorklyBadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class RequestValidator {

    public void throwIfRequestIsInvalid(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw WorklyBadRequestException.of(bindingResult);
        }
    }

}
