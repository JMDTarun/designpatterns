package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long>{
    Channel findByName(String name);

    Page<Channel> findByIdLike(String string, PageRequest pageRequest);

    Page<Channel> findByNameLike(String string, PageRequest pageRequest);
}
