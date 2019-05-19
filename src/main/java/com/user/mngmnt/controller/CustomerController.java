package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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

import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.CustomerSetTopBoxRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.SetTopBoxRepository;

@Controller
public class CustomerController {

	@Autowired
	private CustomerSetTopBoxRepository customerSetTopBoxRepository;

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
			CustomerSetTopBox cstb = n.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getSetTopBox().getId().longValue() == customerSetTopBox.getId().longValue())
					.findFirst().get();
			if (cstb != null) {
				customerSetTopBox.setId(cstb.getId());
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
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allCustomerSetTopBoxChannels/{id}")
	public @ResponseBody ViewPage<NetworkChannel> listCustomerSetTopBoxChannels(@PathVariable("id") Long id,
			@RequestParam("_search") Boolean search, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
//		if (search) {
//			return getFilteredPackNetworkChannels(filters, pageRequest);
//		}
		return new ViewPage<>(customerRepository.getCutomerSetTopBoxChannels(id, pageRequest));
	}
	
}
