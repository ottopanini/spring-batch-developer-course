package io.me.multipleflatfiles.config;

import io.me.multipleflatfiles.domain.Customer;
import io.me.multipleflatfiles.domain.CustomerFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class JobConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Value("classpath*:/data/customer*.csv")
    private Resource[] inputFiles;

    public JobConfig(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    MultiResourceItemReader<Customer> multiResourceItemReader() {
        MultiResourceItemReader<Customer> itemReader = new MultiResourceItemReader<>();
        itemReader.setDelegate(customerItemReader());
        itemReader.setResources(inputFiles);

        return itemReader;
    }

    @Bean
    FlatFileItemReader<Customer> customerItemReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "firstName", "lastName", "birthdate");

        DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
        customerLineMapper.setLineTokenizer(tokenizer);
        customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
        customerLineMapper.afterPropertiesSet();

        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setLineMapper(customerLineMapper);
        return reader;
    }

    @Bean
    ItemWriter<Customer> cursorItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .<Customer, Customer>chunk(10)
                .reader(multiResourceItemReader())
                .writer(cursorItemWriter())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
