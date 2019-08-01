package com.user.mngmnt;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.user.mngmnt.model.CustomerCode;
import com.user.mngmnt.model.RoleNames;
import com.user.mngmnt.model.User;
import com.user.mngmnt.repository.CustomerCodeRepository;
import com.user.mngmnt.service.UserService;

@Component
public class InitialSetup {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerCodeRepository customerCodeRepository;

    @Value("${admin.first.name}")
    private String firstName;

    @Value("${admin.last.name}")
    private String lastName;

    @Value("${admin.email.address}")
    private String emailAddress;

    @Value("${admin.password}")
    private String password;

    @PostConstruct
    public void initIt() throws Exception {

        User dbUser = userService.findUserByEmail(emailAddress);

        if (dbUser == null) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(emailAddress);
            user.setPassword(password);
            user.setActive(Boolean.TRUE);
            user.setRoleName(RoleNames.ADMIN.name());
            userService.saveUser(user);
        }
        
		Optional<CustomerCode> customerCode = customerCodeRepository.findById(1l);
		if (!customerCode.isPresent()) {
			customerCodeRepository.save(CustomerCode.builder().id(1l).customerCode(0l).build());
		}
        
    }
}
