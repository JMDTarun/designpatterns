package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.model.ResponseHandler;
import com.user.mngmnt.model.User;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.UserRepository;
import com.user.mngmnt.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private GenericRepository genericRepository;

    @GetMapping("/")
    public String home() {
        return "customer";
    }
    
    @GetMapping("/users")
    public String createUser() {
        return "create-user";
    }

    @GetMapping("/allUsers")
    public @ResponseBody ViewPage<User> listNetworks(@RequestParam("_search") Boolean search,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "firstName", required = false) String sort) throws ParseException {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        if (search) {
            return getFilteredUsers(filters, pageRequest);
        }
        return new ViewPage<>(userRepository.findAll(pageRequest));
    }

    public ViewPage<User> getFilteredUsers(String filters, PageRequest pageRequest) throws ParseException {
        long count = userRepository.count();
        List<User> records = genericRepository.findAllWithCriteria(filters, User.class, pageRequest);
        return ViewPage.<User>builder().rows(records).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }

    @RequestMapping(value = "/user/{id}", method = POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable("id") Long id, @ModelAttribute User user) {
        userRepository.findById(id).ifPresent(n -> {
            user.setUpdatedAt(Instant.now());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setId(n.getId());
            userRepository.save(user);
        });
    }

    @RequestMapping(value = "/user", method = POST)
    public ResponseEntity<ResponseHandler> createUser(HttpServletRequest request, @ModelAttribute User user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail()) == null) {
            user.setCreatedAt(Instant.now());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User dbUser = userRepository.save(user);
            URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
                    dbUser.getId());
            final HttpHeaders headers = new HttpHeaders();
            headers.put("Location", singletonList(uri.toASCIIString()));
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(ResponseHandler.builder()
                .errorCode(HttpStatus.CONFLICT.value())
                .errorCause("User with email already exists.")
                .build(), HttpStatus.CONFLICT);
    }

    @GetMapping("/403")
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("403");
        return modelAndView;
    }



    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }

}
