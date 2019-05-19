package com.user.mngmnt.service;

import com.user.mngmnt.model.Area;

public interface AreaService {

    void saveArea(Area area);

    void removeById(Long id);

    Area findById(Long id);

}
