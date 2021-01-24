package io.me.listeners.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class Joblistener implements JobExecutionListener {
    private final JavaMailSender mailSender;

    public Joblistener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(String.format("%s is starting", jobName),
                String.format("Per your request, we are informing you that %s is starting", jobName));

        mailSender.send(mail);
    }

    private SimpleMailMessage getSimpleMailMessage(String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo("press.christian@googlemail.com");
        mailMessage.setText(text);
        mailMessage.setSubject(subject);

        return mailMessage;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(String.format("%s has completed", jobName),
                String.format("Per your request, we are informing you that %s has completed", jobName));

        mailSender.send(mail);
    }
}
