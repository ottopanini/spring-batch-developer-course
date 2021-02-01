package io.me.filteringitemprocessor.processor;

import io.me.filteringitemprocessor.domain.Customer;
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
