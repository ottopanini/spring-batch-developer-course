package io.me.compositeitemprocessor.processor;

import io.me.compositeitemprocessor.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

public class FilterItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        if (customer.getId() % 2 == 0) {
            return null;
        }
        else {
            return customer;
        }
    }
}
