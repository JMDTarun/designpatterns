package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.user.mngmnt.driver.FastwayRunner;
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
import com.user.mngmnt.enums.DiscountFrequency;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.utils.CalcUtils;

@Controller
public class UtilityController {

	@Autowired
	private CustomerLedgreRepository customerLedgreRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private FastwayRunner fastwayRunner;
	
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
			c.getCustomerSetTopBoxes().stream()
					.filter(cstb -> CustomerSetTopBoxStatus.ACTIVE.equals(cstb.getCustomerSetTopBoxStatus())
							&& getLocalDate(Date.from(cstb.getCreatedAt()))
							.compareTo(getLocalDate(Date.from(cstb.getCreatedAt())).withMonth(month)) < 0)
					.forEach(cstb -> {
						CustomerLedgre customerLedgre = customerLedgreRepository
								.findByCustomerAndCustomerSetTopBoxAndActionAndMonth(c, cstb, Action.MONTHLY_PACK_PRICE, mth.toString());
						boolean isPrepaid = cstb.getPaymentMode().equals(PaymentMode.PREPAID) ? true : false;
						if (isPrepaid && customerLedgre == null) {
							addEntriesToCustomerLedgre(mth, c, cstb, false);
						} else if (!isPrepaid) {
							if(getLocalDate(new Date()).compareTo(getLocalDate(cstb.getBillingCycle()).withMonth(month)) >= 0) {
								Month previousMonth = Month.of(month - 1);
								List<CustomerLedgre> customerLedgres = customerLedgreRepository.findByCustomerAndCustomerSetTopBoxAndMonth(c, cstb, previousMonth.toString());
								for(CustomerLedgre cl: customerLedgres) {
									c.setAmountCredit(CalcUtils.round(c.getAmountCredit() + cl.getAmountCredit(), 2));
									c.setAmountDebit(CalcUtils.round(c.getAmountDebit() + cl.getAmountDebit(), 2));
									c.setBalance(CalcUtils.round(c.getAmountCredit() - c.getAmountDebit(), 2));
									cl.setOnHold(false);
									customerLedgreRepository.save(cl);
								}
								if(customerLedgre == null) {
									addEntriesToCustomerLedgre(mth, c, cstb, true);
								} 
							} else {
								if(customerLedgre == null) {
									addEntriesToCustomerLedgre(mth, c, cstb, true);
								}
							}
						}
					});
			customerRepository.save(c);
		});
		
		final HttpHeaders headers = new HttpHeaders();
		URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
		headers.put("Location", singletonList(uri.toASCIIString()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	private void addEntriesToCustomerLedgre(Month mth, Customer c, CustomerSetTopBox cstb, boolean isOnHold) {
		Double amountCredit = 0.0;
		Double amountDebit = 0.0;

		customerLedgreRepository.save(CustomerLedgre.builder().action(Action.MONTHLY_PACK_PRICE)
				.amountDebit(CalcUtils.round(cstb.getPackPrice(), 2)).createdAt(Instant.now()).month(mth.toString())
				.creditDebit(CreditDebit.DEBIT).customer(c).customerSetTopBox(cstb).customerNetworkChannel(null)
				.reason(null).isOnHold(isOnHold).customerLedgreEntry(CustomerLedgreEntry.UTILITY).build());
		if (!isOnHold) {
			amountDebit += cstb.getPackPrice();
		}
		if (cstb.getDiscount() != null && cstb.getDiscount() > 0 && DiscountFrequency.MONTHLY.equals(cstb.getDiscountFrequency())) {
			customerLedgreRepository.save(CustomerLedgre
					.builder()
					.action(Action.MONTHLY_DISCOUNT)
					.amountCredit(CalcUtils.round(cstb.getDiscount(), 2))
					.createdAt(Instant.now())
					.month(mth.toString())
					.creditDebit(CreditDebit.CREDIT)
					.customer(c)
					.customerSetTopBox(cstb)
					.customerNetworkChannel(null)
					.reason(null).isOnHold(isOnHold).customerLedgreEntry(CustomerLedgreEntry.UTILITY).build());
			if (!isOnHold) {
				amountCredit += cstb.getDiscount();
			}
		}
		List<CustomerNetworkChannel> networkChannels = cstb.getCustomerNetworkChannels().stream()
				.filter(nc -> !nc.isDeleted()).collect(Collectors.toList());
		for (CustomerNetworkChannel nc : networkChannels) {
			customerLedgreRepository.save(CustomerLedgre.builder().action(Action.MONTHLY_CHANNEL_PRICE)
					.amountDebit(nc.getNetworkChannel().getTotal()).createdAt(Instant.now()).month(mth.toString())
					.creditDebit(CreditDebit.DEBIT).customer(c).customerSetTopBox(cstb).customerNetworkChannel(nc)
					.reason(null).isOnHold(isOnHold).customerLedgreEntry(CustomerLedgreEntry.UTILITY).build());
			if (!isOnHold) {
				amountDebit += cstb.getPackPrice();
			}
		}
		c.setAmountDebit(CalcUtils.round(c.getAmountDebit() + amountDebit, 2));
		c.setAmountCredit(CalcUtils.round(c.getAmountCredit() + amountCredit, 2));
		c.setBalance(CalcUtils.round(c.getAmountCredit() - c.getAmountDebit(), 2));
	}
	
	private LocalDate getLocalDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}
	
	@GetMapping("/revertUtility")
	@Transactional
	public @ResponseBody ResponseEntity<String> revertTransacions(@RequestParam("month") Integer month,
			HttpServletRequest request) throws ParseException {
		Month mth = Month.of(month);
		List<CustomerLedgre> customerLedgresToDelete = customerLedgreRepository.findByCustomerLedgreEntryAndMonth(CustomerLedgreEntry.UTILITY, mth.toString());
		
		for(CustomerLedgre customerLedgre: customerLedgresToDelete) {
			Customer customer = customerLedgre.getCustomer();
			customer.setAmountCredit(CalcUtils.round(customer.getAmountCredit() - customerLedgre.getAmountCredit(), 2));
			customer.setAmountDebit(CalcUtils.round(customer.getAmountDebit() - customerLedgre.getAmountDebit(), 2));
			customer.setBalance(CalcUtils.round(customer.getAmountCredit() - customer.getAmountDebit(), 2));
			customerRepository.save(customer);
		}
		
		customerLedgreRepository.deleteAll(customerLedgresToDelete);
		final HttpHeaders headers = new HttpHeaders();
		URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
		headers.put("Location", singletonList(uri.toASCIIString()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/uploadActions")
	public @ResponseBody ResponseEntity<Boolean> uploadActions() {
		fastwayRunner.run();
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
}
