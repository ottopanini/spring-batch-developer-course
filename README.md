# spring-batch-developer-course
Sources written during the ultimate spring batch developer course on Udemy.

other sources: https://howtodoinjava.com/spring-batch/

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

## Input & Output data

ItemReader and ItemWriter  
Item = Record

### input interfaces

Demonstrates a simple item reader reading a list of strings.  
*notice*`.<String, String>chunk(2)` in the step config parametrises the types provided by the reader and consumed by the writer.

### database input

Use of database to read from. Example shows customer records readin in.
1. Cursor Item Reader
    - !demo implementation not thread save!
2. Paging Item Reader
    - fetchsize = chunk size
    - paging requires key to determine current page position &rarr; unique sort field required

### flat files

Demo of CSV reading.   
uses `DelimitedLineTokenizer`

### reading XML
Demo of reading xml data.
```xml
<customers>
	<customer>
		<id>1</id>
		<firstName>Mufutau</firstName>
		<lastName>Maddox</lastName>
		<birthdate>2016-06-05 19:43:51PM</birthdate>
	</customer>
	<customer>
		<id>2</id>
		<firstName>Brenden</firstName>
		<lastName>Cobb</lastName>
		<birthdate>2016-01-06 13:18:17PM</birthdate>
	</customer>
...
</customers>
```
one record is:
```xml
	<customer>
		<id>1</id>
		<firstName>Mufutau</firstName>
		<lastName>Maddox</lastName>
		<birthdate>2016-06-05 19:43:51PM</birthdate>
	</customer>
```

### multiple flat files
Reads multiple CSV files.
```java
    @Value("classpath*:/data/customer*.csv")
    private Resource[] inputFiles;

    ...

    @Bean
    MultiResourceItemReader<Customer> multiResourceItemReader() {
        ...
        itemReader.setResources(inputFiles);

        return itemReader;
    }

```
ResourceAware will handle storing resource information in generated objects.
```java
public class Customer implements ResourceAware {
    ...
    private Resource resource;

    ...
    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

```
### item stream
Demonstrates stateful job processing.
![](state_schema.png)  

![](item-sequence.png)

*notice* needs to be run twice to fully do the job.

### item writer
Demo writes to console output.

### database output
Writing into a database.

### flat file output
Demonstrates writing of a file.
2nd version uses JSON format.

### xml output
Shows writing of a xml file.

### write multiple destinations
How to write to multiple destinations. Reads from DB. Writes one XML and one JSON file.
First version uses CompositeItemWriter.
Second version writes the odd items to the JSON file and the even numbers to XML file.
```java
    ...
    .stream(xmlItemWriter()) // inform spring batch the underlying writers used by the 
    .stream(jsonItemWriter()) // ClassifierCompositeItemWriter are stateful
    ...
```
## Processing Models
Every process call returning null will be filtered from the output. 
### item processor interface
Shows general use of an item processor transforming the name and firstname of customers to upper case.

### filtering item processor
Filters items while processing. Writes only items with odd ids to Json file.

### item-validator
How to validate items in item processor.

### composite item processor
Chaining processing steps. Combines the filter item processor from previous lesson with the uppercase item processor from the other previous lesson.

## Learn How To Handle Errors and Scale Applications
### restart
Shows how to restart a step. The test application needs 3 runs to complete all steps.

### retry
If started with console parameter "retry=processing" processing will fail at 42 some times and then succeed. The item processor is retrying only the one item that has failed. For the item writer this is different: it will retry the whole chunk if one item has failed.  
Item readers can not be retried.

### skip
If started with console parameter "skip=processing" processing will fail at item 42 and then retry the whole chunk but this time item 42 won't be proceeded. For the item writer this is different: at first it fails with the parameter "skip=writer" and retry the whole chunk one item by time to figure out by wich item the exception occurs to have all other written successfully.

### skip (retry) listeners
Demonstrates skip listeners at first. Skip listeners can become handy when skipped items should be stored somehow to get reprocessed later as an example. Interesting is here that the listener of the processor is called after the chunk is executed but the listener of the writer is just called when the next item is processed (of course after the chunk has been restarted). 

## how to scale
1. Multi-threaded step
    - add task executor to each step - each chunk is processed on its own thread
2. Async ItemProcessor/ItemWriter
    - item processor returns a future - the processor logic itself is executed in a different thread
    - the item writer then unwraps the future and writes the data
    - allows to scale the processor logic within the same vm
3. Partitioning
    - dividing data into partitions so it can be parallelised
    - advantage over multi-threaded step execution: each partition is executed as an independant step execution -> thread safety
4. Remote chunking
    - Processing and writing occur in a remote 'slave' 
    - read is done by the master - but reads the whole records and sends them into processing -> much more I/O

### multi threaded step
Shows chunk processing, each on its own thread. Transfers from one db to another in chunks of 1000 items. Uses SimpleAsyncTaskExecutor which is not recommended in any production environment (uses no pooling, better use ThreadPoolExecutioners in that regard instead).  

Performance gain (in opposition to the SimpleAsyncTaskExecutor is not used):
18s vs 34s (100000 items) 

Restartability is lost by this execution model because it is not possible to know which thread was the last executing the task.

### Async item processor
Demonstrates use of an async item processor. There is a sleep phase while processing each item to simulate external processes. 
9mins vs 24s (huge improvement)
Ordered writing is not supported because of the asynchrnous processing and writing.

### Local partitioning
Demonstrates dividing data into partitions. Restartable.

### remote partitioning
Scale over multiple JVMs. RabbitMQ used for scaling. The message handler partitioner uses a simplified solution to know when the job is done - it just pulls the jobrepo. In real life you would implement a listener to check the messages returned. The stepExecutionRequestHandler() is the heart of the communication.
Master/Slave Role can be activated on startup by profile.
This is fully restartable. Low Overhead.

### remote chunking
Scale chunking over multiple JVMs. If Input is hardly shardable. The master does the reading and sends it over the wire (in opposition to remote partitioning which just only sends a description of the 'page' to read). 

## Job Orchestration & Spring Integration
### Introduction - starting a job
Web controller to start a job. At first a REST controller was implemented. A job can be started via:
```win
curl --data "name=foo" localhost:8080
``` 
starts step 'foo'.  
Second version uses another approach where the job to start can be provided as string. 
```win
curl --data "name=bar" localhost:8080
``` 

### Stopping a job
Simple application example of how to stop a job.
















