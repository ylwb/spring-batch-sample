package com.auto1.spring.batch.sample;

import com.auto1.spring.batch.sample.config.SpringBatchExecutor;
import com.auto1.spring.batch.sample.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Slf4j
@SpringBootTest
@Sql(scripts = "/sql/clear.sql")
class SpringBatchSampleApplicationIntegrationTest {

	@Autowired
	private SpringBatchExecutor springBatchExecutor;

	@Autowired
	private BookRepository bookRepository;

	@Test
	public void testExecution() {
		long initialCount = bookRepository.count();
		assertThat(initialCount, equalTo(0L));

		springBatchExecutor.execute("sample-data.csv");

		long count = bookRepository.count();
		assertThat(count, equalTo(7L));
	}

	@Test
	public void testLargeData() {
		long startTime = System.currentTimeMillis();

		long initialCount = bookRepository.count();
		assertThat(initialCount, equalTo(0L));

		springBatchExecutor.execute("large-data.csv");

		long count = bookRepository.count();
		assertThat(count, equalTo(60000L));

		long endTime = System.currentTimeMillis();

		log.info("executed in miles: {}", endTime - startTime);
	}

}
