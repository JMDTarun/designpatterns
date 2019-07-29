package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.PlanChangeControl;
import com.user.mngmnt.model.PlanChangeControlAction;
import com.user.mngmnt.model.ResponseHandler;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.PlanChangeControlRepository;
import com.user.mngmnt.util.CalcUtils;

@Controller
public class CustomerLedgreController {

	@Autowired
	private CustomerLedgreRepository customerLedgreRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private GenericRepository genericRepository;
	
	@Autowired
	private PlanChangeControlRepository planChangeControlRepository;
	
	@GetMapping("/customerPayment")
	public String getCustomer() {
		return "customerPayment";
	}
	
	@GetMapping("/allCustomerPayments")
	public @ResponseBody ViewPage<CustomerLedgre> listCustomers(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredCustomerPayments(filters, pageRequest);
		}
		return new ViewPage<>(customerLedgreRepository.findByCustomerLedgreEntry(CustomerLedgreEntry.MANUAL, pageRequest));
	}

	public ViewPage<CustomerLedgre> getFilteredCustomerPayments(String filters, PageRequest pageRequest) throws ParseException {
		long count = customerLedgreRepository.countByCustomerLedgreEntry(CustomerLedgreEntry.MANUAL);
		List<CustomerLedgre> records = genericRepository.findAllWithCriteria(filters, CustomerLedgre.class, pageRequest);
		return ViewPage.<CustomerLedgre>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/customerPayment/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<ResponseHandler> updateCustomerPayment(@PathVariable("id") Long id, @ModelAttribute CustomerLedgre customerLedgre) {
		Optional<CustomerLedgre> dbCustomerLedgre = customerLedgreRepository.findById(id);
		if(dbCustomerLedgre.isPresent()) {
			CustomerLedgre c = dbCustomerLedgre.get();
			Double dbAmountCredit = c.getAmountCredit();
			Double amountCredit = customerLedgre.getAmountCredit();
			
			c.setAmountCredit(customerLedgre.getAmountCredit());
			c.setPaymentDate(customerLedgre.getPaymentDate());
			c.setPaymentType(customerLedgre.getPaymentType());
			c.setPaymentMode(customerLedgre.getPaymentMode());
			c.setChequeDate(customerLedgre.getChequeDate());
			c.setChequeNumber(customerLedgre.getChequeNumber());
			
			Customer customer = c.getCustomer();
			customer.setAmountCredit(CalcUtils.round(customer.getAmountCredit() + (amountCredit - dbAmountCredit)));
			customer.setBalance(CalcUtils.round(customer.getAmountCredit() - customer.getAmountDebit()));
			customerRepository.save(customer);
			
			customerLedgreRepository.save(c);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/customerPayment", method = POST)
	@Transactional
	public ResponseEntity<ResponseHandler> createCustomerPayment(HttpServletRequest request,
			@ModelAttribute CustomerLedgre customerLedgre) {
		customerLedgre.setCreatedAt(Instant.now());
		customerLedgre.setAction(Action.PAYMENT_CREDIT);
		customerLedgre.setCustomerLedgreEntry(CustomerLedgreEntry.MANUAL);
		customerLedgre.setCreditDebit(CreditDebit.CREDIT);
		customerLedgre.setOnHold(false);
		CustomerLedgre dbCustomerLedgre = customerLedgreRepository.save(customerLedgre);
		Customer customer = customerRepository.getOne(customerLedgre.getCustomer().getId());
		customer.setAmountCredit(customer.getAmountCredit() + customerLedgre.getAmountCredit());
		customer.setBalance(customer.getAmountCredit() - customer.getAmountDebit());
		if(customer.getBalance() > (customer.getCustomerType().getMaxAmount() * -1)) {
			List<CustomerSetTopBox> customerSetTopBoxes = customer.getCustomerSetTopBoxes().stream()
					.filter(box -> CustomerSetTopBoxStatus.DEACTIVE.equals(box.getCustomerSetTopBoxStatus()))
					.filter(box -> box.getDeactivateReason().equals("NO PAYMENT"))
					.filter(box -> box.isDeleted()).collect(Collectors.toList());
			for (CustomerSetTopBox dbCstb : customerSetTopBoxes) {
				activateSetTopBoxes(dbCstb);
			}
		}
		customerRepository.save(customer);
		URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
				dbCustomerLedgre.getId());
		final HttpHeaders headers = new HttpHeaders();
		headers.put("Location", singletonList(uri.toASCIIString()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}
	
	private void activateSetTopBoxes(CustomerSetTopBox dbCstb) {
		planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.ACTIVATE)
				.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber()).build());

		planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.ADD)
				.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber()).plans(dbCstb.getPack().getName())
				.listName("SUGGESTIVE PACKS").build());

		dbCstb.getCustomerNetworkChannels().stream().forEach(cnc -> {
			planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.ADD)
					.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber())
					.plans(cnc.getNetworkChannel().getName())
					.listName(cnc.getNetworkChannel().getNetwork().getName()).build());
		});
	}
	
}
