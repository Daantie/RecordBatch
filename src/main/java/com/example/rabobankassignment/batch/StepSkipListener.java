package com.example.rabobankassignment.batch;

import java.util.HashMap;
import java.util.Map;

import com.example.rabobankassignment.exception.InvalidEndBalanceException;
import com.example.rabobankassignment.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import static com.example.rabobankassignment.util.Constants.*;

public class StepSkipListener implements SkipListener<Record, Record> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepSkipListener.class);
    private final SimpleJdbcInsert jdbcInsert;

    public StepSkipListener(SimpleJdbcInsert jdbcInsert) {
        this.jdbcInsert = jdbcInsert;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        LOGGER.error("Read skipped with error: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(Record item, Throwable t) {
        LOGGER.error("Writer skipped with error: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(Record item, Throwable t) {
        if (t instanceof InvalidEndBalanceException ex) {
            LOGGER.error("Invalid end balance for item with reference {}: start {} - mutation {} - end {} - calculated {}",
                    item.getReference(), item.getStartBalance(), item.getMutation(), item.getEndBalance(), ex.getCalculatedBalance());
            persistInvalidRecord(item, t.getMessage());
        }
    }

    private void persistInvalidRecord(Record item, String reason) {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(REFERENCE, item.getReference());
        parameters.put(ACCOUNT_NUMBER, item.getAccountNumber());
        parameters.put(DESCRIPTION, item.getDescription());
        parameters.put(START_BALANCE, item.getStartBalance());
        parameters.put(MUTATION, item.getMutation());
        parameters.put(END_BALANCE, item.getEndBalance());
        parameters.put(REASON, reason);
        jdbcInsert.execute(parameters);
    }
}
