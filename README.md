# spring-batch-developer-course
Sources written during the ultimate spring batch developer course on Udemy.

## hello-world
Smallest possible demo including spring batch database repository.

## transitions
Demonstrates transitions between steps.

## flow
Simple job flow demo. Uses one flow in two different jobs.

## split
Demonstrates splitted step execution for parallel execution.

## decisions
Demonstrates conditional flow based on state of job execution.

## nested jobs
Demonstrates nested job configuration and start definition by application.properties.

## listeners

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




