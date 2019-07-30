package com.user.mngmnt.controller;

import com.user.mngmnt.driver.FastwayRunner;
import com.user.mngmnt.dto.Credential;
import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.enums.DiscountFrequency;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.FastwayCredentials;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.FastwayCredentialRepository;
import com.user.mngmnt.utils.CalcUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Controller
public class ManageCredentialsController {

	@Autowired
	private FastwayCredentialRepository fastwayCredentialRepository;
	
	@GetMapping("/manageCredentials")
	public String utility(HttpServletRequest request) {
		Optional<FastwayCredentials> credential = fastwayCredentialRepository.findById(1l);
		if(credential.isPresent()){
			request.setAttribute("username", credential.get().getUsername());
			request.setAttribute("password", credential.get().getPassword());
		}
		return "manageCredentials";
	}
	
	@PostMapping("/manageCredentials")
	public @ResponseBody ResponseEntity<String> addTransacions(@RequestBody Credential credentail,
			HttpServletRequest request) throws ParseException {
		fastwayCredentialRepository.save(FastwayCredentials.builder()
				.id(1l)
				.password(credentail.getPassword())
				.username(credentail.getUsername())
				.build());
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}
}
