# spring-batch-developer-course
Sources written during the ultimate spring batch developer course on Udemy.

## Getting started

### hello-world
Smallest possible demo including spring batch database repository.

### transitions
Demonstrates transitions between steps.

### flow
Simple job flow demo. Uses one flow in two different jobs.

### split
Demonstrates splitted step execution for parallel execution.

### decisions
Demonstrates conditional flow based on state of job execution.

### nested jobs
Demonstrates nested job configuration and start definition by application.properties.

### listeners

types:
- JobExecutionListener
- StepExecutionListener
- CHunkListener
- ItemReadListener
- ItemProcessListener
- ItemWriteListener

Example shows use of batch listeners with:
- ChunkListener
- JobExecutionListener
    - sends mail before and after job starts
    - mail sending needs appropriate settings in application.properties
- configuration
    - item reader: uses ListItemReader

### job parameters

Uses parameter in demo to be printed out in tasklet by injection.

```java
    @Bean
    @StepScope
    Tasklet helloWorldTasklet(@Value("#{jobParameters['message']}") String message) {
        return ((stepContribution, chunkContext) ->  {
            System.out.println(message);
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .tasklet(helloWorldTasklet(null))
                .build();
    }
```
*Notice* helloWorldTasklet is defined in the step configuration to be called with null. Actually this will be replaced by the value of the parameter by Spring. Spring uses it only as a reference to the bean generated just above.

`@StepScope` means beans are lazily instantiated when the step using them is executed.

run with: `java -jar target\job-parameters-0.0.1-SNAPSHOT.jar message=hello`

first version can be executed multiple times. 2nd version is with spring batch database - repeated runs using same message will fail.

`Caused by: org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters={message=hello}.  If you want to run this job again, change the parameters.`








