package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.NetworkChannel;

@Repository
public interface NetworkChannelRepository
        extends JpaRepository<NetworkChannel, Long> {
    NetworkChannel findByName(String name);

    Page<NetworkChannel> findByIdLike(String string, PageRequest pageRequest);

    Page<NetworkChannel> findByNameLike(String string, PageRequest pageRequest);
}
