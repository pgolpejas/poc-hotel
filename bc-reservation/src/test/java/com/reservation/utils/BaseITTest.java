package com.reservation.utils;

import com.outbox.configuration.JDBCTransactionalOutboxAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@SqlMergeMode(MergeMode.MERGE)
@Sql(scripts = "/sql/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Import({JDBCTransactionalOutboxAutoConfiguration.class})
@SuppressWarnings("java:S2187")
public class BaseITTest {

}
