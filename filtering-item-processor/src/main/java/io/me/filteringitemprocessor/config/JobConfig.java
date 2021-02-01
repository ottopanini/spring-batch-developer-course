package io.me.filteringitemprocessor.config;

import io.me.filteringitemprocessor.domain.Customer;
import io.me.filteringitemprocessor.domain.CustomerLineAggregator;
import io.me.filteringitemprocessor.domain.CustomerRowMapper;
import io.me.filteringitemprocessor.processor.FilterItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    JdbcPagingItemReader<Customer> pagingItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("FROM customer");
        HashMap<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    FlatFileItemWriter<Customer> jsonItemWriter() throws Exception {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new CustomerLineAggregator());
        String customerOutput = File.createTempFile("customerOutput", ".json").getAbsolutePath();
        System.out.println(">> Json Output Path: " + customerOutput);
        itemWriter.setResource(new FileSystemResource(customerOutput));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    ItemProcessor<Customer, Customer> itemProcessor() {
        return new FilterItemProcessor();
    }

    @Bean
    Step step() throws Exception {
        return stepBuilderFactory.get("step666")
                .<Customer, Customer>chunk(10)
                .reader(pagingItemReader())
                .processor(itemProcessor())
                .writer(jsonItemWriter())
                .build();
    }

    @Bean
    Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

}
