package com.example.rabobankassignment.batch.config;

import com.example.rabobankassignment.batch.RecordFieldSetMapper;
import com.example.rabobankassignment.batch.StepSkipListener;
import com.example.rabobankassignment.exception.InvalidEndBalanceException;
import com.example.rabobankassignment.model.Record;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.PlatformTransactionManager;

import static com.example.rabobankassignment.util.Constants.*;

@Configuration
public class SpringBatchCsvJobConfig {
    @Value("classpath:input/records.csv")
    private Resource inputCsv;

    public ItemReader<Record> csvItemReader(Resource inputCsv) throws UnexpectedInputException {
        FlatFileItemReader<Record> reader = new FlatFileItemReader<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = {REFERENCE, ACCOUNT_NUMBER, DESCRIPTION, START_BALANCE, MUTATION, END_BALANCE};
        tokenizer.setNames(tokens);
        reader.setEncoding("UTF-8");
        reader.setResource(inputCsv);
        DefaultLineMapper<Record> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean(name = "csvStep")
    protected Step csvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           ItemProcessor<Record, Record> processor, ItemWriter<Record> writer, SimpleJdbcInsert simpleJdbcInsert) {
        return new StepBuilder("csvStep", jobRepository)
                .<Record, Record> chunk(5, transactionManager)
                .reader(csvItemReader(inputCsv))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(5)
                .skip(InvalidEndBalanceException.class)
                .listener(new StepSkipListener(simpleJdbcInsert))
                .build();
    }

    @Bean(name = "csvBatchJob")
    public Job job(JobRepository jobRepository, @Qualifier("csvStep") Step csvStep) {
        return new JobBuilder("csvBatchJob", jobRepository)
                .preventRestart()
                .start(csvStep)
                .build();
    }
}
