package io.me.itemprocessorinterface.processor;

import io.me.itemprocessorinterface.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

import java.util.Locale;

public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        return new Customer(customer.getId(),
                customer.getFirstName().toUpperCase(),
                customer.getLastName().toUpperCase(),
                customer.getbirthdate());
    }
}
