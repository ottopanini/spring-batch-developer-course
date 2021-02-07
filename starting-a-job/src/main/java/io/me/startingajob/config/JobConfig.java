package io.me.startingajob.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig implements ApplicationContextAware {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    private ApplicationContext applicationContext;

    public JobConfig(JobBuilderFactory jobBuilderFactory,
                     StepBuilderFactory stepBuilderFactory,
                     JobExplorer jobExplorer,
                     JobRepository jobRepository,
                     JobRegistry jobRegistry,
                     JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor registrar = new JobRegistryBeanPostProcessor();
        registrar.setJobRegistry(jobRegistry);
        registrar.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        registrar.afterPropertiesSet();

        return registrar;
    }

    @Bean
    JobOperator jobOperator() throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRegistry(jobRegistry); // map jobname -> reference to job
        jobOperator.afterPropertiesSet();

        return jobOperator;
    }

    @Bean
    @StepScope
    Tasklet tasklet(@Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
            System.out.println(String.format("The job ran for %s", name));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(stepBuilderFactory.get("step602")
                        .tasklet(tasklet(null))
                        .build())
                .build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
