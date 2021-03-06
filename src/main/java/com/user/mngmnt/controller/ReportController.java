package com.user.mngmnt.controller;

import static com.user.mngmnt.utils.CalcUtils.round;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.user.mngmnt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.PlanChangeControlRepository;
import com.user.mngmnt.repository.ReportsRepository;
import com.user.mngmnt.util.ExcelUtils;

@Controller
public class ReportController {

	// private CustomerLedgreRepository customerLedgreRepository;
	@Autowired
	private GenericRepository genericRepository;
	
	@Autowired
	private ReportsRepository reportsRepository;
	
	@Autowired
	private PlanChangeControlRepository planChangeControlRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	private static final Integer MAX_RECORDS = 1000000;
	
	@GetMapping("/customerReports")
	public String area() {
		return "customerReport";
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/customerReport")
	public @ResponseBody ViewPage<CustomerReport> listCustomerReports(
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
			@ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class,
				pageRequest);
		Integer count = genericRepository.findCountWithCriteria(resportSearchCriteria, Customer.class);
		List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers, resportSearchCriteria, false);
		return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/deactivateCustomerSetTopBox")
	public @ResponseBody ResponseEntity<ResponseHandler> deactiveCustomerSetTopBoxes(
			@ModelAttribute ReportSearchCriteria resportSearchCriteria, HttpServletResponse response)
			throws ParseException, NoSuchFieldException, IOException {
		List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class, null);
		for (Customer c : customers) {
			List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes().stream()
					.filter(box -> CustomerSetTopBoxStatus.ACTIVE.equals(box.getCustomerSetTopBoxStatus()))
					.filter(box -> !box.isDeleted()).collect(Collectors.toList());
			for (CustomerSetTopBox dbCstb : customerSetTopBoxes) {
				deActivateSetTopBoxes(dbCstb);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void deActivateSetTopBoxes(CustomerSetTopBox dbCstb) {
		planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.DEACTIVATE)
				.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber()).build());

		planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.REMOVE)
				.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber()).plans(dbCstb.getPack().getName())
				.listName("SUGGESTIVE PACKS").build());

		dbCstb.getCustomerNetworkChannels().stream().forEach(cnc -> {
			planChangeControlRepository.save(PlanChangeControl.builder().action(PlanChangeControlAction.REMOVE)
					.serialNumber(dbCstb.getSetTopBox().getSetTopBoxNumber())
					.plans(cnc.getNetworkChannel().getName())
					.listName(cnc.getNetworkChannel().getNetwork().getName()).build());
		});
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/downloadCustomerReport")
	public ResponseEntity<InputStreamResource> downloadCustomerReport(@ModelAttribute ReportSearchCriteria resportSearchCriteria,
			HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
		List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class, null);
		List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers, resportSearchCriteria, false);
		List<CustomerReportColumns> cutomerReportColumns = new ArrayList<>();
		for(CustomerReport cr: cusstomerReports) {
			cutomerReportColumns.add(
			CustomerReportColumns.builder().status(cr.getStatus())
			.customerName(cr.getCustomer().getName())
			.customerCode(cr.getCustomer().getCustomerCode())
			.area(cr.getCustomer().getArea().getName())
			.street(cr.getCustomer().getStreet().getStreetNumber())
			.subArea(cr.getCustomer().getSubArea().getWardNumber())
			.address(cr.getCustomer().getAddress())
			.mobile(cr.getCustomer().getMobile())
			.monthlyTotal(cr.getMonthlyTotal())
			.channelTotal(cr.getChannelTotal())
			.totalSetTopBoxes(cr.getTotalSetTopBoxes())
			.totalChannels(cr.getTotalChannels())
			.setTopBoxes(cr.getSetTopBoxes())
			.networkChannels(Arrays.asList(cr.getNetworkChannels().split(",")))
			.outstanding(cr.getCustomer().getBalance())
			.build());
		}
		
		ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(cutomerReportColumns);
		HttpHeaders headers = new HttpHeaders();
	    // set filename in header
	    headers.add("Content-Disposition", "attachment; filename=CustomerReport.xlsx");
	    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}

	private List<CustomerReport> mapCustomerToCustomerReport(List<Customer> customers, ReportSearchCriteria resportSearchCriteria, boolean isPartial) {
		return customers.stream().map(c -> {
			
			List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes().stream()
                    .filter(box -> !box.isDeleted()).collect(Collectors.toList());

			customerSetTopBoxes = getCustomerSetTopBoxes(resportSearchCriteria, customerSetTopBoxes);

			Double sumMonthlyRent = customerSetTopBoxes.stream()
                    .map(box -> box.getPackPrice()).reduce(0.0, Double::sum);
            
			boolean isActive = customerSetTopBoxes.size() > 0 ? customerSetTopBoxes.stream()
					.anyMatch(stb -> CustomerSetTopBoxStatus.ACTIVE.equals(stb.getCustomerSetTopBoxStatus())) : true;
			Double networkChannelPrice = 0.0;
			Integer networkChannelsCount = 0;
			String networks = "";
			String setTopBoxes = "";
			for (CustomerSetTopBox cstb : customerSetTopBoxes) {
				setTopBoxes = setTopBoxes.concat(cstb.getSetTopBox().getSetTopBoxNumber()).concat(", ");
				Set<CustomerNetworkChannel> customerNetworkChannels = cstb.getCustomerNetworkChannels();
				networkChannelsCount += customerNetworkChannels.size();

				List<CustomerNetworkChannel> cncs = customerNetworkChannels.stream().filter(nc -> !nc.isDeleted()).collect(Collectors.toList());
				networks = networks.concat(cncs.stream().map(nc -> nc.getNetworkChannel().getName()).collect(Collectors.joining(",")));

				networkChannelPrice += cncs.stream().map(nc -> nc.getNetworkChannel().getTotal()).reduce(0.0, Double::sum);
			}
			Double partialPayment = 0.0;
			String creditOrDebit = "";
			if(isPartial) {
			    partialPayment = c.getAmountCredit() - c.getMonthlyTotal();
			    
			    if(partialPayment > 0) {
			        creditOrDebit = "Dr";
			    } else if (partialPayment < 0) {
			        creditOrDebit = "Cr";
			    }
			}
			
			return CustomerReport.builder().status(isActive ? "ACTIVE" : "DEACTIVE").customer(c)
					.monthlyTotal(sumMonthlyRent).channelTotal(networkChannelPrice)
					.balance(Math.abs(round(partialPayment, 2)))
					.creditOrDebit(creditOrDebit)
					.setTopBoxes(setTopBoxes)
					.networkChannels(networks)
					.totalSetTopBoxes(customerSetTopBoxes.size()).totalChannels(networkChannelsCount).build();
		}).collect(Collectors.toList());
	}
	
	@GetMapping("/customerOutstandingReports")
    public String customerOutstandingReports() {
        return "customerOutstandingReport";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/customerOutstandingReport")
    public @ResponseBody ViewPage<CustomerReport> customerOutstandingReport(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
            @ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class,
                pageRequest);
        Integer count = genericRepository.findCountWithCriteria(resportSearchCriteria, Customer.class);
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerOutstandingReport(customers, resportSearchCriteria);
        return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }
    
    private List<CustomerReport> mapCustomerToCustomerOutstandingReport(List<Customer> customers,
            ReportSearchCriteria resportSearchCriteria) {
        List<CustomerReport> customerReports = new ArrayList<>();

        for (Customer c : customers) {
            List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes();
			customerSetTopBoxes = getCustomerSetTopBoxes(resportSearchCriteria, customerSetTopBoxes);

			boolean isActive = customerSetTopBoxes.size() > 0 ? customerSetTopBoxes.stream()
                    .anyMatch(stb -> CustomerSetTopBoxStatus.ACTIVE.equals(stb.getCustomerSetTopBoxStatus())) : true;
            for (CustomerSetTopBox cstb : customerSetTopBoxes) {
                Set<CustomerNetworkChannel> customerNetworkChannels = cstb.getCustomerNetworkChannels();
                customerNetworkChannels.size();
                List<CustomerNetworkChannel> cncs = customerNetworkChannels.stream().filter(nc -> !nc.isDeleted()).collect(Collectors.toList());
                Double networkChannelPrice = cncs.stream()
                        .map(nc -> nc.getNetworkChannel().getTotal()).reduce(0.0, Double::sum);
                String networks = cncs.stream().map(nc -> nc.getNetworkChannel().getName()).collect(Collectors.joining(","));
                        
                customerReports.add(CustomerReport.builder().status(isActive ? "ACTIVE" : "DEACTIVE").customer(c)
                        .monthlyTotal(cstb.getPackPrice() + networkChannelPrice)
                        .networkChannels(networks)
                        .channelTotal(networkChannelPrice)
                        .totalSetTopBoxes(customerSetTopBoxes.size()).totalChannels(customerNetworkChannels.size())
                        .customerSetTopBox(cstb)
						.build());
            }
        }
        return customerReports;
    }

	private List<CustomerSetTopBox> getCustomerSetTopBoxes(ReportSearchCriteria resportSearchCriteria, List<CustomerSetTopBox> customerSetTopBoxes) {
		if (resportSearchCriteria.getPackPrice() != null) {
			customerSetTopBoxes = customerSetTopBoxes.stream().filter(
					cstb -> cstb.getPackPrice().doubleValue() == resportSearchCriteria.getPackPrice().doubleValue())
					.collect(Collectors.toList());
		}

		if (resportSearchCriteria.getPackId() != null) {
			customerSetTopBoxes = customerSetTopBoxes.stream().filter(
					cstb -> cstb.getPack().getId().longValue() == resportSearchCriteria.getPackId().doubleValue())
					.collect(Collectors.toList());
		}

		if (resportSearchCriteria.getCustomerStatus() != null) {
			customerSetTopBoxes = customerSetTopBoxes.stream().filter(cstb -> cstb.getCustomerSetTopBoxStatus()
					.toString().equals(resportSearchCriteria.getCustomerStatus())).collect(Collectors.toList());
		}
		return customerSetTopBoxes;
	}

	@SuppressWarnings("unchecked")
    @GetMapping("/downloadCustomerOutstandingReport")
    public ResponseEntity<InputStreamResource> downloadCustomerOutstandingReport(@ModelAttribute ReportSearchCriteria resportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class, null);
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerOutstandingReport(customers, resportSearchCriteria);
        List<CustomerOutstandingReportColumns> cutomerReportColumns = new ArrayList<>();
        for(CustomerReport cr: cusstomerReports) {
            cutomerReportColumns.add(
            CustomerOutstandingReportColumns.builder().status(cr.getStatus())
            .customerName(cr.getCustomer().getName())
            .customerCode(cr.getCustomer().getCustomerCode())
            .area(cr.getCustomer().getArea().getName())
            .street(cr.getCustomer().getStreet().getStreetNumber())
            .subArea(cr.getCustomer().getSubArea().getWardNumber())
            .address(cr.getCustomer().getAddress())
            .mobile(cr.getCustomer().getMobile())
            .monthlyTotal(cr.getMonthlyTotal()).channelTotal(cr.getChannelTotal())
            .totalSetTopBoxes(cr.getTotalSetTopBoxes()).totalChannels(cr.getTotalChannels())
            .outstanding(cr.getCustomer().getBalance())
			.entryDate(cr.getCustomerSetTopBox().getEntryDate())
			.setTopBoxNumber(cr.getCustomerSetTopBox().getSetTopBox().getSetTopBoxNumber())
			.pack(cr.getCustomerSetTopBox().getPack().getName())
            .networkChannels(Arrays.asList(cr.getNetworkChannels().split(",")))
            .build());
        }
        
        ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(cutomerReportColumns);
        HttpHeaders headers = new HttpHeaders();
        // set filename in header
        headers.add("Content-Disposition", "attachment; filename=CustomerOutstandingReport.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
    
    @GetMapping("/customerLedgreReports")
    public String customerLedgreReport() {
        return "customerLedgreReport";
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/customerLedgreReport")
    public @ResponseBody ViewPage<CustomerLedgreReport> customerLedgreReport(
            @ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        List<CustomerLedgreReport> customerLedgreReports = getCustomerLedgreRecords(resportSearchCriteria);
        return ViewPage.<CustomerLedgreReport>builder().rows(customerLedgreReports).build();
    }

    private List<CustomerLedgreReport> getCustomerLedgreRecords(ReportSearchCriteria resportSearchCriteria)
            throws ParseException, NoSuchFieldException {
        
        List<CustomerLedgreReport> customerLedgreReports = new ArrayList<>();
        if(resportSearchCriteria.getCustomerId() != null) {
        	resportSearchCriteria.setExcludeOnHold(true);
            PageRequest pageRequest = PageRequest.of(0, MAX_RECORDS, Direction.ASC, "createdAt");
            List<CustomerLedgre> customerLedgres = genericRepository.findAllWithCriteria(resportSearchCriteria, CustomerLedgre.class,
                    pageRequest);
            Double balance = 0.0;
            for(CustomerLedgre cl: customerLedgres) {
                balance += cl.getAmountCredit() - cl.getAmountDebit();
                String type = null;
                if(balance.doubleValue() == 0) {
                    type = "";
                } else {
                    type = balance < 0 ? "DR" : "CR";
                }
                customerLedgreReports.add(CustomerLedgreReport.builder()
                        .action(cl.getAction().toString())
                        .credit(cl.getAmountCredit())
                        .debit(cl.getAmountDebit())
                        .customerLedgreEntry(cl.getCustomerLedgreEntry().toString())
                        .setTopBoxNumber(cl.getCustomerSetTopBox() != null ? cl.getCustomerSetTopBox().getSetTopBox().getSetTopBoxNumber() : "")
                        .creditOrDebit(type)
                        .date(Date.from(cl.getCreatedAt()))
                        .balance(Math.abs(round(balance, 2)))
                        .build());
            }
        }
        return customerLedgreReports;
    }

    @GetMapping("/downloadCustomerLedgreReport")
    public ResponseEntity<InputStreamResource> downloadCustomerLedgreReport(@ModelAttribute ReportSearchCriteria resportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<CustomerLedgreReport> customerLedgreRecords = getCustomerLedgreRecords(resportSearchCriteria);
        ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(customerLedgreRecords);
        HttpHeaders headers = new HttpHeaders();
        // set filename in header
        headers.add("Content-Disposition", "attachment; filename=CustomerLedgreReport.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
    
    @GetMapping("/customerPartialPaymentReports")
    public String customerPartialPaymentReport() {
        return "customerPartialPaymentReport";
    }

    @GetMapping("/customerPartialPaymentReport")
    public @ResponseBody ViewPage<CustomerReport> listCustomerPartialPaymentReports(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
            @ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        List<Customer> customers = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        Integer count = 0;
        if(resportSearchCriteria.getStartDate() != null && resportSearchCriteria.getEndDate() != null) {
            customers = getPartialPaymentCustomers(resportSearchCriteria, pageRequest);
            count = getPartialPaymentCount(resportSearchCriteria);
        }
        
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers, resportSearchCriteria, true);
        return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }

	@GetMapping("/deactivateCustomerNoPaymentSetTopBox")
	public @ResponseBody ResponseEntity<ResponseHandler> deactiveCustomerOutstandingSetTopBoxes(
			@ModelAttribute ReportSearchCriteria resportSearchCriteria, HttpServletResponse response)
			throws ParseException, NoSuchFieldException, IOException {
    	List<Customer> customers = getPartialPaymentCustomers(resportSearchCriteria, null);
		for (Customer c : customers) {
			List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes().stream()
					.filter(box -> CustomerSetTopBoxStatus.ACTIVE.equals(box.getCustomerSetTopBoxStatus()))
					.filter(box -> !box.isDeleted()).collect(Collectors.toList());
			for (CustomerSetTopBox dbCstb : customerSetTopBoxes) {
				deActivateSetTopBoxes(dbCstb);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
    
    private List<Customer> getPartialPaymentCustomers(ReportSearchCriteria resportSearchCriteria, PageRequest pageRequest) {
        String sql = "select distinct sq.total_credit as amount_credit, c.* from customer_ledgre mq "
                + "inner join (select sum(amount_credit) as total_credit, customer_id from customer_ledgre "
                + "where is_on_hold = false and created_at>= ? and created_at <= ? group by customer_id) sq "
                + "on sq.customer_id = mq.customer_id join customer c on c.id = mq.customer_id "
                + "where ";
        if(resportSearchCriteria.isNoPaymentBetween()) {
            sql = sql.concat("total_credit = 0 ");
        } else {
            sql = sql.concat("total_credit < c.monthly_total or total_credit > c.monthly_total ");
        }
        if(resportSearchCriteria.getOutstandingValue() != null) {
        	if (resportSearchCriteria.getOutstandingValue().intValue() == 0) {
        		sql = sql.concat("and c.balance = 0 ");
            } else if(resportSearchCriteria.getOutstandingValue().intValue() < 0) {
            	sql = sql.concat("and c.balance < 0 ");
            } else {
            	sql = sql.concat("and c.balance > 0 ");
            }
        }
        return genericRepository.findAllWithSqlQuery(sql, Customer.class, getSqlQueryParamsForPartialPayment(resportSearchCriteria), pageRequest);
    }
    
    private Integer getPartialPaymentCount(ReportSearchCriteria resportSearchCriteria) {
        String countSql = "select count(distinct mq.customer_id) from customer_ledgre mq "
                + "inner join (select sum(amount_credit) as total_credit, customer_id from customer_ledgre "
                + "where is_on_hold = false and created_at>= ? and created_at <= ? group by customer_id) sq "
                + "on sq.customer_id = mq.customer_id join customer c on c.id = mq.customer_id "
                + "where ";
        if(resportSearchCriteria.isNoPaymentBetween()) {
            countSql = countSql.concat("total_credit = 0 ");
        } else {
            countSql = countSql.concat("total_credit < c.monthly_total or total_credit > c.monthly_total ");
        }
        return genericRepository.findCountWithSqlQuery(countSql, getSqlQueryParamsForPartialPayment(resportSearchCriteria));
    }
    
    private List<Object> getSqlQueryParamsForPartialPayment(ReportSearchCriteria resportSearchCriteria) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(resportSearchCriteria.getStartDate());
        parameters.add(resportSearchCriteria.getEndDate());
        return parameters;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadCustomerPartialPaymentReport")
    public ResponseEntity<InputStreamResource> downloadCustomerPartialPaymentReport(@ModelAttribute ReportSearchCriteria resportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<Customer> customers = getPartialPaymentCustomers(resportSearchCriteria, null);
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers, resportSearchCriteria, true);
        List<CustomerPartialPaymentColumns> cutomerReportColumns = new ArrayList<>();
        for(CustomerReport cr: cusstomerReports) {
            cutomerReportColumns.add(
             CustomerPartialPaymentColumns.builder().status(cr.getStatus())
            .customerName(cr.getCustomer().getName())
            .customerCode(cr.getCustomer().getCustomerCode())
            .area(cr.getCustomer().getArea().getName())
            .street(cr.getCustomer().getStreet().getStreetNumber())
            .subArea(cr.getCustomer().getSubArea().getWardNumber())
            .address(cr.getCustomer().getAddress())
            .mobile(cr.getCustomer().getMobile())
            .monthlyTotal(cr.getMonthlyTotal())
            .creditOrDebit(cr.getCreditOrDebit())
            .balance(cr.getBalance())
            .build());
        }
        
        ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(cutomerReportColumns);
        HttpHeaders headers = new HttpHeaders();
        // set filename in header
        headers.add("Content-Disposition", "attachment; filename=CustomerPartialPaymentReport.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
    
    
    @GetMapping("/paymentReceipts")
	public String paymentReceipt() {
		return "customerPaymentReceiptReport";
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/paymentReceipt")
	public @ResponseBody ViewPage<CustomerLedgre> listPaymentReceipt(
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
			@ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		resportSearchCriteria.setPaymentReceiptReport(true);
		List<CustomerLedgre> customerLedgres = genericRepository.findAllWithCriteria(resportSearchCriteria, CustomerLedgre.class,
				pageRequest);
		Integer count = genericRepository.findCountWithCriteria(resportSearchCriteria, CustomerLedgre.class);
		return ViewPage.<CustomerLedgre>builder().rows(customerLedgres).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/downloadPaymentReceiptReport")
	public ResponseEntity<InputStreamResource> downloadPaymentReceipt(@ModelAttribute ReportSearchCriteria resportSearchCriteria,
			HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
		List<CustomerLedgre> customerLedgres = genericRepository.findAllWithCriteria(resportSearchCriteria, CustomerLedgre.class,
				null);
		
		List<PaymentReceiptColumns> paymentReceiptColumns = customerLedgres.stream().map(cl -> PaymentReceiptColumns
				.builder()
				.customerName(cl.getCustomer().getName())
				.customerCode(cl.getCustomer().getCustomerCode())
				.area(cl.getCustomer().getArea().getName())
				.subArea(cl.getCustomer().getSubArea().getWardNumber())
				.street(cl.getCustomer().getStreet().getStreetNumber())
				.amount(cl.getAmountCredit())
				.chequeDate(cl.getChequeDate())
				.chequeNumber(cl.getChequeNumber())
				.paymentDate(cl.getPaymentDate())
				.paymentMode(cl.getPaymentMode().toString())
				.paymentType(cl.getPaymentType().toString())
				.build()).collect(Collectors.toList());
		
		ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(paymentReceiptColumns);
		HttpHeaders headers = new HttpHeaders();
	    // set filename in header
	    headers.add("Content-Disposition", "attachment; filename=PaymentReceiptReport.xlsx");
	    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}
    
	@GetMapping("/setTopBoxReplacementReports")
    public String setTopBoxReplacementReport() {
        return "setTopBoxReplacementReport";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/setTopBoxReplacementReport")
    public @ResponseBody ViewPage<SetTopBoxReplacement> listSetTopBoxReplacement(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
            @ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        List<SetTopBoxReplacement> setTopBoxReplacements = getReplacedSetTopBoxes(resportSearchCriteria, pageRequest);
        Integer count = getReplacedSetTopBoxesCount(resportSearchCriteria);
        return ViewPage.<SetTopBoxReplacement>builder().rows(setTopBoxReplacements).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }

    private List<SetTopBoxReplacement> getReplacedSetTopBoxes(ReportSearchCriteria reportSearchCriteria, PageRequest pageRequest) {
        String sql = "SELECT mo.* FROM set_top_box_replacement  mo join customer c on mo.replaced_for_customer_id = c.id "
                + "join area a on a.id = c.area_id "
                + "join sub_area sa on sa.id = c.sub_area_id "
                + "join street st on st.id = c.street_id where true";
        
        List<Object> parameters = new ArrayList<>();
        
        sql = addConditionsToReplacementSquery(reportSearchCriteria, sql, parameters);
        
        return genericRepository.findAllWithSqlQuery(sql, SetTopBoxReplacement.class, parameters, pageRequest);
    }

    private String addConditionsToReplacementSquery(ReportSearchCriteria reportSearchCriteria, String sql,
            List<Object> parameters) {
        if (reportSearchCriteria.getCustomerId() != null) {
            parameters.add(reportSearchCriteria.getCustomerId());
            sql = sql.concat(" and c.id = ?");
        }

        if (reportSearchCriteria.getAreaId() != null) {
            parameters.add(reportSearchCriteria.getAreaId());
            sql = sql.concat(" and a.id = ?");
        }

        if (reportSearchCriteria.getSubAreaId() != null) {
            parameters.add(reportSearchCriteria.getSubAreaId());
            sql = sql.concat(" and sa.id = ?");
        }

        if (reportSearchCriteria.getStreetId() != null) {
            parameters.add(reportSearchCriteria.getStreetId());
            sql = sql.concat(" and st.id = ?");
        }

        if (reportSearchCriteria.getReplacementReason() != null) {
            parameters.add(reportSearchCriteria.getReplacementReason());
            sql = sql.concat(" and mo.replacement_reason = ?");
        }

        if (reportSearchCriteria.getReplacementType() != null) {
            parameters.add(reportSearchCriteria.getReplacementType());
            sql = sql.concat(" and mo.replacement_type = ?");
        }

        if (reportSearchCriteria.getReplacementAmountStart() != null) {
            parameters.add(reportSearchCriteria.getReplacementAmountStart());
            sql = sql.concat(" and mo.replacement_charge >= ?");
        }

        if (reportSearchCriteria.getReplacementAmountEnd() != null) {
            parameters.add(reportSearchCriteria.getReplacementAmountEnd());
            sql = sql.concat(" and mo.replacement_charge <= ?");
        }
        
		if (reportSearchCriteria.getSetTopBoxStatus() != null) {
			if(reportSearchCriteria.getSetTopBoxStatus().equals("NOT_REACTIVATED")) {
				sql = sql.concat(" and mo.is_re_activated = false and mo.set_top_box_status = 'DE_ACTIVATE'");
			} else {
				parameters.add(reportSearchCriteria.getSetTopBoxStatus());
				sql = sql.concat(" and mo.set_top_box_status = ?");	
			}
		}

		if (reportSearchCriteria.getStartDate() != null) {
			parameters.add(reportSearchCriteria.getStartDate());
			sql = sql.concat(" and mo.date_time >= ?");
		}

		if (reportSearchCriteria.getEndDate() != null) {
			parameters.add(reportSearchCriteria.getEndDate());
			sql = sql.concat(" and mo.date_time <= ?");
		}
        
		if (reportSearchCriteria.getStart() != null) {
			parameters.add(reportSearchCriteria.getStart());
			sql = sql.concat(" and mo.date >= ?");
		}
		
		if (reportSearchCriteria.getEnd() != null) {
			parameters.add(reportSearchCriteria.getEnd());
			sql = sql.concat(" and mo.date <= ?");
		}
		
        return sql;
    }
    
    private Integer getReplacedSetTopBoxesCount(ReportSearchCriteria reportSearchCriteria) {
        String sql = "SELECT count(distinct mo.id) FROM set_top_box_replacement  mo join customer c on mo.replaced_for_customer_id = c.id "
                + "join area a on a.id = c.area_id "
                + "join sub_area sa on sa.id = c.sub_area_id "
                + "join street st on st.id = c.street_id where true";
        
        List<Object> parameters = new ArrayList<>();
        
        sql = addConditionsToReplacementSquery(reportSearchCriteria, sql, parameters);
        
        return genericRepository.findCountWithSqlQuery(sql, parameters);
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadSetTopBoxReplacementReport")
    public ResponseEntity<InputStreamResource> downloadSetTopBoxReplacementReport(@ModelAttribute ReportSearchCriteria reportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<SetTopBoxReplacement> setTopBoxReplacements = getReplacedSetTopBoxes(reportSearchCriteria, null);
        
        List<SetTopBoxReplacementColumns> setTopBoxReplacementsList = setTopBoxReplacements.stream().map(cl -> SetTopBoxReplacementColumns
                .builder()
                .customerName(cl.getReplacedForCustomer().getName())
                .customerCode(cl.getReplacedForCustomer().getCustomerCode())
                .area(cl.getReplacedForCustomer().getArea().getName())
                .subArea(cl.getReplacedForCustomer().getSubArea().getWardNumber())
                .street(cl.getReplacedForCustomer().getStreet().getStreetNumber())
                .replacementCharge(cl.getReplacementCharge())
                .replacementReason(cl.getReplacementReason().toString())
                .replacementType(cl.getReplacementType().toString())
                .oldSetTopBoxNumber(cl.getOldSetTopBox().getSetTopBoxNumber())
                .replacedSetTopBoxNumber(cl.getReplacedSetTopBox().getSetTopBoxNumber())
                .build()).collect(Collectors.toList());
        
        ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(setTopBoxReplacementsList);
        HttpHeaders headers = new HttpHeaders();
        // set filename in header
        headers.add("Content-Disposition", "attachment; filename=PaymentReceiptReport.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }
    
 
    @GetMapping("/setTopBoxActiveDeactiveReports")
    public String setTopBoxActiveDeactiveReports() {
        return "setTopBoxActiveDeactiveReport";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/setTopBoxActiveDeactiveReport")
    public @ResponseBody ViewPage<SetTopBoxActiveDeactiveColumns> listSetTopBoxBoxActiveDeactive(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
            @ModelAttribute ReportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        List<CustomerSetTopBoxHistory> activeDeactiveSetTopBoxes = getActiveDeactiveSetTopBoxes(resportSearchCriteria, pageRequest);
        List<SetTopBoxActiveDeactiveColumns> activeDeactiveSetTopBoxesList = buildSetTopBoxActiveDeactiveHistoryColumns(
				activeDeactiveSetTopBoxes);
        Integer count = getSetTopBoxActiveDeactiveCount(resportSearchCriteria);
        return ViewPage.<SetTopBoxActiveDeactiveColumns>builder().rows(activeDeactiveSetTopBoxesList).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }

    private List<CustomerSetTopBoxHistory> getActiveDeactiveSetTopBoxes(ReportSearchCriteria reportSearchCriteria, PageRequest pageRequest) {
        String sql = "SELECT mo.* FROM set_top_box_history mo join customer c on mo.customer_id = c.id "
                + "join area a on a.id = c.area_id "
                + "join sub_area sa on sa.id = c.sub_area_id "
                + "join street st on st.id = c.street_id where true";
        
        List<Object> parameters = new ArrayList<>();
        sql = addConditionsToReplacementSquery(reportSearchCriteria, sql, parameters);
        return genericRepository.findAllWithSqlQuery(sql, CustomerSetTopBoxHistory.class, parameters, pageRequest);
    }
    
    private Integer getSetTopBoxActiveDeactiveCount(ReportSearchCriteria reportSearchCriteria) {
        String sql = "SELECT count(distinct mo.id) FROM set_top_box_history  mo join customer c on mo.customer_id = c.id "
                + "join area a on a.id = c.area_id "
                + "join sub_area sa on sa.id = c.sub_area_id "
                + "join street st on st.id = c.street_id where true";
        List<Object> parameters = new ArrayList<>();
        sql = addConditionsToReplacementSquery(reportSearchCriteria, sql, parameters);
        return genericRepository.findCountWithSqlQuery(sql, parameters);
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadSetTopBoxActiveDeactiveReport")
    public ResponseEntity<InputStreamResource> downloadSetTopBoxActiveDeactiveReport(@ModelAttribute ReportSearchCriteria reportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<CustomerSetTopBoxHistory> activeDeactiveSetTopBoxes = getActiveDeactiveSetTopBoxes(reportSearchCriteria, null);
        
        List<SetTopBoxActiveDeactiveColumns> activeDeactiveSetTopBoxesList = buildSetTopBoxActiveDeactiveHistoryColumns(
				activeDeactiveSetTopBoxes);
        
        ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(activeDeactiveSetTopBoxesList);
        HttpHeaders headers = new HttpHeaders();
        // set filename in header
        headers.add("Content-Disposition", "attachment; filename=PaymentReceiptReport.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

	private List<SetTopBoxActiveDeactiveColumns> buildSetTopBoxActiveDeactiveHistoryColumns(
			List<CustomerSetTopBoxHistory> activeDeactiveSetTopBoxes) {
		List<SetTopBoxActiveDeactiveColumns> activeDeactiveSetTopBoxesList = activeDeactiveSetTopBoxes.stream().map(cl -> SetTopBoxActiveDeactiveColumns
                .builder()
                .customerName(cl.getCustomer().getName())
                .customerCode(cl.getCustomer().getCustomerCode())
                .area(cl.getCustomer().getArea().getName())
                .subArea(cl.getCustomer().getSubArea().getWardNumber())
                .street(cl.getCustomer().getStreet().getStreetNumber())
                .setTopBoxStatus(cl.getSetTopBoxStatus().toString())
                .dateTime(Date.from(cl.getDateTime()))
                .build()).collect(Collectors.toList());
		return activeDeactiveSetTopBoxesList;
	}
}
