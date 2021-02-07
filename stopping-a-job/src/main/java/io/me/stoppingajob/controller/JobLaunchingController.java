package io.me.stoppingajob.controller;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobLaunchingController {
    private final JobOperator jobOperator;

    public JobLaunchingController(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    /**
     * @param name
     * @return the job id
     * @throws JobParametersInvalidException
     * @throws JobInstanceAlreadyExistsException
     * @throws NoSuchJobException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public long launch(@RequestParam("name") String name) throws JobParametersInvalidException,
            JobInstanceAlreadyExistsException, NoSuchJobException {
        return jobOperator.start("job", String.format("name=%s", name));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void stop(@PathVariable("id") Long id) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        jobOperator.stop(id);
    }
}
