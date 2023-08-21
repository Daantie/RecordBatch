package com.example.rabobankassignment.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.rabobankassignment.exception.InvalidEndBalanceException;
import com.example.rabobankassignment.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import static com.example.rabobankassignment.util.Constants.*;

public class RecordItemProcessor implements ItemProcessor<Record, Record> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordItemProcessor.class);
    private final Set<Long> seenReferences = new HashSet<>();
    private final SimpleJdbcInsert simpleJdbcInsert;

    public RecordItemProcessor(SimpleJdbcInsert simpleJdbcInsert) {
        this.simpleJdbcInsert = simpleJdbcInsert;
    }

    @Override
    public Record process(Record item) {
        validateEndBalance(item);

        if (seenReferences.contains(item.getReference()) && !item.isProcessed()) {
            LOGGER.error("Duplicate reference {}", item.getReference());
            persistInvalidRecord(item);
            return null;
        }
        seenReferences.add(item.getReference());
        item.setProcessed(true);
        return item;
    }

    private void validateEndBalance(Record item) {
        BigDecimal calculatedEndBalance = item.getStartBalance().add(item.getMutation()).setScale(2, RoundingMode.HALF_DOWN);
        if (item.getEndBalance().compareTo(calculatedEndBalance) != 0) {
            throw new InvalidEndBalanceException("Invalid end balance. Should be: " + calculatedEndBalance, calculatedEndBalance);
        }
    }

    private void persistInvalidRecord(Record item) {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(REFERENCE, item.getReference());
        parameters.put(ACCOUNT_NUMBER, item.getAccountNumber());
        parameters.put(DESCRIPTION, item.getDescription());
        parameters.put(START_BALANCE, item.getStartBalance());
        parameters.put(MUTATION, item.getMutation());
        parameters.put(END_BALANCE, item.getEndBalance());
        parameters.put(REASON, "Duplicate reference");
        simpleJdbcInsert.execute(parameters);
    }
}
