package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;
import com.user.mngmnt.model.RemoveCustomerSetTopBox;
import com.user.mngmnt.model.SetTopBox;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.CustomerSetTopBoxRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.PackRepository;
import com.user.mngmnt.repository.SetTopBoxRepository;

@Controller
public class CustomerController {

	@Autowired
	private SetTopBoxRepository setTopBoxRepository;

	@Autowired
	private PackRepository packRepository;

	@Autowired
	private GenericRepository genericRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerLedgreRepository customerLedgreRepository;

	@Autowired
	private CustomerSetTopBoxRepository customerSetTopBoxRepository;

	private static DecimalFormat df = new DecimalFormat("0.00");

	@GetMapping("/customer")
	public String getCustomer() {
		return "customer";
	}

	@GetMapping("/allCustomers")
	public @ResponseBody ViewPage<Customer> listCustomers(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredCustomers(filters, pageRequest);
		}
		return new ViewPage<>(customerRepository.findAll(pageRequest));
	}

	public ViewPage<Customer> getFilteredCustomers(String filters, PageRequest pageRequest) throws ParseException {
		long count = customerRepository.count();
		List<Customer> records = genericRepository.findAllWithCriteria(filters, Customer.class, pageRequest);
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
	@Transactional
	public void updateCustomerSetTopBox(@PathVariable("id") Long id,
			@ModelAttribute CustomerSetTopBox customerSetTopBox) {
		customerRepository.findById(id).ifPresent(n -> {
			CustomerSetTopBox dbCstb = n.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getId().longValue() == customerSetTopBox.getId().longValue()).findFirst()
					.orElse(null);
			if (dbCstb != null) {
				customerSetTopBox.setId(dbCstb.getId());
				manageTransactionForPakChange(customerSetTopBox, dbCstb, n);
				manageTransactionForBillingCycleChange(customerSetTopBox, dbCstb, n);
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
			customerSetTopBoxRepository.save(customerSetTopBox);
			customer.getCustomerSetTopBoxes().add(customerSetTopBox);
			customerRepository.save(customer);
			manageTransactionForNewCustomerSetTopBox(customerSetTopBox, customer);
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

	private void manageTransactionForNewCustomerSetTopBox(CustomerSetTopBox customerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		Double packPrice = 0.0;
		if (customerSetTopBox.getPackPrice() > 0) {
			packPrice = customerSetTopBox.getPackPrice();
			customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_ADD, customerSetTopBox.getPackPrice(),
					CreditDebit.DEBIT, customerSetTopBox));
		} else {
			Pack pack = packRepository.getOne(customerSetTopBox.getPack().getId());
			packPrice = pack.getPrice();
			customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_ADD, pack.getPrice(), CreditDebit.DEBIT,
					customerSetTopBox));
		}
		if (customerSetTopBox.getOpeningBalance() != null && customerSetTopBox.getOpeningBalance() > 0) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.OPENING_BALANCE,
					customerSetTopBox.getOpeningBalance(), CreditDebit.DEBIT, customerSetTopBox));
		}
		if (customerSetTopBox.getDiscount() != null && customerSetTopBox.getDiscount() > 0) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.DISCOUNT, customerSetTopBox.getDiscount(),
					CreditDebit.CREDIT, customerSetTopBox));
		}
		// Payment Calculations
		{
			boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
			LocalDate entryDate = getLocalDate(customerSetTopBox.getEntryDate());
			LocalDate billingDate = isPrepaid ? entryDate.withDayOfMonth(1)
					: getLocalDate(customerSetTopBox.getBillingCycle());
			LocalDate paymentStartDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
			long days = Duration.between(billingDate.atStartOfDay(), paymentStartDate.atStartOfDay()).toDays();
			if (days != 0) {
				Double oneDayCharge = packPrice / entryDate.lengthOfMonth();
				Double balance = days * oneDayCharge;
				customerLedgres.add(buildCustomerLedgre(customer, Action.PAYMENT_START_DISCOUNT, Math.abs(balance),
						days > 0 ? CreditDebit.CREDIT : CreditDebit.DEBIT, customerSetTopBox));
			}
		}
		if (customerLedgres.size() > 0) {
			customerLedgreRepository.saveAll(customerLedgres);
		}
	}

	private void manageTransactionForPakChange(CustomerSetTopBox customerSetTopBox,
			CustomerSetTopBox dbCustomerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		Double packPrice = 0.0;
		if (customerSetTopBox.getId() != null) {
			if (Double.compare(dbCustomerSetTopBox.getPackPrice(), customerSetTopBox.getPackPrice()) != 0) {
				packPrice = customerSetTopBox.getPackPrice() - dbCustomerSetTopBox.getPackPrice();
				CreditDebit type = packPrice > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT;
				boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
				LocalDate entryDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
				LocalDate billingDate = isPrepaid ? entryDate.withDayOfMonth(entryDate.lengthOfMonth())
						: getLocalDate(customerSetTopBox.getBillingCycle()).plusMonths(1);
				LocalDate paymentStartDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
				long days = Duration.between(paymentStartDate.atStartOfDay(), billingDate.atStartOfDay()).toDays();
				if (days != 0) {
					Double oneDayCharge = packPrice / entryDate.lengthOfMonth();
					Double balance = days * oneDayCharge;
					customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_CHANGE, Math.abs(balance), type,
							dbCustomerSetTopBox));
					customerLedgreRepository.saveAll(customerLedgres);
				}
			}
		}
	}

	private void manageTransactionForBillingCycleChange(CustomerSetTopBox customerSetTopBox,
			CustomerSetTopBox dbCustomerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		if (customerSetTopBox.getId() != null && dbCustomerSetTopBox.getBillingCycle() != null) {
			LocalDate dbBillingCycle = getLocalDate(dbCustomerSetTopBox.getBillingCycle());
			LocalDate newBillingDate = getLocalDate(customerSetTopBox.getBillingCycle());
			LocalDate dateForMonthDays = getLocalDate(dbCustomerSetTopBox.getBillingCycle()).plusMonths(1);
			long monthDays = Duration.between(dbBillingCycle.atStartOfDay(), dateForMonthDays.atStartOfDay()).toDays();
			long days = Duration.between(dbBillingCycle.atStartOfDay(), newBillingDate.atStartOfDay()).toDays();
			if (days != 0) {
				Double oneDayCharge = customerSetTopBox.getPackPrice() / monthDays;
				Double balance = days * oneDayCharge;
				customerLedgres.add(buildCustomerLedgre(customer, Action.PAYMENT_START_DISCOUNT, Math.abs(balance),
						days > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT, dbCustomerSetTopBox));
				customerLedgreRepository.saveAll(customerLedgres);
			}
		}
	}

	private LocalDate getLocalDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}

	private CustomerLedgre buildCustomerLedgre(Customer customer, Action action, Double price, CreditDebit creditDebit,
			CustomerSetTopBox customerSetTopBox) {
		return CustomerLedgre.builder().action(action).amount(round(price, 2)).createdAt(Instant.now())
				.creditDebit(creditDebit).customer(customer).customerSetTopBox(customerSetTopBox).build();
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	@GetMapping("/allCustomerSetTopBoxChannels/{id}")
	public @ResponseBody ViewPage<CustomerNetworkChannel> listCustomerSetTopBoxChannels(@PathVariable("id") Long id,
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
			//cstb.getNetworkChannels().add(NetworkChannel.builder().id(networkChannelId).build());
			customerRepository.save(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/removeCustomerSetTopBox/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> removeCustomerSetTopBox(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute RemoveCustomerSetTopBox removeCustomerSetTopBox) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			CustomerSetTopBox dbCstb = customerSetTopBoxRepository.getOne(removeCustomerSetTopBox.getId());
			dbCstb.getSetTopBox().setSetTopBoxStatus(removeCustomerSetTopBox.getSetTopBoxStatus());
			dbCstb.getSetTopBox().setUpdatedAt(Instant.now());
			dbCstb.setUpdatedAt(Instant.now());
			customerSetTopBoxRepository.save(dbCstb);
			if (removeCustomerSetTopBox.getAmount() != null && removeCustomerSetTopBox.getAmount().doubleValue() > 0) {
				customerLedgreRepository.save(buildCustomerLedgre(customer, Action.SET_TOP_BOX_REMOVE,
						removeCustomerSetTopBox.getAmount(), CreditDebit.CREDIT, dbCstb));
			}
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}