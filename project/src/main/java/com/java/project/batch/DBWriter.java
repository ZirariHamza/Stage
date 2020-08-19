package com.java.project.batch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.java.project.model.Logs;
import com.java.project.model.employee;
import com.java.project.repository.LogRepository;
import com.java.project.repository.UserRepository;

@Component
public class DBWriter implements ItemWriter<employee>{

	private int counter = 0;
	private String re1 = "^[a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*$";
	private String re2 = "^(\\d*),[a-zA-Z]*,(\\d*),(\\d*)$";
	private String line = "";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LogRepository logRepository;
	
	@Override
	public void write(List<? extends employee> employees) {
		try{
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/employee.txt"));

			Pattern pt1 = Pattern.compile(re1);
			Pattern pt2 = Pattern.compile(re2);
		while((line = br.readLine()) != null) {
			Matcher mt1 = pt1.matcher(line);
			Matcher mt2 = pt2.matcher(line);
			if(mt1.matches() == true){
				System.out.println("Header structure matched");
			}else if(mt2.matches() == true) {
				String[] part = line.split(",");
				employee e = new employee(Integer.parseInt(part[0]), part[1], part[2], Integer.parseInt(part[3]));
				userRepository.save(e);
				System.out.println("employee : " + e);
			}
		}
		}catch (Exception e) {
			System.out.println("Error : " + e);
		}
	}
	
	public void update(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("ItemCount: "+executionContext.get("FlatFileItemReader.read.count"));
    }
}
