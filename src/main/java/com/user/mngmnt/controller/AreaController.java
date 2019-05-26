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
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.Area;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.SubArea;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.AreaRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.StreetRepository;
import com.user.mngmnt.repository.SubAreaRepository;

@Controller
public class AreaController {

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private SubAreaRepository subAreaRepository;

	@Autowired
	private StreetRepository streetRepository;

	@Autowired
	private GenericRepository genericRepository;

	@Value("${max.result.per.page}")
	private int maxResults;

	@Value("${max.card.display.on.pagination.tray}")
	private int maxPaginationTraySize;

	@GetMapping("/area")
	public String area() {
		return "areaManager";
	}

	@GetMapping("/areaManager")
	public ModelAndView addNewUser() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("area-manager");
		modelAndView.addObject("subArea", new SubArea());
		modelAndView.addObject("areaList", areaRepository.findAll());
		return modelAndView;
	}

	@PostMapping("/addArea")
	public String addArea(@ModelAttribute Area area) {
		String result = "redirect:/areaManager?success=Area Created Successfully!";
		Area dbArea = areaRepository.findByName(area.getName());
		if (dbArea == null) {
			areaRepository.save(area);
		} else {
			result = "redirect:/areaManager?error=Area Already Exists!";
		}
		return result;
	}

	@PostMapping("/addSubArea")
	public String addSubArea(@ModelAttribute SubArea subArea) {
		String result = "redirect:/areaManager?success=Sub Area Created Successfully!";
		subArea.setCreatedAt(Instant.now());
		subAreaRepository.save(subArea);
		return result;
	}

	@PostMapping("/addStreet")
	public String addStreet(@ModelAttribute Street street) {
		String result = "redirect:/areaManager?success=Sub Area Created Successfully!";
		street.setCreatedAt(Instant.now());
		streetRepository.save(street);
		return result;
	}

	@GetMapping("/allAreas")
	public @ResponseBody ViewPage<Area> listAreas(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredAreas(filters, pageRequest);
		}
		return new ViewPage<>(areaRepository.findAll(pageRequest));
	}

	public ViewPage<Area> getFilteredAreas(String filters, PageRequest pageRequest) throws ParseException {
		long count = areaRepository.count();
		List<Area> records = genericRepository.findAllWithCriteria(filters, Area.class, pageRequest);
		return ViewPage.<Area>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/area/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateArea(@PathVariable("id") Long id, @ModelAttribute Area area) {
		areaRepository.findById(id).ifPresent(n -> {
			area.setUpdatedAt(Instant.now());
			area.setId(n.getId());
			areaRepository.save(area);
		});
	}

	@RequestMapping(value = "/area", method = POST)
	public ResponseEntity<String> createArea(HttpServletRequest request, @ModelAttribute Area area) {
		if (areaRepository.findByName(area.getName()) == null) {
			area.setCreatedAt(Instant.now());
			Area dbArea = areaRepository.save(area);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(), dbArea.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allSubAreas")
	public @ResponseBody ViewPage<SubArea> listSubAreas(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "wardNumber", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredSubAreas(filters, pageRequest);
		}
		return new ViewPage<>(subAreaRepository.findAll(pageRequest));
	}

	public ViewPage<SubArea> getFilteredSubAreas(String filters, PageRequest pageRequest) throws ParseException {
		long count = subAreaRepository.count();
		List<SubArea> records = genericRepository.findAllWithCriteria(filters, SubArea.class, pageRequest);
		return ViewPage.<SubArea>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/subArea/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateSubArea(@PathVariable("id") Long id, @ModelAttribute SubArea subArea) {
		subAreaRepository.findById(id).ifPresent(n -> {
			subArea.setId(n.getId());
			subArea.setUpdatedAt(Instant.now());
			subAreaRepository.save(subArea);
		});
	}

	@RequestMapping(value = "/subArea", method = POST)
	public ResponseEntity<String> createSubArea(HttpServletRequest request, @ModelAttribute SubArea subArea) {
		if (subAreaRepository.findByWardNumber(subArea.getWardNumber()) == null) {
			subArea.setCreatedAt(Instant.now());
			SubArea dbSubArea = subAreaRepository.save(subArea);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbSubArea.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allStreets")
	public @ResponseBody ViewPage<Street> listStreets(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredStreets(filters, pageRequest);
		}
		return new ViewPage<>(streetRepository.findAll(pageRequest));
	}

	public ViewPage<Street> getFilteredStreets(String filters, PageRequest pageRequest) throws ParseException {
		long count = streetRepository.count();
		List<Street> records = genericRepository.findAllWithCriteria(filters, Street.class, pageRequest);
		return ViewPage.<Street>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/street/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateStreet(@PathVariable("id") Long id, @ModelAttribute Street street) {
		streetRepository.findById(id).ifPresent(n -> {
			street.setId(n.getId());
			street.setUpdatedAt(Instant.now());
			streetRepository.save(street);
		});
	}

	@RequestMapping(value = "/street", method = POST)
	public ResponseEntity<String> createStreet(HttpServletRequest request, @ModelAttribute Street street) {
		if (streetRepository.findByStreetNumber(street.getStreetNumber()) == null) {
			street.setCreatedAt(Instant.now());
			Street dbStreet = streetRepository.save(street);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(), dbStreet.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/getAllAreas")
	public @ResponseBody Map<Long, String> getAllAreas() {
		List<Area> areas = areaRepository.findAll();
		return areas.stream().filter(n -> n != null && n.getName() != null)
				.collect(Collectors.toMap(Area::getId, Area::getName));
	}

	@GetMapping("/getAllSubAreas")
	public @ResponseBody Map<Long, String> getAllSubAreas() {
		List<SubArea> areas = subAreaRepository.findAll();
		return areas.stream().filter(n -> n != null && n.getWardNumber() != null)
				.collect(Collectors.toMap(SubArea::getId, SubArea::getWardNumber));
	}

	@GetMapping("/getAllStreets")
	public @ResponseBody Map<Long, String> getAllStreets() {
		List<Street> streets = streetRepository.findAll();
		return streets.stream().filter(n -> n != null && n.getStreetNumber() != null)
				.collect(Collectors.toMap(Street::getId, Street::getStreetNumber));
	}
}
