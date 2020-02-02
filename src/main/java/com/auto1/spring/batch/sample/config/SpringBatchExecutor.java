package com.auto1.spring.batch.sample.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringBatchExecutor {

    private final JobLauncher jobLauncher;

    private final Job job;

    @SneakyThrows
    public void execute(final String filePath) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("filePath", filePath)
                .toJobParameters();

        jobLauncher.run(job, parameters);
    }

}
