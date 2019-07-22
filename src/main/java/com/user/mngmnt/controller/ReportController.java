package com.user.mngmnt.controller;

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
import static com.user.mngmnt.utils.CalcUtils.round;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerLedgreReport;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerPartialPaymentColumns;
import com.user.mngmnt.model.CustomerReport;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.CustomerReportColumns;
import com.user.mngmnt.model.ResportSearchCriteria;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.ReportsRepository;
import com.user.mngmnt.util.ExcelUtils;
import static com.user.mngmnt.utils.CalcUtils.round;

@Controller
public class ReportController {

	// private CustomerLedgreRepository customerLedgreRepository;
	@Autowired
	private GenericRepository genericRepository;
	
	@Autowired
	private ReportsRepository reportsRepository;

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
			@ModelAttribute ResportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class,
				pageRequest);
		Integer count = genericRepository.findCountWithCriteria(resportSearchCriteria, Customer.class);
		List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers, resportSearchCriteria, false);
		return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/downloadCustomerReport")
	public ResponseEntity<InputStreamResource> downloadCustomerReport(@ModelAttribute ResportSearchCriteria resportSearchCriteria,
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
			.outstanding(cr.getCustomer().getBalance())
			.build());
		}
		
		ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(cutomerReportColumns);
		HttpHeaders headers = new HttpHeaders();
	    // set filename in header
	    headers.add("Content-Disposition", "attachment; filename=CustomerReport.xlsx");
	    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}

	private List<CustomerReport> mapCustomerToCustomerReport(List<Customer> customers, ResportSearchCriteria resportSearchCriteria, boolean isPartial) {
		return customers.stream().map(c -> {
			
			List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes();
			
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
			
            Double sumMonthlyRent = customerSetTopBoxes.stream()
                    .filter(box -> CustomerSetTopBoxStatus.ACTIVE.equals(box.getCustomerSetTopBoxStatus()))
                    .map(box -> box.getPackPrice()).reduce(0.0, Double::sum);
            
			boolean isActive = customerSetTopBoxes.size() > 0 ? customerSetTopBoxes.stream()
					.anyMatch(stb -> CustomerSetTopBoxStatus.ACTIVE.equals(stb.getCustomerSetTopBoxStatus())) : true;
			Double networkChannelPrice = 0.0;
			Integer networkChannelsCount = 0;
			for (CustomerSetTopBox cstb : customerSetTopBoxes) {
				Set<CustomerNetworkChannel> customerNetworkChannels = cstb.getCustomerNetworkChannels();
				networkChannelsCount += customerNetworkChannels.size();
				networkChannelPrice += customerNetworkChannels.stream().filter(nc -> !nc.isDeleted())
						.map(nc -> nc.getNetworkChannel().getTotal()).reduce(0.0, Double::sum);
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
            @ModelAttribute ResportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
        List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class,
                pageRequest);
        Integer count = genericRepository.findCountWithCriteria(resportSearchCriteria, Customer.class);
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerOutstandingReport(customers, resportSearchCriteria);
        return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
                .page(pageRequest.getPageNumber() + 1).total(count).build();
    }
    
    private List<CustomerReport> mapCustomerToCustomerOutstandingReport(List<Customer> customers,
            ResportSearchCriteria resportSearchCriteria) {
        List<CustomerReport> customerReports = new ArrayList<>();

        for (Customer c : customers) {
            List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes();
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
                        .customerSetTopBox(cstb).build());
            }
        }
        return customerReports;
    }
    
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadCustomerOutstandingReport")
    public ResponseEntity<InputStreamResource> downloadCustomerOutstandingReport(@ModelAttribute ResportSearchCriteria resportSearchCriteria,
            HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
        List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class, null);
        List<CustomerReport> cusstomerReports = mapCustomerToCustomerOutstandingReport(customers, resportSearchCriteria);
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
            .monthlyTotal(cr.getMonthlyTotal()).channelTotal(cr.getChannelTotal())
            .totalSetTopBoxes(cr.getTotalSetTopBoxes()).totalChannels(cr.getTotalChannels())
            .outstanding(cr.getCustomer().getBalance())
            .networkChannels(Arrays.asList(cr.getNetworkChannels().split(",")))
            .entryDate(cr.getCustomerSetTopBox().getEntryDate())
            .setTopBoxNumber(cr.getCustomerSetTopBox().getSetTopBox().getSetTopBoxNumber())
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
            @ModelAttribute ResportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
        List<CustomerLedgreReport> customerLedgreReports = getCustomerLedgreRecords(resportSearchCriteria);
        return ViewPage.<CustomerLedgreReport>builder().rows(customerLedgreReports).build();
    }

    private List<CustomerLedgreReport> getCustomerLedgreRecords(ResportSearchCriteria resportSearchCriteria)
            throws ParseException, NoSuchFieldException {
        
        List<CustomerLedgreReport> customerLedgreReports = new ArrayList<>();
        if(resportSearchCriteria.getCustomerId() != null) {
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
                        .creditOrDebit(type)
                        .date(Date.from(cl.getCreatedAt()))
                        .balance(Math.abs(round(balance, 2)))
                        .build());
            }
        }
        return customerLedgreReports;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadCustomerLedgreReport")
    public ResponseEntity<InputStreamResource> downloadCustomerLedgreReport(@ModelAttribute ResportSearchCriteria resportSearchCriteria,
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

    @SuppressWarnings("unchecked")
    @GetMapping("/customerPartialPaymentReport")
    public @ResponseBody ViewPage<CustomerReport> listCustomerPartialPaymentReports(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "name", required = false) String sort,
            @ModelAttribute ResportSearchCriteria resportSearchCriteria) throws ParseException, NoSuchFieldException {
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

    private List<Customer> getPartialPaymentCustomers(ResportSearchCriteria resportSearchCriteria, PageRequest pageRequest) {
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
        return genericRepository.findAllWithSqlQuery(sql, Customer.class, getSqlQueryParamsForPartialPayment(resportSearchCriteria), pageRequest);
    }
    
    private Integer getPartialPaymentCount(ResportSearchCriteria resportSearchCriteria) {
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
    
    private List<Object> getSqlQueryParamsForPartialPayment(ResportSearchCriteria resportSearchCriteria) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(resportSearchCriteria.getStartDate());
        parameters.add(resportSearchCriteria.getEndDate());
        return parameters;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/downloadCustomerPartialPaymentReport")
    public ResponseEntity<InputStreamResource> downloadCustomerPartialPaymentReport(@ModelAttribute ResportSearchCriteria resportSearchCriteria,
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
    
}
