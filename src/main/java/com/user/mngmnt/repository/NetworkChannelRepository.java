package com.user.mngmnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Channel;
import com.user.mngmnt.model.NetworkChannel;

@Repository
public interface NetworkChannelRepository
        extends JpaRepository<NetworkChannel, Long> {
	
    NetworkChannel findByName(String name);

    Page<NetworkChannel> findByIdLike(String string, PageRequest pageRequest);

    Page<NetworkChannel> findByNameLike(String string, PageRequest pageRequest);
    
    @Query(value = "SELECT c FROM NetworkChannel nc inner join nc.network n inner join nc.channel c where n.id = :networkId")
	List<Channel> getChannels(@Param("networkId") long networkId);

    @Query(value = "SELECT distinct nc FROM NetworkChannel nc inner join nc.network n inner join nc.channel c where n.id = :networkId")
	List<NetworkChannel> getNetworkChannelsByNetworkId(@Param("networkId") long channelId);
    
    @Query(value = "SELECT distinct nc FROM NetworkChannel nc inner join nc.network n inner join nc.channel c where c.id = :channelId")
	List<NetworkChannel> getNetworkChannelsByChannelId(@Param("channelId") long channelId);
    
    @Query(value = "SELECT distinct nc FROM NetworkChannel nc join nc.network n join nc.channel c where c.id = :channelId and n.id = :networkId")
    List<NetworkChannel> getNetworkChannels(@Param("channelId") long channelId, @Param("networkId") long networkId);
    
}
