package com.user.mngmnt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.mngmnt.model.Area;
import com.user.mngmnt.repository.AreaRepository;

@Service("areaService")
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Override
    public void saveArea(Area area) {
        areaRepository.save(area);
    }

    @Override
    public void removeById(Long id) {
        areaRepository.deleteById(id);
    }

    @Override
    public Area findById(Long id) {
        return areaRepository.findById(id).get();
    }

}
