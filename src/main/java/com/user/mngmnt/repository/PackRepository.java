package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {

	Page<Pack> findByIdLike(String string, Pageable pageRequest);

	Page<Pack> findByNameLike(String string, Pageable pageRequest);

	Pack findByName(String name);

	@Query("SELECT p.networkChannels FROM Pack p WHERE p.id = :id")
	Page<NetworkChannel> getNetworkChannelsByPackId(@Param("id") long id, Pageable pageRequest);

	@Query("SELECT c FROM Pack p INNER JOIN p.networkChannels c WHERE c.id = :id")
	NetworkChannel getNetworkChannelById(@Param("id") long id);

}
