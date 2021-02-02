package io.me.itemvalidator;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class ItemValidatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemValidatorApplication.class, args);
    }

}
