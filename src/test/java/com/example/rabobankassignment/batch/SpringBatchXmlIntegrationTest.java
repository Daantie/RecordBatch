package com.example.rabobankassignment.batch;

import java.util.List;
import javax.sql.DataSource;

import com.example.rabobankassignment.batch.config.SpringBatchConfig;
import com.example.rabobankassignment.batch.config.SpringBatchXmlJobConfig;
import com.example.rabobankassignment.model.Record;
import com.example.rabobankassignment.model.RecordDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;

import static com.example.rabobankassignment.util.Constants.*;

@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { SpringBatchConfig.class, SpringBatchXmlJobConfig.class })
class SpringBatchXmlIntegrationTest {

    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    JobRepository jobRepository;

    @Autowired
    Job xmlBatchJob;

    @BeforeEach
    void setUp() {
        this.jobLauncherTestUtils = new JobLauncherTestUtils();
        this.jobLauncherTestUtils.setJobLauncher(jobLauncher);
        this.jobLauncherTestUtils.setJobRepository(jobRepository);
        this.jobLauncherTestUtils.setJob(xmlBatchJob);
        jdbcTemplate.update("delete from record");
        jdbcTemplate.update("delete from invalid_record");
    }

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void testCsvJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        RowMapper<Record> rowMapperValidRecord = (rs, rowNum) -> {
            Record record = new Record();
            record.setReference(rs.getLong(REFERENCE));
            record.setAccountNumber(rs.getString(ACCOUNT_NUMBER));
            record.setDescription(rs.getString(DESCRIPTION));
            record.setStartBalance(rs.getBigDecimal(START_BALANCE));
            record.setMutation(rs.getBigDecimal(MUTATION));
            record.setEndBalance(rs.getBigDecimal(END_BALANCE));
            return record;
        };
        List<Record> recordList = jdbcTemplate.query("SELECT * FROM `record`", rowMapperValidRecord);
        Assertions.assertEquals(8, recordList.size());

        RowMapper<RecordDTO> rowMapperInvalidRecord = (rs, rowNum) -> new RecordDTO(
                rs.getLong(REFERENCE),
                rs.getString(ACCOUNT_NUMBER),
                rs.getString(DESCRIPTION),
                rs.getBigDecimal(START_BALANCE),
                rs.getBigDecimal(MUTATION),
                rs.getBigDecimal(END_BALANCE),
                rs.getString(REASON)
        );
        List<RecordDTO> invalidRecordList = jdbcTemplate.query("SELECT * FROM `invalid_record`", rowMapperInvalidRecord);
        Assertions.assertEquals(2, invalidRecordList.size());
        Assertions.assertEquals("Invalid end balance. Should be: 4490.00", invalidRecordList.get(0).getReason());
        Assertions.assertEquals("Invalid end balance. Should be: 4980.00", invalidRecordList.get(1).getReason());
    }
}
