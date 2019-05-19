package com.user.mngmnt.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.repository.GenericRepository;

@Repository
public class GenericRepositoryImpl<T> implements GenericRepository<T> {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<T> findAllWithCriteria(String criteriaJson, Class<T> c, PageRequest pageRequest) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(c);
		Root<T> root = criteriaQuery.from(c);
		JqgridFilter jqgridFilter = JqgridObjectMapper.map(criteriaJson);
		Predicate[] predicates = new Predicate[jqgridFilter.getRules().size()];

		for (int i = 0; i < jqgridFilter.getRules().size(); i++) {
			String column = jqgridFilter.getRules().get(i).getField();
			predicates[i] = column.equals("id")
					? builder.equal(builder.lower(root.get(jqgridFilter.getRules().get(i).getField())),
							Long.valueOf(jqgridFilter.getRules().get(i).getData()))
					: builder.like(builder.lower(root.get(jqgridFilter.getRules().get(i).getField())),
							"%" + jqgridFilter.getRules().get(i).getData() + "%");
		}
		criteriaQuery.where(builder.and(predicates));

		return entityManager.createQuery(criteriaQuery)
				.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
				.setMaxResults(pageRequest.getPageSize()).getResultList();
	}
}
