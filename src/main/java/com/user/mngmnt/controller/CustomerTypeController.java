package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

import com.user.mngmnt.model.CustomerType;
import com.user.mngmnt.model.ResponseHandler;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerTypeRepository;
import com.user.mngmnt.repository.GenericRepository;

@Controller
public class CustomerTypeController {

	@Autowired
	private CustomerTypeRepository customerTypeRepository;

	@Autowired
	private GenericRepository genericRepository;

	@GetMapping("/customerType")
	public String getCustomerType() {
		return "customerType";
	}
	
	@GetMapping("/allCustomerTypes")
	public @ResponseBody ViewPage<CustomerType> listNetworks(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredCustomerTypes(filters, pageRequest);
		}
		return new ViewPage<>(customerTypeRepository.findAll(pageRequest));
	}

	public ViewPage<CustomerType> getFilteredCustomerTypes(String filters, PageRequest pageRequest)
			throws ParseException {
		long count = customerTypeRepository.count();
		List<CustomerType> records = genericRepository.findAllWithCriteria(filters, CustomerType.class, pageRequest);
		return ViewPage.<CustomerType>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();

	}

	@RequestMapping(value = "/customerType/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNetwork(@PathVariable("id") Long id, @ModelAttribute CustomerType customerType) {
		customerTypeRepository.findById(id).ifPresent(n -> {
			customerType.setUpdatedAt(Instant.now());
			customerType.setId(n.getId());
			customerTypeRepository.save(customerType);
		});
	}

	@RequestMapping(value = "/customerType", method = POST)
	public ResponseEntity<ResponseHandler> createNetwork(HttpServletRequest request, @ModelAttribute CustomerType customerType) {
		if (customerTypeRepository.findByCustomerType(customerType.getCustomerType()) == null) {
			customerType.setCreatedAt(Instant.now());
			CustomerType dbCustomerRype = customerTypeRepository.save(customerType);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbCustomerRype.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
				.errorCode(HttpStatus.CONFLICT.value())
				.errorCause("Customer Type already exists.")
				.build(), HttpStatus.CONFLICT);
		//return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/getAllCustomerTypes")
	public @ResponseBody Map<Long, CustomerType> getAllStreets() {
		List<CustomerType> customerTypes = customerTypeRepository.findAll();
		return customerTypes.stream().filter(n -> n != null && n.getCustomerType() != null)
				.collect(Collectors.toMap(CustomerType::getId, c -> c));
	}

}
