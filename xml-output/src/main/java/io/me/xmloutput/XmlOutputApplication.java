package io.me.xmloutput;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class XmlOutputApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlOutputApplication.class, args);
    }

}
