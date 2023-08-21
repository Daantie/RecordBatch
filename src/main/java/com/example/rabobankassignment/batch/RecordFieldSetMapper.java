package com.example.rabobankassignment.batch;

import java.math.RoundingMode;

import com.example.rabobankassignment.model.Record;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import static com.example.rabobankassignment.util.Constants.*;

public class RecordFieldSetMapper implements FieldSetMapper<Record> {

    public Record mapFieldSet(FieldSet fieldSet) {
        Record newRecord = new Record();
        newRecord.setReference(fieldSet.readLong(REFERENCE));
        newRecord.setAccountNumber(fieldSet.readString(ACCOUNT_NUMBER));
        newRecord.setDescription(fieldSet.readString(DESCRIPTION));
        newRecord.setStartBalance(fieldSet.readBigDecimal(START_BALANCE).setScale(2, RoundingMode.HALF_DOWN));
        newRecord.setMutation(fieldSet.readBigDecimal(MUTATION).setScale(2, RoundingMode.HALF_DOWN));
        newRecord.setEndBalance(fieldSet.readBigDecimal(END_BALANCE).setScale(2, RoundingMode.HALF_DOWN));

        return newRecord;
    }

}
