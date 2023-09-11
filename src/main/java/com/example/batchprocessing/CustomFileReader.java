package com.example.batchprocessing;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CustomFileReader {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	static String path = "src/main/resources/sample-data.csv";
	
	@Bean
	public FlatFileItemReader<Person> readerFile() {
		return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			.resource(new FileSystemResource(path))
			.delimited()
			.names(new String[]{"firstName", "lastName"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}
}