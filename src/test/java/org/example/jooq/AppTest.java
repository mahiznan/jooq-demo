package org.example.jooq;

import org.example.sources.tables.Transactions;
import org.example.sources.tables.records.TransactionsRecord;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger("AppTest");

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void getTransactionById() {
        DataSource dataSource = DatasourceConfig.createDataSource();
        TransactionsRecord transactionsRecord = DSL.using(dataSource, SQLDialect.MYSQL)
                .selectFrom(Transactions.TRANSACTIONS)
                .where(Transactions.TRANSACTIONS.ID.eq(1L))
                .fetchAny();
        logger.info(transactionsRecord.getAccount());
        logger.error("Didn't do it.");

    }

}
