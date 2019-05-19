package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.SubArea;

@Repository
public interface SubAreaRepository extends JpaRepository<SubArea, Long> {

    SubArea findByWardNumber(String wardNumber);

    SubArea findByWardNumber2(String wardNumber2);

	Page<SubArea> findByIdLike(String string, Pageable pageable);

	Page<SubArea> findByWardNumberLike(String string, Pageable pageable);
}
