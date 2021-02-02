package io.me.compositeitemprocessor.domain;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class CustomerValidator implements Validator<Customer> {
    @Override
    public void validate(Customer customer) throws ValidationException {
        if (customer.getFirstName().startsWith("A")) {
            throw new ValidationException("First beginning with A are invalid: " + customer);
        }
    }
}
