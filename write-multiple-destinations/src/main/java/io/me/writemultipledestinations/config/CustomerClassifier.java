package io.me.writemultipledestinations.config;

import io.me.writemultipledestinations.domain.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {
    private final ItemWriter<Customer> evenItemWriter;
    private final ItemWriter<Customer> oddItemWriter;

    public CustomerClassifier(ItemWriter<Customer> oddItemWriter, ItemWriter<Customer> evenItemWriter) {
        this.evenItemWriter = evenItemWriter;
        this.oddItemWriter = oddItemWriter;
    }

    @Override
    public ItemWriter<? super Customer> classify(Customer customer) {
        return customer.getId() % 2 == 0 ? evenItemWriter : oddItemWriter;
    }
}
