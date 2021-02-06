package io.me.remotechunking;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableBatchProcessing
@EnableBatchIntegration
@IntegrationComponentScan
public class RemoteChunkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemoteChunkingApplication.class, args);
    }

}
