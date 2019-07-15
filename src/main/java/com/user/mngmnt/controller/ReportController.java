package com.user.mngmnt.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

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
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerReport;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.CutomerReportColumns;
import com.user.mngmnt.model.ResportSearchCriteria;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.util.ExcelUtils;

@Controller
public class ReportController {

	// private CustomerLedgreRepository customerLedgreRepository;
	@Autowired
	private GenericRepository genericRepository;

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
		List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers);
		return ViewPage.<CustomerReport>builder().rows(cusstomerReports).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/downloadCustomerReport")
	public ResponseEntity<InputStreamResource> downloadCustomerReport(@ModelAttribute ResportSearchCriteria resportSearchCriteria,
			HttpServletResponse response) throws ParseException, NoSuchFieldException, IOException {
		List<Customer> customers = genericRepository.findAllWithCriteria(resportSearchCriteria, Customer.class, null);
		List<CustomerReport> cusstomerReports = mapCustomerToCustomerReport(customers);
		List<CutomerReportColumns> cutomerReportColumns = new ArrayList<>();
		for(CustomerReport cr: cusstomerReports) {
			cutomerReportColumns.add(
			CutomerReportColumns.builder().status(cr.getStatus())
			.customerName(cr.getCustomer().getName())
			.customerCode(cr.getCustomer().getCustomerCode())
			.area(cr.getCustomer().getArea().getName())
			.street(cr.getCustomer().getStreet().getStreetNumber())
			.subArea(cr.getCustomer().getSubArea().getWardNumber())
			.address(cr.getCustomer().getAddress())
			.mobile(cr.getCustomer().getMobile())
			.monthlyTotal(cr.getMonthlyTotal()).channelTotal(cr.getChannelTotal())
			.totalSetTopBoxes(cr.getTotalSetTopBoxes()).totalChannels(cr.getTotalChannels()).build());
		}
		
		ByteArrayInputStream in = ExcelUtils.writeToExcelInMultiSheets(cutomerReportColumns);
		HttpHeaders headers = new HttpHeaders();
	    // set filename in header
	    headers.add("Content-Disposition", "attachment; filename=CustomerReport.xlsx");
	    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}

	private List<CustomerReport> mapCustomerToCustomerReport(List<Customer> customers) {
		return customers.stream().map(c -> {
			Double sumMonthlyRent = c.getCustomerSetTopBoxes().stream()
					.filter(box -> CustomerSetTopBoxStatus.ACTIVE.equals(box.getCustomerSetTopBoxStatus()))
					.map(box -> box.getPackPrice()).reduce(0.0, Double::sum);
			List<CustomerSetTopBox> customerSetTopBoxes = c.getCustomerSetTopBoxes();

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
			return CustomerReport.builder().status(isActive ? "ACTIVE" : "DEACTIVE").customer(c)
					.monthlyTotal(sumMonthlyRent).channelTotal(networkChannelPrice)
					.totalSetTopBoxes(customerSetTopBoxes.size()).totalChannels(networkChannelsCount).build();
		}).collect(Collectors.toList());
	}
	
}
