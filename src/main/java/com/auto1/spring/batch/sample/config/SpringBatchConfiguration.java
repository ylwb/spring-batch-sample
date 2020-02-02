package com.auto1.spring.batch.sample.config;

import com.auto1.spring.batch.sample.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public FlatFileItemReader<Book> bookReader(@Value("#{jobParameters['filePath']}") final String filePath) {
        return new FlatFileItemReaderBuilder<Book>()
                .name("BookItemReader")
                .resource(new ClassPathResource(filePath))
                .delimited()
                .names(new String[]{"title", "description", "author"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {{
                    setTargetType(Book.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Book> bookWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Book>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO books (title, description, author_full_name) VALUES (:title, :description, :author)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step1(final ItemWriter<Book> writer, final ItemReader<Book> reader) {
        return stepBuilderFactory.get("step1")
                .<Book, Book> chunk(100)
                .reader(reader)
                .processor((ItemProcessor<Book, Book>) book -> book)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importUserJob(final Step step1) {
        return jobBuilderFactory.get("bookReaderJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

}
