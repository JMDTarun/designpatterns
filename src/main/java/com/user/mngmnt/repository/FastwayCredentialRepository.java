package com.user.mngmnt.repository;

import com.user.mngmnt.model.Area;
import com.user.mngmnt.model.FastwayCredentials;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FastwayCredentialRepository extends JpaRepository<FastwayCredentials, Long> {

}
