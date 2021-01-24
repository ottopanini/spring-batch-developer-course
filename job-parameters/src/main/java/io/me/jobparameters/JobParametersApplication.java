package io.me.jobparameters;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableBatchProcessing
@SpringBootApplication
public class JobParametersApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobParametersApplication.class, args);
    }

}
