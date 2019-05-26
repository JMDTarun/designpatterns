package com.user.mngmnt.repository;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface GenericRepository<T> {

	List<T> findAllWithCriteria(String criteriaJson, Class<T> c, PageRequest pageRequest) throws ParseException;
	
}
