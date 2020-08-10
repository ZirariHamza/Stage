package com.java.project.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.java.project.batch.ChunkCountListener;
import com.java.project.model.employee;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	private int counter = 0;
	private String re1 = "^[a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*$";
	private String re2 = "^(\\d*),[a-zA-Z]*,(\\d*),(\\d*)$";
	private String line = "";
	private Vector<String> V = new Vector<>();
	@Bean
	public ChunkCountListener listener() {
		return new ChunkCountListener();
	}
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory,
			ItemReader<employee> itemReader,
			ItemProcessor<employee, employee> itemProcessor,
			ItemWriter<employee> itemWriter
			) {
		
		Step step = stepBuilderFactory.get("ETL-file-load")
				.<employee, employee>chunk(100)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.listener(listener())
				.build();
		
		
		return jobBuilderFactory.get("ETL-Load")
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}
	
	
	@Bean
	public boolean verify() {
		boolean result = true;
		Pattern pt1 = Pattern.compile(re1);
		Pattern pt2 = Pattern.compile(re2);
		Matcher mt = pt1.matcher(V.firstElement());
		for (int i = 1; i < V.size() ; i++) {
			Matcher mt1 = pt2.matcher(V.get(i));
			if(mt1.matches() != true) {
				result = false;
			}
		}
		System.out.println(result);
		return result;
	}
	
	@Bean
	public FlatFileItemReader<employee> fileItemReader(@Value("employee.txt") Resource resource) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/employee.txt"));
			while((line = br.readLine()) != null) {
				V.add(line);
				counter++;
			}
			for (String string : V) {
				System.out.println(string);
			}
			if(verify() == true) {
				System.out.println("Matching sequence...");
				FlatFileItemReader<employee> flatFileItemReader = new FlatFileItemReader<>();
				flatFileItemReader.setResource(resource);
				flatFileItemReader.setName("TXT-Reader");
				flatFileItemReader.setLinesToSkip(1);
				flatFileItemReader.setLineMapper(lineMapper());
				System.out.println("Nombre de lignes insérés : " + (counter - 1) );
				return flatFileItemReader;
			}
			else {
				System.out.println("Not matching sequence...");
				return null;
			}
		} catch (Exception e) {
			System.out.println("Error here");
			return null;
		}
		
	}
	
	@Bean
	public LineMapper<employee> lineMapper() {
		
		DefaultLineMapper<employee> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] {"id", "name", "dept", "salary"});
		
		BeanWrapperFieldSetMapper<employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(employee.class);
		
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		return defaultLineMapper;
	}
}
