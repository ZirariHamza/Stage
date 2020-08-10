package com.java.project.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.java.project.model.employee;

@Component
public class Processor implements ItemProcessor<employee, employee>{
	
	@Override
	public employee process(employee employee) throws Exception {
		
		return employee;
	}

}
