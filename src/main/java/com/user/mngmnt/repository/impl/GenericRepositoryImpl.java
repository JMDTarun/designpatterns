package com.user.mngmnt.repository.impl;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.el.util.ReflectionUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.model.ResportSearchCriteria;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.utils.ReflectionUtils;

@Repository
public class GenericRepositoryImpl<T> implements GenericRepository<T> {

	@PersistenceContext
	private EntityManager entityManager;

	private Map<String, String> fieldsMap = new HashMap<>();

	{
		fieldsMap.put("area.id", "area.name");
		fieldsMap.put("subArea.id", "subArea.wardNumber");
		fieldsMap.put("street.id", "street.streetNumber");
		fieldsMap.put("network.id", "network.name");
		fieldsMap.put("channel.id", "channel.name");
		fieldsMap.put("areaId", "area.id");
		fieldsMap.put("subAreaId", "subArea.id");
		fieldsMap.put("streetId", "street.id");
		fieldsMap.put("customerStatus", "customerSetTopBoxes.customerSetTopBoxStatus");
		fieldsMap.put("monthlyCharge", "customerSetTopBoxes.packPrice");
	}

	@Override
	public List<T> findAllWithCriteria(String criteriaJson, Class<T> c, PageRequest pageRequest) throws ParseException {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(c);
		Root<T> root = criteriaQuery.from(c);
		JqgridFilter jqgridFilter = JqgridObjectMapper.map(criteriaJson);
		Predicate[] predicates = new Predicate[jqgridFilter.getRules().size()];

		for (int i = 0; i < jqgridFilter.getRules().size(); i++) {
			String field = jqgridFilter.getRules().get(i).getField();
			Class<?> propertyClass = ReflectionUtils.getPropertyClass(c,
					field.contains(".") ? fieldsMap.get(field) : field);
			boolean isNumber = false;
			if (Number.class.isAssignableFrom(propertyClass)) {
				isNumber = true;
			}

			if (field.contains(".")) {
				field = fieldsMap.get(field);
				predicates[i] = isNumber
						? builder.like(
								root.join(field.substring(0, field.indexOf(".")))
										.get(field.substring(field.indexOf(".") + 1, field.length())),
								jqgridFilter.getRules().get(i).getData())
						: builder.like(
								root.join(field.substring(0, field.indexOf(".")))
										.get(field.substring(field.indexOf(".") + 1, field.length())),
								"%" + jqgridFilter.getRules().get(i).getData().toLowerCase() + "%");
			} else {
				predicates[i] = isNumber
						? builder.equal(root.get(jqgridFilter.getRules().get(i).getField()),
								NumberFormat.getInstance().parse(jqgridFilter.getRules().get(i).getData()))
						: builder.like(builder.lower(root.get(jqgridFilter.getRules().get(i).getField())),
								"%" + jqgridFilter.getRules().get(i).getData().toLowerCase() + "%");
			}

		}
		criteriaQuery.where(builder.and(predicates));
		return entityManager.createQuery(criteriaQuery)
				.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
				.setMaxResults(pageRequest.getPageSize()).getResultList();
	}

	@Override
	public List<T> findAllWithCriteria(ResportSearchCriteria resportSearchCriteria, Class<T> c, PageRequest pageRequest)
			throws ParseException, NoSuchFieldException {
		CriteriaQuery<T> criteriaQuery = getFilterCriteria(resportSearchCriteria, c);
		if(pageRequest == null) {
			return entityManager.createQuery(criteriaQuery).getResultList();
		} 
		return entityManager.createQuery(criteriaQuery)
				.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
				.setMaxResults(pageRequest.getPageSize()).getResultList();
	}

	@Override
	public Integer findCountWithCriteria(ResportSearchCriteria resportSearchCriteria, Class<T> c)
			throws ParseException, NoSuchFieldException {

		CriteriaQuery<T> criteriaQuery = getFilterCriteria(resportSearchCriteria, c);
		return entityManager.createQuery(criteriaQuery).getResultList().size();
	}
	
	private CriteriaQuery<T> getFilterCriteria(ResportSearchCriteria resportSearchCriteria, Class<T> c)
			throws NoSuchFieldException, ParseException {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(c);
		Root<T> root = criteriaQuery.from(c);
		List<Field> fields = ReflectionUtils.getPrivateFields(ResportSearchCriteria.class);
		// Predicate[] predicates = new Predicate[jqgridFilter.getRules().size()];
		List<Predicate> predicatesList = new ArrayList<>();
		if (resportSearchCriteria != null) {
			for (int i = 0; i < fields.size(); i++) {
				String field = fields.get(i).getName();
				String fieldValue = (field == null) ? null
						: String.valueOf(ReflectionUtils.getPropertyValue(resportSearchCriteria, field));

				if (field != null && !"null".equals(fieldValue) && fieldValue != null) {
					field = fieldsMap.get(fields.get(i).getName());
					Class<?> propertyClass = ReflectionUtils.getPropertyClass(c, field);

					boolean isNumber = false;
					boolean isEnum = false;
					if (Number.class.isAssignableFrom(propertyClass)) {
						isNumber = true;
					} else if (propertyClass instanceof Class && ((Class<?>) propertyClass).isEnum()) {
						isEnum = true;
					}

					if (field.contains(".")) {
						predicatesList.add(isNumber || isEnum
								? builder.equal(
										root.join(field.substring(0, field.indexOf(".")))
												.get(field.substring(field.indexOf(".") + 1, field.length())),
										isEnum ? CustomerSetTopBoxStatus.valueOf(fieldValue) : fieldValue)
								: builder.like(
										root.join(field.substring(0, field.indexOf(".")))
												.get(field.substring(field.indexOf(".") + 1, field.length())),
										"%" + fieldValue.toLowerCase() + "%"));
					} else {
						predicatesList.add(isNumber || isEnum
								? builder.equal(root.get(field),
										isEnum ? CustomerSetTopBoxStatus.valueOf(fieldValue)
												: NumberFormat.getInstance().parse(fieldValue))
								: builder.like(builder.lower(root.get(field)), "%" + fieldValue.toLowerCase() + "%"));
					}
				}
			}
		}
		Predicate[] predicates = predicatesList.stream().toArray(Predicate[]::new);
		criteriaQuery.where(builder.and(predicates));
		return criteriaQuery;
	}

}
