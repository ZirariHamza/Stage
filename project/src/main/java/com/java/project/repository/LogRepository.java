package com.java.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.project.model.Logs;

@Repository
public interface LogRepository extends JpaRepository<Logs, Integer>{

}
