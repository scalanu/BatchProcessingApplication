package com.example.batchprocessing;

import java.net.ConnectException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	String basePath = "src/main/resources/";
	
	@Autowired
    private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
    private StepBuilderFactory stepBuilderFactory;
	
	/*@Autowired
	private CustomFileReader fileReader;*/

	// -- Readers --
	//Reader step required to read from file (csv,json, etc)
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>()
				.name("personItemReader")
				.resource(new FileSystemResource(basePath + "sample-data.csv"))
				.delimited()
				.names(new String[] { "firstName", "lastName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				}).build();
	}
	 
	
	
	//Reader step required to read data from database through JDBC 
	@Bean
	public JdbcCursorItemReader<Person> jdbcReader() {
		return new JdbcCursorItemReaderBuilder<Person>()
				.name("jdbcReader")
				.dataSource(dataSource)
				.sql("SELECT first_name, last_name FROM people")
				.rowMapper(new CustomePersonMapper())
				.build();
	}

	// -- Processors --
	// Processor to process data the of the job step
	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}
	
	@Bean
	public PersonItemProcessor2 processor2() {
		return new PersonItemProcessor2();
	}

	
	// -- Writers --
	// Writer step required to write to database with JDBC
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
			.dataSource(dataSource)
			.build();
	}
	
	
	// Writer step required to write to flat file (csv, json, etc)
	@Bean
	public FlatFileItemWriter<Person> fileWriter() {
		return new FlatFileItemWriterBuilder<Person>()
				.name("fileWriter")
				//.resource( new PathResource(basePath +  "sample-data-out.csv"))
				.resource(new FileSystemResource(basePath +  "sample-data-out.csv"))
				.append(false)
				.lineAggregator(createPersonLineAggregator())
				.build();
	}
	

	
	// -- Jobs -- 
	// Job to import file data and write to database
	@Primary
	@Bean
	public Job importUserJob(
			JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory
			.get("importUserJob")
			.incrementer(new RunIdIncrementer()) // capture the chain of events
			.listener(listener) // Listener to capture event hook to test file data
			.flow(step1) // Flow steps capture 
			.end()
			.build();
	}
	
	
	// Job to import database data and write to file
	@Bean
	public Job exportUserJob(
			JobCompletionNotificationListener2 listener1, Step step2) {
		return jobBuilderFactory
			.get("exportUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener1) // Listener to capture event hook to test database data
			.flow(step2)  // Flow steps capture 
			.end()
			.build();
	}

	// -- Steps --
	// Steps to capture flow of unit of job from reading data from file, process it and write to database
	@Bean
	public Step step1(JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory
			.get("step1")
			.<Person, Person> chunk(50)
			//.reader(fileReader.readerFile()) // use reader in a separate package if required
			.reader(reader())
			.processor(processor())
			.writer(writer)
			.faultTolerant()
			.skip(FlatFileParseException.class) // Put the exceptions to be skipped
			.skipLimit(5)
			.retry(ConnectException.class) // Put the exception for retry required
			.retryLimit(3)
			.listener(new CustomRetryListener())
			.build();
	}
	
	// Steps to capture flow of unit of job from reading data from database, process it and write to file
	@Bean
	public Step step2(FlatFileItemWriter<Person> fileWriter) {
		return stepBuilderFactory
			.get("step2")
			.<Person, Person> chunk(50)
			.reader(jdbcReader())
			.processor(processor2())
			.writer(fileWriter())
			.faultTolerant()
			.skip(FlatFileParseException.class) // Put the exceptions to be skipped
			.skipLimit(5)
			.retry(ConnectException.class) // Put the exception for retry required
			.retryLimit(3)
			.build();
	}
	// end::jobstep[]
	
	// Aggregating data of a record in a line
	private LineAggregator<Person> createPersonLineAggregator() {
        DelimitedLineAggregator<Person> lineAggregator
                = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
 
        FieldExtractor<Person> fieldExtractor
                = createPersonFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);
 
        return lineAggregator;
    }
 
	// Extracting the fields required for line aggregation
    private FieldExtractor<Person> createPersonFieldExtractor() {
        BeanWrapperFieldExtractor<Person> extractor = 
                new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {
                "firstName",
                "lastName",
         });
        return extractor;
    }
}
