package com.user.mngmnt.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.ResportSearchCriteria;
import com.user.mngmnt.repository.ReportsRepository;

@Repository
public class ReportsRepositoryImpl implements ReportsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<CustomerLedgre> findAllWithPartialPayment(ResportSearchCriteria resportSearchCriteria, Pageable pageable) {
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerLedgre> criteriaQuery = builder.createQuery(CustomerLedgre.class);
        Root<CustomerLedgre> customerLedgreRoot = criteriaQuery.from(CustomerLedgre.class);
        
        Subquery<Double> customerLedgreSubquery = criteriaQuery.subquery(Double.class);
        Root<CustomerLedgre> subQueryRoot = customerLedgreSubquery.from(CustomerLedgre.class);

        List<Predicate> predicatesList = new ArrayList<>();
        if(resportSearchCriteria.getMonth() != null) {
            predicatesList.add(builder.equal(customerLedgreRoot.get("month"), resportSearchCriteria.getMonth()));
        }
        if(resportSearchCriteria.getStart() != null && resportSearchCriteria.getEnd() != null) {
            predicatesList.add(builder.greaterThanOrEqualTo(customerLedgreRoot.get("createdAt"), resportSearchCriteria.getStart()));
            predicatesList.add(builder.lessThanOrEqualTo(customerLedgreRoot.get("createdAt"), resportSearchCriteria.getStart()));
        }
        
        Predicate[] predicates = predicatesList.stream().toArray(Predicate[]::new);
        
        customerLedgreSubquery.select(builder.sum(subQueryRoot.get("amountCredit"))).where(builder.and(predicates)).groupBy(subQueryRoot.get("customer"));

        Expression<Double> amountCreditExpression = customerLedgreRoot.join("customer").get("balance");
        criteriaQuery.where(builder.ge(amountCreditExpression, customerLedgreSubquery));
        if(pageable != null) {
            return entityManager.createQuery(criteriaQuery)
                    .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                    .setMaxResults(pageable.getPageSize()).getResultList();
        }
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
