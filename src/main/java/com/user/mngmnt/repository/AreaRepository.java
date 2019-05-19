package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    
    Area findByName(String name);

    Page<Area> findByIdLike(String string, Pageable pageable);

    Page<Area> findByNameLike(String string, Pageable pageable);

}
