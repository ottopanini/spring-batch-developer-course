package io.me.startingajob.controller;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobLaunchingController {
    private final JobOperator jobOperator;

    public JobLaunchingController(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void launch(@RequestParam("name") String name) throws JobParametersInvalidException,
            JobInstanceAlreadyExistsException, NoSuchJobException {
        jobOperator.start("job", String.format("name=%s", name));
    }
}
