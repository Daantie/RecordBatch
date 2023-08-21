package com.example.rabobankassignment.batch;

import java.math.BigDecimal;

import com.example.rabobankassignment.exception.InvalidEndBalanceException;
import com.example.rabobankassignment.model.Record;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

class RecordItemProcessorTest {
    @Mock
    SimpleJdbcInsert simpleJdbcInsert;

    private final RecordItemProcessor recordItemProcessor = new RecordItemProcessor(simpleJdbcInsert);

    @Test
    void testInvalidAddedBalance() {
        Record item = new Record();
        item.setStartBalance(BigDecimal.valueOf(29.82));
        item.setMutation(BigDecimal.valueOf(0.19));
        item.setEndBalance(BigDecimal.valueOf(30.00000));

        InvalidEndBalanceException thrown =
                Assertions.assertThrows(InvalidEndBalanceException.class, () -> recordItemProcessor.process(item));

        Assertions.assertEquals("Invalid end balance. Should be: 30.01", thrown.getMessage());
    }

    @Test
    void testInvalidSubtractedBalance() {
        Record item = new Record();
        item.setStartBalance(BigDecimal.valueOf(29.82));
        item.setMutation(BigDecimal.valueOf(-30));
        item.setEndBalance(BigDecimal.valueOf(-0.1));

        InvalidEndBalanceException thrown =
                Assertions.assertThrows(InvalidEndBalanceException.class, () -> recordItemProcessor.process(item));

        Assertions.assertEquals("Invalid end balance. Should be: -0.18", thrown.getMessage());
    }

    @Test
    void testValidBalance() {
        Record item = new Record();
        item.setStartBalance(BigDecimal.valueOf(29.82));
        item.setMutation(BigDecimal.valueOf(0.18));
        item.setEndBalance(BigDecimal.valueOf(30));

        Assertions.assertDoesNotThrow(() -> recordItemProcessor.process(item));
    }

}
