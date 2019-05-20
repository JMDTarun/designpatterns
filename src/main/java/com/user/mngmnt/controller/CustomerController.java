package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;
import com.user.mngmnt.model.SetTopBox;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.CustomerSetTopBoxRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.PackRepository;
import com.user.mngmnt.repository.SetTopBoxRepository;

@Controller
public class CustomerController {

	@Autowired
	private CustomerSetTopBoxRepository customerSetTopBoxRepository;

	@Autowired
	private SetTopBoxRepository setTopBoxRepository;

	@Autowired
	private PackRepository packRepository;

	@Autowired
	private GenericRepository genericRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping("/customer")
	public String getCustomer() {
		return "customer";
	}

	@GetMapping("/allCustomers")
	public @ResponseBody ViewPage<Customer> listCustomers(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredCustomers(filters, pageRequest);
		}
		return new ViewPage<>(customerRepository.findAll(pageRequest));
	}

	public ViewPage<Customer> getFilteredCustomers(String filters, PageRequest pageRequest) {
		long count = customerRepository.count();
		List<Customer> records = genericRepository.findAllWithCriteria(filters, Street.class, pageRequest);
		return ViewPage.<Customer>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/customer/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateStreet(@PathVariable("id") Long id, @ModelAttribute Customer customer) {
		customerRepository.findById(id).ifPresent(n -> {
			customer.setUpdatedAt(Instant.now());
			customer.setId(n.getId());
			customerRepository.save(customer);
		});
	}

	@RequestMapping(value = "/customer", method = POST)
	public ResponseEntity<String> createCustomer(HttpServletRequest request, @ModelAttribute Customer customer) {
		if (customerRepository.findByName(customer.getMobile()) == null) {
			customer.setCreatedAt(Instant.now());
			Customer dbCustomer = customerRepository.save(customer);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbCustomer.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allCustomerSetTopBoxes/{id}")
	public @ResponseBody ViewPage<CustomerSetTopBox> listCustomerSetTopBoxes(@PathVariable("id") Long id,
			@RequestParam("_search") Boolean search, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredPackNetworkChannels(filters, pageRequest);
		}
		return new ViewPage<>(customerRepository.getCutomerSetTopBoxes(id, pageRequest));
	}

	public ViewPage<CustomerSetTopBox> getFilteredPackNetworkChannels(String filters, PageRequest pageRequest) {
		Page<CustomerSetTopBox> setTopBoxes = null;
		return new ViewPage<>(setTopBoxes);
	}

	@RequestMapping(value = "/updateCustomerSetTopBox/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateCustomerSetTopBox(@PathVariable("id") Long id,
			@ModelAttribute CustomerSetTopBox customerSetTopBox) {
		customerRepository.findById(id).ifPresent(n -> {
			CustomerSetTopBox dbCstb = n.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getId().longValue() == customerSetTopBox.getId().longValue()).findFirst()
					.orElse(null);
			if (dbCstb != null) {
				customerSetTopBox.setId(dbCstb.getId());
				BeanUtils.copyProperties(customerSetTopBox, dbCstb);
				customerRepository.save(n);
			}
		});
	}

	@RequestMapping(value = "/createCustomerSetTopBox/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> createCustomerSetTopBox(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute CustomerSetTopBox customerSetTopBox) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			customerSetTopBox.setCreatedAt(Instant.now());
			customer.getCustomerSetTopBoxes().add(customerSetTopBox);
			customerRepository.save(customer);
			updateSetTopBoxStatusToAlloted(customerSetTopBox.getSetTopBox().getId());
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	private void updateSetTopBoxStatusToAlloted(Long setTopBoxId) {
		SetTopBox setTopBox = setTopBoxRepository.getOne(setTopBoxId);
		setTopBox.setSetTopBoxStatus(SetTopBoxStatus.ALLOTED);
		setTopBox.setReason("Assigned To Customer");
		setTopBoxRepository.save(setTopBox);
	}

	private void manageTransaction(CustomerSetTopBox customerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		if (customerSetTopBox.getId() == null) {
			if (customerSetTopBox.getPackPrice() > 0) {
				customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_ADD, customerSetTopBox.getPackPrice(),
						CreditDebit.DEBIT));
			} else {
				Pack pack = packRepository.getOne(customerSetTopBox.getPack().getId());
				customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_ADD, pack.getPrice(), CreditDebit.DEBIT));
			}
			if (customerSetTopBox.getOpeningBalance() > 0) {
				customerLedgres.add(buildCustomerLedgre(customer, Action.OPENING_BALANCE,
						customerSetTopBox.getOpeningBalance(), CreditDebit.CREDIT));
			}
			if (customerSetTopBox.getDiscount() > 0) {
				customerLedgres.add(buildCustomerLedgre(customer, Action.DISCOUNT,
						customerSetTopBox.getOpeningBalance(), CreditDebit.CREDIT));
			}
			if(customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID)) {
				Calendar cal = Calendar.getInstance();
		        cal.setTime(customerSetTopBox.getPaymentStartDate());
		        int days = (cal.getActualMaximum(Calendar.DATE));
		        System.out.println(days);
			}
		}
	}

	private CustomerLedgre buildCustomerLedgre(Customer customer, Action action, Double price,
			CreditDebit creditDebit) {
		return CustomerLedgre.builder().action(Action.PACK_ADD).amount(price).createdAt(Instant.now())
				.creditDebit(creditDebit).customer(customer).build();
	}

	@GetMapping("/allCustomerSetTopBoxChannels/{id}")
	public @ResponseBody ViewPage<NetworkChannel> listCustomerSetTopBoxChannels(@PathVariable("id") Long id,
			@RequestParam("_search") Boolean search, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		return new ViewPage<>(customerRepository.getCutomerSetTopBoxChannels(id, pageRequest));
	}

	@RequestMapping(value = "/addCustomerNetworkChannel/{customemerId}/{customerSetTopBoxId}", method = POST)
	@Transactional
	public ResponseEntity<String> addCustomerNetworkChannel(@PathVariable("customemerId") Long customemerId,
			@PathVariable("customerSetTopBoxId") Long customerSetTopBoxId, HttpServletRequest request,
			@RequestParam("networkChannelId") Long networkChannelId) {
		Customer customer = customerRepository.getOne(customemerId);
		if (customer != null) {
			CustomerSetTopBox cstb = customer.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getId().longValue() == customerSetTopBoxId.longValue()).findFirst().get();
			cstb.getNetworkChannels().add(NetworkChannel.builder().id(networkChannelId).build());

			customerRepository.save(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}
