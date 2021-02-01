package io.me.itemprocessorinterface.config;

import io.me.itemprocessorinterface.domain.Customer;
import io.me.itemprocessorinterface.domain.CustomerRowMapper;
import io.me.itemprocessorinterface.processor.UpperCaseItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    public JobConfig(JobBuilderFactory jobBuilderFactory,
                     StepBuilderFactory stepBuilderFactory,
                     DataSource dataSource) {
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
    StaxEventItemWriter<Customer> xmlItemWriter() throws Exception {
        XStreamMarshaller marshaller = new XStreamMarshaller();

        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);
        marshaller.setAliases(aliases);

        StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setRootTagName("customers");
        itemWriter.setMarshaller(marshaller);
        String customerOutput = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
        System.out.println(">> Xml Output Path: " + customerOutput);
        itemWriter.setResource(new FileSystemResource(customerOutput));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    ItemProcessor<Customer, Customer> itemProcessor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    Step step() throws Exception {
        return stepBuilderFactory.get("step566")
                .<Customer, Customer>chunk(10)
                .reader(pagingItemReader())
                .processor(itemProcessor())
                .writer(xmlItemWriter())
                .build();
    }

    @Bean
    Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
