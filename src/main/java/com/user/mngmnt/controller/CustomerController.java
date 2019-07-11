package com.user.mngmnt.controller;

import static com.user.mngmnt.util.DateCalculationUtil.DATE_FORMATTER;
import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.enums.DiscountFrequency;
import com.user.mngmnt.model.Area;
import com.user.mngmnt.model.CustomerType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;
import com.user.mngmnt.model.RemoveCustomerNetworkChannel;
import com.user.mngmnt.model.RemoveCustomerSetTopBox;
import com.user.mngmnt.model.ResponseHandler;
import com.user.mngmnt.model.SetTopBox;
import com.user.mngmnt.model.SetTopBoxActivateDeactivate;
import com.user.mngmnt.model.SetTopBoxReplacement;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerLedgreRepository;
import com.user.mngmnt.repository.CustomerNetworkChannelRepository;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.CustomerSetTopBoxRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.NetworkChannelRepository;
import com.user.mngmnt.repository.PackRepository;
import com.user.mngmnt.repository.SetTopBoxReplacementRepository;
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

	@Autowired
	private CustomerNetworkChannelRepository customerNetworkChannelRepository;
	
	@Autowired
	private SetTopBoxReplacementRepository setTopBoxReplacementRepository;

	@Autowired
	private NetworkChannelRepository networkChannelRepository;

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
	public ResponseEntity<ResponseHandler> updateStreet(@PathVariable("id") Long id, @ModelAttribute Customer customer) {
		Optional<Customer> dbCustomer = customerRepository.findById(id);
		if(dbCustomer.isPresent()) {
			Customer c = dbCustomer.get();
			//customerRepository.save(customer);
			if(!customer.getCustomerCode().equals(c.getCustomerCode())) {
				if (customerRepository.findByCustomerCode(customer.getCustomerCode()) != null) {
					return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
							.errorCode(HttpStatus.CONFLICT.value())
							.errorCause("Customer with Customer Code already exist")
							.build(), HttpStatus.CONFLICT);
				}
			}
			c.setUpdatedAt(Instant.now());
			c.setName(customer.getName());
			c.setAddress(customer.getAddress());
			c.setCustomerCode(customer.getCustomerCode());
			c.setCity(customer.getCity());
			c.setMobile(customer.getMobile());
			c.setLandLine(customer.getLandLine());
			c.setArea(customer.getArea());
			c.setSubArea(customer.getSubArea());
			c.setStreet(customer.getStreet());
			saveCustomer(c);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/customer", method = POST)
	public ResponseEntity<ResponseHandler> createCustomer(HttpServletRequest request, @ModelAttribute Customer customer) {
		if (customerRepository.findByCustomerCode(customer.getCustomerCode()) == null) {
			customer.setCreatedAt(Instant.now());
			
			Customer dbCustomer = saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbCustomer.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		//return new ResponseEntity<>(HttpStatus.CONFLICT);
		return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
				.errorCode(HttpStatus.CONFLICT.value())
				.errorCause("Customer with Customer Code already exist")
				.build(), HttpStatus.CONFLICT);
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
				manageTransactionForBoxPriceChange(customerSetTopBox, dbCstb, n);
				customerSetTopBox.setCustomerNetworkChannels(dbCstb.getCustomerNetworkChannels());
				BeanUtils.copyProperties(customerSetTopBox, dbCstb);
				saveCustomer(n);	
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
			manageTransactionForNewCustomerSetTopBox(customerSetTopBox, customer);
			updateSetTopBoxStatus(customerSetTopBox.getSetTopBox().getId(), SetTopBoxStatus.ALLOTED,
					"Assigned To Customer");
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	private void updateSetTopBoxStatus(Long setTopBoxId, SetTopBoxStatus setTopBoxStatus, String reason) {
		SetTopBox setTopBox = setTopBoxRepository.getOne(setTopBoxId);
		setTopBox.setSetTopBoxStatus(setTopBoxStatus);
		setTopBox.setReason(reason);
		setTopBoxRepository.save(setTopBox);
	}

	private void manageTransactionForNewCustomerSetTopBox(CustomerSetTopBox customerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		Double packPrice = 0.0;
		boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
		Pack pack = packRepository.getOne(customerSetTopBox.getPack().getId());
		packPrice = pack.getTotal();
		if (customerSetTopBox.getPackPrice() > 0) {
			customerSetTopBox.setPackPriceDifference(pack.getTotal() - customerSetTopBox.getPackPrice());
			packPrice = customerSetTopBox.getPackPrice();
		} 
		if(isPrepaid) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.MONTHLY_PACK_PRICE, packPrice,
					CreditDebit.DEBIT, customerSetTopBox, null, null));
		}
		
		if (customerSetTopBox.getOpeningBalance() != null && customerSetTopBox.getOpeningBalance() > 0) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.OPENING_BALANCE,
					customerSetTopBox.getOpeningBalance(), CreditDebit.DEBIT, customerSetTopBox, null, null));
		}
		if (customerSetTopBox.getDiscount() != null && customerSetTopBox.getDiscount() > 0) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.DISCOUNT, customerSetTopBox.getDiscount(),
					CreditDebit.CREDIT, customerSetTopBox, null, null));
		}
		
		if(customerSetTopBox.getSetTopBoxPrice() > 0) {
			customerLedgres.add(buildCustomerLedgre(customer, Action.SET_TOP_BOX_PRICE, customerSetTopBox.getSetTopBoxPrice(),
					CreditDebit.DEBIT, customerSetTopBox, null, null));
		}
		
		// Payment Calculations
		CustomerLedgre cl = customerDiscountForNewTransaction(customerSetTopBox, customer,
				Action.PAYMENT_START_ADJUSTMENT, packPrice);
		if (cl != null) {
			customerLedgres.add(cl);
		}

		if (customerLedgres.size() > 0) {
			customerLedgreRepository.saveAll(customerLedgres);
		}
	}

	private CustomerLedgre customerDiscountForNewTransaction(CustomerSetTopBox customerSetTopBox, Customer customer,
			Action action, Double packPrice) {
		boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
		LocalDate entryDate = getLocalDate(customerSetTopBox.getEntryDate());
		LocalDate billingDate = isPrepaid ? entryDate.withDayOfMonth(1)
				: getLocalDate(customerSetTopBox.getBillingCycle());
		LocalDate paymentStartDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
		long days = Duration.between(billingDate.atStartOfDay(), paymentStartDate.atStartOfDay()).toDays();

		if (days != 0) {
			Double oneDayCharge = packPrice / entryDate.lengthOfMonth();
			Double balance = days * oneDayCharge;
			return buildCustomerLedgre(customer, action, Math.abs(balance),
					days > 0 ? CreditDebit.CREDIT : CreditDebit.DEBIT, customerSetTopBox, null, null);
		}
		return null;
	}
	
	private void manageTransactionForBoxPriceChange(CustomerSetTopBox customerSetTopBox,
			CustomerSetTopBox dbCustomerSetTopBox, Customer customer) {
		if (Double.compare(dbCustomerSetTopBox.getSetTopBoxPrice(), customerSetTopBox.getSetTopBoxPrice()) != 0) {
			Double difference = dbCustomerSetTopBox.getSetTopBoxPrice() - customerSetTopBox.getSetTopBoxPrice();
			customerLedgreRepository.save(buildCustomerLedgre(customer, Action.SET_TOP_BOX_PRICE_CHANGE, Math.abs(difference),
					difference > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT, dbCustomerSetTopBox, null, null));
		}
	}
	
	private void manageTransactionForPakChange(CustomerSetTopBox customerSetTopBox,
			CustomerSetTopBox dbCustomerSetTopBox, Customer customer) {
		List<CustomerLedgre> customerLedgres = new ArrayList<>();
		Double packPrice = 0.0;
		if (customerSetTopBox.getId() != null) {
			if (Double.compare(dbCustomerSetTopBox.getPackPrice(), customerSetTopBox.getPackPrice()) != 0) {
				Pack pack = packRepository.getOne(customerSetTopBox.getPack().getId());
				customerSetTopBox.setPackPriceDifference(pack.getTotal() - customerSetTopBox.getPackPrice());
				packPrice = customerSetTopBox.getPackPrice() - dbCustomerSetTopBox.getPackPrice();
				CreditDebit type = packPrice > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT;
				boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
				LocalDate entryDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
				LocalDate billingDate = isPrepaid ? entryDate.withDayOfMonth(entryDate.lengthOfMonth())
						: getLocalDate(customerSetTopBox.getBillingCycle())/*.plusMonths(1)*/;
				LocalDate paymentStartDate = getLocalDate(customerSetTopBox.getPaymentStartDate());
				long days = Duration.between(paymentStartDate.atStartOfDay(), billingDate.atStartOfDay()).toDays();
				if (days != 0) {
					Double oneDayCharge = packPrice / entryDate.lengthOfMonth();
					Double balance = days * oneDayCharge;
					customerLedgres.add(buildCustomerLedgre(customer, Action.PACK_CHANGE, Math.abs(balance), type,
							dbCustomerSetTopBox, null, null));
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
			dbBillingCycle.withMonth(newBillingDate.getMonthValue());
			LocalDate dateForMonthDays = getLocalDate(dbCustomerSetTopBox.getBillingCycle()).plusMonths(1);
			long monthDays = Duration.between(dbBillingCycle.atStartOfDay(), dateForMonthDays.atStartOfDay()).toDays();
			long days = Duration.between(dbBillingCycle.atStartOfDay(), newBillingDate.atStartOfDay()).toDays();
			if (days != 0) {
				Double oneDayCharge = customerSetTopBox.getPackPrice() / monthDays;
				Double balance = days * oneDayCharge;
				customerLedgres.add(buildCustomerLedgre(customer, Action.PAYMENT_START_ADJUSTMENT, Math.abs(balance),
						days > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT, dbCustomerSetTopBox, null, null));
				if (dbCustomerSetTopBox.getCustomerNetworkChannels() != null
						&& !CollectionUtils.isEmpty(dbCustomerSetTopBox.getCustomerNetworkChannels())) {
					for (CustomerNetworkChannel cnc : dbCustomerSetTopBox.getCustomerNetworkChannels()) {
						if (!cnc.isDeleted()) {
							Double price = cnc.getNetworkChannel().getMonthlyRent() + cnc.getNetworkChannel().getGst();
							oneDayCharge = price / monthDays;
							balance = days * oneDayCharge;
							customerLedgres.add(buildCustomerLedgre(customer, Action.CHANNEL_PAYMENT_START_DISCOUNT,
									Math.abs(balance), days > 0 ? CreditDebit.DEBIT : CreditDebit.CREDIT,
									dbCustomerSetTopBox, cnc, null));
						}
					}
				}
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
			CustomerSetTopBox customerSetTopBox, CustomerNetworkChannel custpomerNetworkChannel, String reason) {
		boolean isPrepaid = customerSetTopBox.getPaymentMode().equals(PaymentMode.PREPAID);
		if(creditDebit.equals(CreditDebit.CREDIT)) {
			customer.setAmountCreditTemp(customer.getAmountCreditTemp() + price);
		} else {
			customer.setAmountDebitTemp(customer.getAmountDebitTemp() + price);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(customerSetTopBox.getEntryDate());
		
		return CustomerLedgre.builder()
				.action(action)
				.amount(round(price, 2))
				.createdAt(Instant.now())
				.month(Month.of(cal.get(Calendar.MONTH)).toString())
				.creditDebit(creditDebit)
				.customer(customer)
				.customerSetTopBox(customerSetTopBox)
				.customerNetworkChannel(custpomerNetworkChannel)
				.reason(reason)
				.isOnHold(isPrepaid ? false: true)
				.customerLedgreEntry(CustomerLedgreEntry.SOFTWARE)
				.build();
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
			@RequestParam("networkChannelId") Long networkChannelId,
			@RequestParam("entryDate") @DateTimeFormat(pattern = "yyyy/MM/dd") Date entryDate,
			@RequestParam("paymentStartDate") @DateTimeFormat(pattern = "yyyy/MM/dd") Date paymentStartDate) {
		Customer customer = customerRepository.getOne(customemerId);
		if (customer != null) {
			List<CustomerLedgre> customerLedgres = new ArrayList<>();
			CustomerSetTopBox cstb = customer.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getId().longValue() == customerSetTopBoxId.longValue()).findFirst().get();
			Optional<CustomerNetworkChannel> customerNetworkChannel = cstb.getCustomerNetworkChannels().stream()
					.filter(nc -> nc.getId().longValue() == networkChannelId.longValue()).findAny();
			if (customerNetworkChannel == null || !customerNetworkChannel.isPresent()) {
				NetworkChannel networkChannel = networkChannelRepository.getOne(networkChannelId);
				Double price = networkChannel.getMonthlyRent() + networkChannel.getGst();
				CustomerNetworkChannel cnc = customerNetworkChannelRepository.save(CustomerNetworkChannel.builder()
						.networkChannel(NetworkChannel.builder().id(networkChannelId).build())
						.paymentStartDate(paymentStartDate).build());
				cstb.getCustomerNetworkChannels().add(cnc);
				if(cstb.getPaymentMode().equals(PaymentMode.PREPAID)) {
					customerLedgres.add(
							buildCustomerLedgre(customer, Action.MONTHLY_CHANNEL_PRICE, price, CreditDebit.DEBIT, cstb, cnc, null));
				}
				CustomerLedgre customerDiscountForNewTransaction = customerDiscountForAddChannel(cstb.getPaymentMode(),
						entryDate, cstb.getBillingCycle(), paymentStartDate, customer,
						Action.CHANNEL_PAYMENT_START_DISCOUNT, price, cstb);
				if (customerDiscountForNewTransaction != null) {
					customerLedgres.add(customerDiscountForNewTransaction);
				}
				customerLedgreRepository.saveAll(customerLedgres);
				saveCustomer(customer);
				URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
				final HttpHeaders headers = new HttpHeaders();
				headers.put("Location", singletonList(uri.toASCIIString()));
				return new ResponseEntity<>(headers, HttpStatus.CREATED);
			}
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	private CustomerLedgre customerDiscountForAddChannel(PaymentMode paymentMode, Date entryDate, Date billingDate,
			Date paymentStartDate, Customer customer, Action action, Double packPrice,
			CustomerSetTopBox customerSetTopBox) {
		boolean isPrepaid = paymentMode.equals(PaymentMode.PREPAID);
		LocalDate ed = getLocalDate(entryDate);
		LocalDate bd = isPrepaid ? ed.withDayOfMonth(1) : getLocalDate(billingDate);
		LocalDate psd = getLocalDate(paymentStartDate);
		long days = Duration.between(bd.atStartOfDay(), psd.atStartOfDay()).toDays();
		if (days != 0) {
			Double oneDayCharge = packPrice / ed.lengthOfMonth();
			Double balance = days * oneDayCharge;
			return buildCustomerLedgre(customer, action, Math.abs(balance),
					days > 0 ? CreditDebit.CREDIT : CreditDebit.DEBIT, customerSetTopBox, null, null);
		}
		return null;
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
			dbCstb.setDeleted(true);
			customerSetTopBoxRepository.save(dbCstb);
			updateSetTopBoxStatus(dbCstb.getSetTopBox().getId(), removeCustomerSetTopBox.getSetTopBoxStatus(),
					removeCustomerSetTopBox.getReason());
			if (removeCustomerSetTopBox.getAmount() != null && removeCustomerSetTopBox.getAmount().doubleValue() > 0) {
				customerLedgreRepository.save(buildCustomerLedgre(customer, Action.SET_TOP_BOX_REMOVE,
						removeCustomerSetTopBox.getAmount(), CreditDebit.CREDIT, dbCstb, null, null));
			}
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/addAdditionalDiscount/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> addAdditionalDiscount(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute RemoveCustomerSetTopBox removeCustomerSetTopBox) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			if (removeCustomerSetTopBox.getAmount() != null && removeCustomerSetTopBox.getAmount().doubleValue() > 0) {
				customerLedgreRepository.save(
						buildCustomerLedgre(customer, Action.ADDITIONAL_DISCOUNT, removeCustomerSetTopBox.getAmount(),
								CreditDebit.CREDIT, null, null, removeCustomerSetTopBox.getReason()));
			}
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/addAdditionalCharge/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> addAdditionalCharges(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute RemoveCustomerSetTopBox removeCustomerSetTopBox) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			if (removeCustomerSetTopBox.getAmount() != null && removeCustomerSetTopBox.getAmount().doubleValue() > 0) {
				customerLedgreRepository.save(
						buildCustomerLedgre(customer, Action.ADDITIONAL_CHARGE, removeCustomerSetTopBox.getAmount(),
								CreditDebit.DEBIT, null, null, removeCustomerSetTopBox.getReason()));
			}
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/removeCustomeNetworkChannel/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> removeCustomerNetworkChannel(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute RemoveCustomerNetworkChannel removeCustomerNetworkChannel) {
		CustomerNetworkChannel cnc = customerNetworkChannelRepository.getOne(removeCustomerNetworkChannel.getId());

		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			List<CustomerLedgre> customerLedgres = new ArrayList<>();
			CustomerSetTopBox cstb = customer.getCustomerSetTopBoxes().stream().filter(
					c -> c.getId().longValue() == removeCustomerNetworkChannel.getCustomerSetTopBoxId().longValue())
					.findFirst().get();
			Optional<CustomerNetworkChannel> customerNetworkChannel = cstb.getCustomerNetworkChannels().stream()
					.filter(nc -> nc.getId().longValue() == removeCustomerNetworkChannel.getId().longValue()).findAny();
			if (customerNetworkChannel != null && customerNetworkChannel.isPresent()) {

				Double price = cnc.getNetworkChannel().getMonthlyRent() + cnc.getNetworkChannel().getGst();
				cnc.setDeleted(true);
				customerNetworkChannelRepository.save(cnc);
				// customerRepository.save(customer);
				CustomerLedgre customerDiscountForNewTransaction = customerDiscountForRemoveChannelOrActiveDeactiveSetTopBox(
						cstb.getPaymentMode(), cstb.getBillingCycle(),
						removeCustomerNetworkChannel.getChannelRemoveDate(), customer, Action.CHANNEL_REMOVE_DISCOUNT,
						price, cstb, CreditDebit.CREDIT);
				if (customerDiscountForNewTransaction != null) {
					customerLedgres.add(customerDiscountForNewTransaction);
				}
				customerLedgreRepository.saveAll(customerLedgres);
				saveCustomer(customer);
				URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
				final HttpHeaders headers = new HttpHeaders();
				headers.put("Location", singletonList(uri.toASCIIString()));
				return new ResponseEntity<>(headers, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/activateSetTopBox/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> activateSetTopBox(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute SetTopBoxActivateDeactivate setTopBoxActivateDeavtivate) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			CustomerSetTopBox dbCstb = customerSetTopBoxRepository.getOne(setTopBoxActivateDeavtivate.getCustomerSetTopBoxId());
			dbCstb.getSetTopBox().setUpdatedAt(Instant.now());
			dbCstb.setUpdatedAt(Instant.now());
			dbCstb.setActivateDate(setTopBoxActivateDeavtivate.getDate());
			dbCstb.setActive(true);
			dbCstb.setActivateReason(setTopBoxActivateDeavtivate.getReason());
			customerSetTopBoxRepository.save(dbCstb);
			Double price = dbCstb.getPackPrice();
			if (dbCstb.getCustomerNetworkChannels() != null
					&& !CollectionUtils.isEmpty(dbCstb.getCustomerNetworkChannels())) {
				for (CustomerNetworkChannel cnc : dbCstb.getCustomerNetworkChannels()) {
					if (!cnc.isDeleted()) {
						price += cnc.getNetworkChannel().getMonthlyRent() + cnc.getNetworkChannel().getGst();
					}
				}
			}
			CustomerLedgre customerLedgre = customerDiscountForRemoveChannelOrActiveDeactiveSetTopBox(dbCstb.getPaymentMode(), dbCstb.getBillingCycle(),
					setTopBoxActivateDeavtivate.getDate(), customer, Action.SET_TOP_BOX_ACTIVE, price, dbCstb,
					CreditDebit.DEBIT);
			if(customerLedgre != null) {
				customerLedgre.setActivateDate(setTopBoxActivateDeavtivate.getDate());
				customerLedgreRepository.save(customerLedgre);
			}
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@RequestMapping(value = "/replaceSetTopBox/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> replaceSetTopBox(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute SetTopBoxReplacement setTopBoxReplacement) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			
			CustomerSetTopBox cstb = customer.getCustomerSetTopBoxes().stream()
					.filter(c -> c.getId().longValue() == setTopBoxReplacement.getCustomerSetTopBoxId().longValue())
					.findFirst().get();
			Optional<SetTopBoxReplacement> setTopBoxReplacementObj = cstb.getCustomerSetTopBoxReplacements().stream()
					.filter(nc -> nc.getId().longValue() == setTopBoxReplacement.getId().longValue()).findAny();
			if (setTopBoxReplacementObj == null || !setTopBoxReplacementObj.isPresent()) {
				SetTopBoxReplacement stbr = setTopBoxReplacementRepository.save(setTopBoxReplacement);
				cstb.getCustomerSetTopBoxReplacements().add(stbr);
				cstb.setSetTopBox(setTopBoxReplacement.getReplacedSetTopBox());
				cstb.setUpdatedAt(Instant.now());
				if (setTopBoxReplacement.getReplacementCharge() != null
						&& setTopBoxReplacement.getReplacementCharge() > 0) {
					customerLedgreRepository.save(buildCustomerLedgre(customer, Action.SET_TOP_BOX_REPLACEMENT,
							Math.abs(setTopBoxReplacement.getReplacementCharge()), CreditDebit.DEBIT, cstb, null,
							null));
				}
				saveCustomer(customer);
				updateSetTopBoxStatus(setTopBoxReplacement.getOldSetTopBox().getId(),
						setTopBoxReplacement.getReplacementReason(),
						setTopBoxReplacement.getReplacementReason().toString());
				updateSetTopBoxStatus(setTopBoxReplacement.getReplacedSetTopBox().getId(), SetTopBoxStatus.ALLOTED,
						"Assigned To Customer");
				URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
				final HttpHeaders headers = new HttpHeaders();
				headers.put("Location", singletonList(uri.toASCIIString()));
				return new ResponseEntity<>(headers, HttpStatus.CREATED);
			}
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@RequestMapping(value = "/deActivateSetTopBox/{id}", method = POST)
	@Transactional
	public ResponseEntity<String> deActivateSetTopBox(@PathVariable("id") Long id, HttpServletRequest request,
			@ModelAttribute SetTopBoxActivateDeactivate setTopBoxActivateDeavtivate) {
		Customer customer = customerRepository.getOne(id);
		if (customer != null) {
			CustomerSetTopBox dbCstb = customerSetTopBoxRepository.getOne(setTopBoxActivateDeavtivate.getCustomerSetTopBoxId());
			dbCstb.getSetTopBox().setUpdatedAt(Instant.now());
			dbCstb.setUpdatedAt(Instant.now());
			dbCstb.setDeactivateDate(setTopBoxActivateDeavtivate.getDate());
			dbCstb.setDeactivateReason(setTopBoxActivateDeavtivate.getReason());
			dbCstb.setActive(false);
			customerSetTopBoxRepository.save(dbCstb);
			Double price = dbCstb.getPackPrice();
			if (dbCstb.getCustomerNetworkChannels() != null
					&& !CollectionUtils.isEmpty(dbCstb.getCustomerNetworkChannels())) {
				for (CustomerNetworkChannel cnc : dbCstb.getCustomerNetworkChannels()) {
					if (!cnc.isDeleted()) {
						price += cnc.getNetworkChannel().getMonthlyRent() + cnc.getNetworkChannel().getGst();
					}
				}
			}
			CustomerLedgre customerLedgre = customerDiscountForRemoveChannelOrActiveDeactiveSetTopBox(dbCstb.getPaymentMode(), dbCstb.getBillingCycle(),
					setTopBoxActivateDeavtivate.getDate(), customer, Action.SET_TOP_BOX_DEACTIVE, price, dbCstb,
					CreditDebit.CREDIT);
			if(customerLedgre != null) {
				customerLedgre.setDeactivateDate(setTopBoxActivateDeavtivate.getDate());
				customerLedgreRepository.save(customerLedgre);
			}
			saveCustomer(customer);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/getCustomerCode")
	public @ResponseBody Long getCustomerCode() {
		return customerRepository.count();
	}
	
	@GetMapping("/getAllCustomers")
	public @ResponseBody Map<Long, Customer> getAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		return customers.stream().filter(n -> n != null)
				.collect(Collectors.toMap(Customer::getId, c -> c));
	}
	
	private CustomerLedgre customerDiscountForRemoveChannelOrActiveDeactiveSetTopBox(PaymentMode paymentMode,
			Date billingDate, Date packRemoveDate, Customer customer, Action action, Double packPrice,
			CustomerSetTopBox customerSetTopBox, CreditDebit creditDebit) {
		boolean isPrepaid = paymentMode.equals(PaymentMode.PREPAID);

		LocalDate rd = getLocalDate(packRemoveDate);

		LocalDate bd = isPrepaid ? rd.withDayOfMonth(rd.lengthOfMonth())
				: getLocalDate(billingDate).withMonth(rd.getMonthValue())/*.plusMonths(1)*/;

		long days = Duration.between(rd.atStartOfDay(), bd.atStartOfDay()).toDays();
		if (days != 0) {
			Double oneDayCharge = packPrice / rd.lengthOfMonth();
			Double balance = days * oneDayCharge;
			return buildCustomerLedgre(customer, action, Math.abs(balance), creditDebit, customerSetTopBox, null, null);
		}
		return null;
	}

	private Customer saveCustomer(Customer customer) {
		customer.setBalance((customer.getAmountCredit() + customer.getAmountCreditTemp())
				- (customer.getAmountDebit() + customer.getAmountDebitTemp()));
		customer.setAmountCredit(customer.getAmountCredit() + customer.getAmountCreditTemp());
		customer.setAmountDebit(customer.getAmountDebit() + customer.getAmountDebitTemp());
		return customerRepository.save(customer);
	}

	private Area parseArea(CSVRecord record){
		return Area.builder()
			.name(record.get("area name"))
			.name2(record.get("area name2"))
			.areaCode(record.get("area code"))
			.lcoCode(record.get("lco code"))
			.lcoName(record.get("lco name"))
			.build();
	}

	public static final String CUSTOMER_SETOP_BOX_COLUMN_FORMAT = "{index} setop box {name}";

	private String getCustomerSetopBoxColumnName(String column, int index) {
		return CUSTOMER_SETOP_BOX_COLUMN_FORMAT.replace("{index}", String.valueOf(index)).replace("{name}", column);
	}

	private List<CustomerSetTopBox> parseCustomerSetopBox(CSVRecord record) {
		int index =1;
		List<CustomerSetTopBox> customerSetTopBoxes = new ArrayList<>();
		while (record.isMapped(getCustomerSetopBoxColumnName("payment mode", index))){
			try {
				customerSetTopBoxes.add(CustomerSetTopBox.builder()
					.paymentMode(PaymentMode.valueOf(record.get(getCustomerSetopBoxColumnName("payment mode", index))))
					.activateDate(DATE_FORMATTER.parse(record.get(getCustomerSetopBoxColumnName("activation date", index))))
					.activateReason(record.get(getCustomerSetopBoxColumnName("activation reason", index)))
					.billingCycle(DATE_FORMATTER.parse(record.get(getCustomerSetopBoxColumnName("billing Cycle", index))))
					.customerSetTopBoxStatus(CustomerSetTopBoxStatus.valueOf(record.get(getCustomerSetopBoxColumnName("status", index))))
					.deactivateDate(DATE_FORMATTER.parse(record.get(getCustomerSetopBoxColumnName("deactivation date", index))))
					.deactivateReason(record.get(getCustomerSetopBoxColumnName("deactivation reason", index)))
					.discount(Double.valueOf(record.get(getCustomerSetopBoxColumnName("discount", index))))
					.discountFrequency(DiscountFrequency.valueOf(record.get(getCustomerSetopBoxColumnName("discount frequency", index))))
					.isActive(Boolean.valueOf(record.get(getCustomerSetopBoxColumnName("active", index))))
					.openingBalance(Double.valueOf(record.get(getCustomerSetopBoxColumnName("opening balance", index))))
					.entryDate(DATE_FORMATTER.parse(record.get(getCustomerSetopBoxColumnName("entry date", index))))
					.pack(Pack.builder()
						.name(record.get(getCustomerSetopBoxColumnName("pack name", index)))
						.build())
					.packPrice(Double.valueOf(record.get(getCustomerSetopBoxColumnName("pack price", index))))
					.packPriceDifference(Double.valueOf(record.get(getCustomerSetopBoxColumnName("pack price difference", index))))
					.paymentStartDate(DATE_FORMATTER.parse(record.get(getCustomerSetopBoxColumnName("payment start date", index))))
					.setTopBox(SetTopBox.builder()
						.setTopBoxNumber(record.get(getCustomerSetopBoxColumnName("number", index)))
						.cardNumber(record.get(getCustomerSetopBoxColumnName("card number", index)))
						.safeCode(record.get(getCustomerSetopBoxColumnName("safe code", index)))
						.build())
					.setTopBoxPrice(Double.valueOf(record.get(getCustomerSetopBoxColumnName("price", index))))
					.build());
				index++;
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		return customerSetTopBoxes;
	}

	@PostMapping("/uploadCustomerFile")
	public String customerFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
		throws IOException {

		if (file.isEmpty()) {
			redirectAttributes.addAttribute("message", "Please select a file to upload");
			return "redirect:setTopBox";
		}

		Reader in = new InputStreamReader(file.getInputStream());
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().withIgnoreHeaderCase().parse(in);
		List<Customer> customers = new ArrayList<>();
			records.forEach(customer -> {
				customers.add(Customer.builder()
					.name(customer.get("name"))
					.address(customer.get("address"))
					.amountCredit(Double.valueOf(customer.get("amount credit")))
					.amountDebit(Double.valueOf(customer.get("amount debit")))
					.balance(Double.valueOf(customer.get("balance")))
					.city(customer.get("city"))
					.customerCode(customer.get("code"))
					.customerType(CustomerType.builder()
						.
						.build()
					)
					.area(parseArea(customer))
					.customerSetTopBoxes(parseCustomerSetopBox(customer))
					.build());
			});
//		for (CSVRecord record : records) {
//			String id = record.get("ID");
//			String customerNo = record.get("CustomerNo");
//			String name = record.get("Name");
//		}
//		br = new BufferedReader(new InputStreamReader(is));
//		boolean isFirstLineRead = false;
//		int setTopBoxNumberIndex = -1;
//		int cardNumberIndex = -1;
//		int safeCodeIndex = -1;
//		while ((line = br.readLine()) != null) {
//			String[] setTopBoxDetails = line.split(",");
//			if (!isFirstLineRead) {
//				for (int i = 0; i < setTopBoxDetails.length; i++) {
//					if (setTopBoxDetails[i].trim().equalsIgnoreCase("Set Top Box Number")) {
//						setTopBoxNumberIndex = i;
//					} else if (setTopBoxDetails[i].trim().equalsIgnoreCase("Card Number")) {
//						cardNumberIndex = i;
//					} else if (setTopBoxDetails[i].trim().equalsIgnoreCase("Safe Code")) {
//						safeCodeIndex = i;
//					}
//				}
//				isFirstLineRead = true;
//				continue;
//			}
//			if (isFirstLineRead && setTopBoxDetails.length > 1) {
//				customers.add(SetTopBox.builder().setTopBoxNumber(setTopBoxDetails[setTopBoxNumberIndex].trim())
//					.cardNumber(setTopBoxDetails[cardNumberIndex].trim())
//					.safeCode(setTopBoxDetails[safeCodeIndex].trim()).createdAt(Instant.now())
//					.setTopBoxStatus(SetTopBoxStatus.FREE).build());
//			}
//		}
		List<SetTopBox> errorSetTopBoxes = new ArrayList<>();
//		for (Customer stb : customers) {
//			try {
//				setTopBoxRepository.save(stb);
//			} catch (Exception e) {
//				errorSetTopBoxes.add(stb);
//			}
//		}
		if(!CollectionUtils.isEmpty(errorSetTopBoxes)) {
			redirectAttributes.addAttribute("totalElements", customers.size());
			redirectAttributes.addAttribute("savedElements", customers.size() - errorSetTopBoxes.size());
			String errorSetTopBoxesString = errorSetTopBoxes.stream().map(SetTopBox::getSetTopBoxNumber).collect(Collectors.joining( "," ));
			redirectAttributes.addAttribute("errorSetTopBoxes", errorSetTopBoxesString);
			//redirectAttributes.addAttribute("partialDataSaved", errorSetTopBoxes);
		} else {
			redirectAttributes.addAttribute("message",
				"You successfully uploaded '" + file.getOriginalFilename() + "'");
		}
		return "redirect:/setTopBox";
	}
	
}
