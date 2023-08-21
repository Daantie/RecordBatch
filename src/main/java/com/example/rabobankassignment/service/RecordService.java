package com.example.rabobankassignment.service;

import java.util.List;
import javax.sql.DataSource;

import com.example.rabobankassignment.model.RecordDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import static com.example.rabobankassignment.util.Constants.*;

@Service
public class RecordService {
    private final JdbcTemplate jdbcTemplate;

    public RecordService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<RecordDTO> getInvalidRecords() {
        RowMapper<RecordDTO> rowMapper = (rs, rowNum) -> new RecordDTO(
                rs.getLong(REFERENCE),
                rs.getString(ACCOUNT_NUMBER),
                rs.getString(DESCRIPTION),
                rs.getBigDecimal(START_BALANCE),
                rs.getBigDecimal(MUTATION),
                rs.getBigDecimal(END_BALANCE),
                rs.getString(REASON)
        );
        return jdbcTemplate.query("SELECT * FROM `invalid_record`", rowMapper);
    }
}
