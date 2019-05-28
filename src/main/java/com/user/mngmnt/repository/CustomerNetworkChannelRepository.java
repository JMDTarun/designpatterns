package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerNetworkChannel;

@Repository
public interface CustomerNetworkChannelRepository extends JpaRepository<CustomerNetworkChannel, Long> {

}
