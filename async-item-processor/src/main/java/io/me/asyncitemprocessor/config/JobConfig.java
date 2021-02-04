package io.me.asyncitemprocessor.config;

import io.me.asyncitemprocessor.domain.Customer;
import io.me.asyncitemprocessor.domain.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

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
        reader.setFetchSize(1000);
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
    ItemProcessor<Customer, Customer> itemProcessor() {
        return customer -> {
            Thread.sleep(new Random().nextInt(10));
            return new Customer(customer.getId(),
                    customer.getFirstName().toUpperCase(),
                    customer.getLastName().toUpperCase(),
                    customer.getbirthdate());
        };
    }

    @Bean
    AsyncItemProcessor<Customer, Customer> asyncItemProcessor() throws Exception {
        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();

        asyncItemProcessor.setDelegate(itemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        asyncItemProcessor.afterPropertiesSet();

        return asyncItemProcessor;
    }

    @Bean
    JdbcBatchItemWriter<Customer> customerItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES(:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    AsyncItemWriter<Customer> asyncItemWriter() throws Exception {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(customerItemWriter());
        asyncItemWriter.afterPropertiesSet();

        return asyncItemWriter;
    }

    @Bean
    Step step() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(1000)
                .reader(pagingItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
