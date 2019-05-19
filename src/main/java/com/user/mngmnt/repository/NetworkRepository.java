package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Network;

@Repository
public interface NetworkRepository extends JpaRepository<Network, Long> {
    Network findByName(String name);

    Page<Network> findByIdLike(String string, Pageable pageable);

    Page<Network> findByNameLike(String string, Pageable pageable);
    
}
