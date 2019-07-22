package com.user.mngmnt.repository;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.user.mngmnt.model.ResportSearchCriteria;

public interface GenericRepository<T> {

	List<T> findAllWithCriteria(String criteriaJson, Class<T> c, PageRequest pageRequest) throws ParseException;

	List<T> findAllWithCriteria(ResportSearchCriteria resportSearchCriteria, Class<T> c, PageRequest pageRequest)
			throws ParseException, NoSuchFieldException;

	Integer findCountWithCriteria(ResportSearchCriteria resportSearchCriteria, Class<T> c)
			throws ParseException, NoSuchFieldException;
	
	List<T> findAllWithSqlQuery(String sql, Class<T> c, List<Object> parameters, PageRequest pageRequest);
	
	Integer findCountWithSqlQuery(String sql, List<Object> parameters);
	
}
