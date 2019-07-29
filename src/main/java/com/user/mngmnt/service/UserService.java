package com.user.mngmnt.service;

import com.user.mngmnt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User findUserByEmail(String email);

    User saveUser(User user);

    Boolean removeAll();

    void removeById(Long id);

    User findById(Long id);

    Page<User> listUsers(Pageable pageable);

}
