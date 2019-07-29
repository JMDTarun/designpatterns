package com.user.mngmnt.repository;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.user.mngmnt.model.ReportSearchCriteria;

public interface GenericRepository<T> {

	List<T> findAllWithCriteria(String criteriaJson, Class<T> c, PageRequest pageRequest) throws ParseException;

	List<T> findAllWithCriteria(ReportSearchCriteria resportSearchCriteria, Class<T> c, PageRequest pageRequest)
			throws ParseException, NoSuchFieldException;

	Integer findCountWithCriteria(ReportSearchCriteria resportSearchCriteria, Class<T> c)
			throws ParseException, NoSuchFieldException;
	
	List<T> findAllWithSqlQuery(String sql, Class<T> c, List<Object> parameters, PageRequest pageRequest);
	
	Integer findCountWithSqlQuery(String sql, List<Object> parameters);
	
}
