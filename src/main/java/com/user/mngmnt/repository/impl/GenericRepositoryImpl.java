package com.user.mngmnt.repository.impl;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
		fieldsMap.put("packId", "customerSetTopBoxes.pack.id");
		fieldsMap.put("packPrice", "customerSetTopBoxes.packPrice");
		fieldsMap.put("assignedSetTopBoxes", "customerSetTopBoxes");
		fieldsMap.put("customerId", "customer.id");
		fieldsMap.put("packId", "customerSetTopBoxes.pack.id");
		fieldsMap.put("packPrice", "customerSetTopBoxes.packPrice");
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
		if (pageRequest == null) {
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
		List<Predicate> predicatesList = new ArrayList<>();
		if (resportSearchCriteria != null) {
			for (int i = 0; i < fields.size(); i++) {
				String field = fields.get(i).getName();
				String fieldValue = (field == null) ? null
						: String.valueOf(ReflectionUtils.getPropertyValue(resportSearchCriteria, field));
				if (field != null && !"null".equals(fieldValue) && fieldValue != null) {

					if (field.equalsIgnoreCase("assignedSetTopBoxes")) {
						field = fieldsMap.get(field);
						if(field != null) {
							int setTopBoxesAssigned = Integer.parseInt(fieldValue);
							if (setTopBoxesAssigned > 0) {
								predicatesList.add(builder.ge(builder.size(root.get(field)), Integer.parseInt(fieldValue)));
							} else {
								predicatesList.add(builder.le(builder.size(root.get(field)), Integer.parseInt(fieldValue)));
							}
						}
						continue;
					}
					field = fieldsMap.get(fields.get(i).getName());
					if (field != null) {
						Class<?> propertyClass = ReflectionUtils.getPropertyClass(c, field);
						boolean isNumber = false;
						boolean isEnum = false;
						if (Number.class.isAssignableFrom(propertyClass)) {
							isNumber = true;
						} else if (propertyClass instanceof Class && ((Class<?>) propertyClass).isEnum()) {
							isEnum = true;
						}
						if (field.contains(".")) {
							Join<Object, Object> join = null;
							String fieldNames[] = field.split("\\.");
							for (int j = 0; j < fieldNames.length - 1; j++) {
								if (j == 0) {
									join = root.join(fieldNames[j]);
								} else {
									join = join.join(fieldNames[j]);
								}
							}
							predicatesList.add(isNumber || isEnum
									? builder.equal(join.get(fieldNames[fieldNames.length - 1]),
											isEnum ? CustomerSetTopBoxStatus.valueOf(fieldValue) : fieldValue)
									: builder.like(join.get(fieldNames[fieldNames.length - 1]),
											"%" + fieldValue.toLowerCase() + "%"));
						} else {
							predicatesList.add(isNumber || isEnum
									? builder.equal(root.get(field),
											isEnum ? CustomerSetTopBoxStatus.valueOf(fieldValue)
													: NumberFormat.getInstance().parse(fieldValue))
									: builder.like(builder.lower(root.get(field)),
											"%" + fieldValue.toLowerCase() + "%"));
						}
					}
				}
			}
		}
		addAdditionalCriteria(predicatesList, resportSearchCriteria, builder, root);
		Predicate[] predicates = predicatesList.stream().toArray(Predicate[]::new);
		criteriaQuery.where(builder.and(predicates)).groupBy(root.get("id"));
		return criteriaQuery;
	}
	
	private void addAdditionalCriteria(List<Predicate> predicates, ResportSearchCriteria resportSearchCriteria, CriteriaBuilder builder, Root<T> root) {
	    if(resportSearchCriteria != null) {
            if (resportSearchCriteria.getIsGreaterThenZero() != null) {
                if (resportSearchCriteria.getIsGreaterThenZero()) {
                    predicates.add(builder.ge(root.get("balance"), 0.0));
                } else {
                    predicates.add(builder.lt(root.get("balance"), 0.0));
                }
            }
            if (resportSearchCriteria.getRangeStart() != null) {
                predicates.add(builder.ge(root.get("balance"),resportSearchCriteria.getRangeStart()));
            }
            if (resportSearchCriteria.getRangeEnd() != null) {
                predicates.add(builder.lt(root.get("balance"), resportSearchCriteria.getRangeEnd()));
            }
            if (resportSearchCriteria.getPaymentDayStart() != null) {
                predicates.add(builder.ge(root.join("customerSetTopBoxes").get("billingDay"), resportSearchCriteria.getPaymentDayStart()));
            }
            if (resportSearchCriteria.getPaymentDayEnd() != null) {
                predicates.add(builder.lt(root.join("customerSetTopBoxes").get("billingDay"), resportSearchCriteria.getPaymentDayEnd()));
            }
	    }
    }
	
	@SuppressWarnings("unchecked")
    @Override
    public List<T> findAllWithSqlQuery(String sql, Class<T> c, List<Object> parameters, PageRequest pageRequest) {
        Query nativeQuery = entityManager.createNativeQuery(sql, c);
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                nativeQuery.setParameter(i + 1, parameters.get(i));
            }
        }

        if (pageRequest == null) {
            return nativeQuery.getResultList();
        }
        return nativeQuery.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .setMaxResults(pageRequest.getPageSize()).getResultList();
    }

    @Override
    public Integer findCountWithSqlQuery(String sql, List<Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(sql);
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                nativeQuery.setParameter(i + 1, parameters.get(i));
            }
        }
        return ((BigInteger) nativeQuery.getSingleResult()).intValue();
    }
}
