package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.time.Month;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerRepository;

@Controller
public class UtilityController {

	@Autowired
	private CustomerLedgreRepository customerLedgreRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@GetMapping("/utility")
	public String utility() {
		return "utility";
	}
	
	@GetMapping("/runUtility")
	@Transactional
	public @ResponseBody ResponseEntity<String> addTransacions(@RequestParam("month") Integer month,
			HttpServletRequest request) throws ParseException {
		Month mth = Month.of(month);
		List<Customer> customers = customerRepository.findAll();
		customers.stream().filter(c -> !c.isDeleted()).forEach(c -> {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println(c.getCustomerSetTopBoxes().size());
			c.getCustomerSetTopBoxes().stream()
					.filter(cstb -> CustomerSetTopBoxStatus.ACTIVE.equals(cstb.getCustomerSetTopBoxStatus()))
					.forEach(cstb -> {
						CustomerLedgre customerLedgre = customerLedgreRepository
								.findByCustomerAndCustomerSetTopBoxAndActionAndMonth(c, cstb, Action.MONTHLY_PACK_PRICE, mth.toString());
						boolean isPrepaid = cstb.getPaymentMode().equals(PaymentMode.PREPAID) ? true : false;
						if (isPrepaid && customerLedgre == null) {
							addEntriesToCustomerLedgre(mth, c, cstb, false);
						} else if (!isPrepaid && cstb.getBillingCycle().compareTo(new Date()) <= 0) {
							if(customerLedgre != null) {
								customerLedgre.setOnHold(false);
								customerLedgreRepository.save(customerLedgre);
							} else {
								addEntriesToCustomerLedgre(mth, c, cstb, true);
							}
						}
					});
		});
		
		final HttpHeaders headers = new HttpHeaders();
		URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
		headers.put("Location", singletonList(uri.toASCIIString()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	private void addEntriesToCustomerLedgre(Month mth, Customer c, CustomerSetTopBox cstb, boolean isOnHold) {
		customerLedgreRepository.save(CustomerLedgre.builder()
		.action(Action.MONTHLY_PACK_PRICE)
		.amount(cstb.getPackPrice())
		.createdAt(Instant.now())
		.month(mth.toString())
		.creditDebit(CreditDebit.DEBIT)
		.customer(c)
		.customerSetTopBox(cstb)
		.customerNetworkChannel(null)
		.reason(null)
		.isOnHold(cstb.getPaymentMode().equals(PaymentMode.PREPAID) ? false: true)
		.customerLedgreEntry(CustomerLedgreEntry.UTILITY)
		.build());
		cstb.getCustomerNetworkChannels().stream().filter(nc -> !nc.isDeleted()).forEach(nc -> {
			customerLedgreRepository.save(CustomerLedgre.builder()
					.action(Action.MONTHLY_CHANNEL_PRICE)
					.amount(nc.getNetworkChannel().getTotal())
					.createdAt(Instant.now())
					.month(mth.toString())
					.creditDebit(CreditDebit.DEBIT)
					.customer(c)
					.customerSetTopBox(cstb)
					.customerNetworkChannel(nc)
					.reason(null)
					.isOnHold(isOnHold)
					.customerLedgreEntry(CustomerLedgreEntry.UTILITY)
					.build());
		});
	}
	
}
