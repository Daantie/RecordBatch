package com.example.rabobankassignment.batch.config;

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
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchXmlJobConfig {

    @Value("classpath:input/records.xml")
    private Resource inputXml;

    public ItemReader<Record> xmlItemReader(Resource inputXml) {
        return new StaxEventItemReaderBuilder<Record>()
                .name("xmlReader")
                .resource(inputXml)
                .addFragmentRootElements("record")
                .unmarshaller(unMarshaller())
                .build();
    }

    public Jaxb2Marshaller unMarshaller() {
        Jaxb2Marshaller xmlMarshaller = new Jaxb2Marshaller();
        xmlMarshaller.setClassesToBeBound(Record.class);
        return xmlMarshaller;
    }

    @Bean
    public Step xmlStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                        ItemProcessor<Record, Record> processor, ItemWriter<Record> writer, SimpleJdbcInsert simpleJdbcInsert) {
        return new StepBuilder("xmlStep", jobRepository)
                .<Record, Record>chunk(5, transactionManager)
                .reader(xmlItemReader(inputXml))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(5)
                .skip(InvalidEndBalanceException.class)
                .listener(new StepSkipListener(simpleJdbcInsert))
                .build();
    }

    @Bean(name = "xmlBatchJob")
    public Job xmlJob(JobRepository jobRepository, @Qualifier("xmlStep") Step xmlStep) {
        return new JobBuilder("xmlBatchJob", jobRepository)
                .start(xmlStep)
                .preventRestart()
                .build();
    }

}
