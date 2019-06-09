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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.Channel;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.Pack;
import com.user.mngmnt.model.ResponseHandler;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.PackRepository;

@Controller
public class PackController {

	@Autowired
	private PackRepository packRepository;

	@Autowired
	private GenericRepository genericRepository;
	
	@GetMapping("/packs")
	public String area() {
		return "packs";
	}

	@GetMapping("/allPacks")
	public @ResponseBody ViewPage<Pack> listPacks(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredPacks(filters, pageRequest);
		}
		return new ViewPage<>(packRepository.findAll(pageRequest));
	}

	public ViewPage<Pack> getFilteredPacks(String filters, PageRequest pageRequest) throws ParseException {
		long count = packRepository.count();
		List<Pack> records = genericRepository.findAllWithCriteria(filters, Pack.class, pageRequest);
		return ViewPage.<Pack>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	
	}

	@RequestMapping(value = "/pack/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateArea(@PathVariable("id") Long id, @ModelAttribute Pack pack) {
		packRepository.findById(id).ifPresent(n -> {
			pack.setUpdatedAt(Instant.now());
			pack.setId(n.getId());
			packRepository.save(pack);
		});
	}

	@RequestMapping(value = "/pack", method = POST)
	public ResponseEntity<ResponseHandler> createArea(HttpServletRequest request, @ModelAttribute Pack pack) {
		if (packRepository.findByName(pack.getName()) == null) {
			pack.setCreatedAt(Instant.now());
			Pack dbPack = packRepository.save(pack);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(), dbPack.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		//return new ResponseEntity<>(HttpStatus.CONFLICT);
		return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
				.errorCode(HttpStatus.CONFLICT.value())
				.errorCause("Pack with name already exists.")
				.build(), HttpStatus.CONFLICT);
	}

	@GetMapping("/allPackNetworkChannels/{id}")
	public @ResponseBody ViewPage<NetworkChannel> listPackChannels(@PathVariable("id") Long id,
			@RequestParam("_search") Boolean search, @RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredPackNetworkChannels(filters, pageRequest);
		}
		return new ViewPage<>(packRepository.getNetworkChannelsByPackId(id, pageRequest));
	}

	public ViewPage<NetworkChannel> getFilteredPackNetworkChannels(String filters, PageRequest pageRequest) {
		Page<NetworkChannel> channels = null;
		return new ViewPage<>(channels);
	}

	@RequestMapping(value = "/createPackNetworkChannel/{id}", method = POST)
	@Transactional
	public ResponseEntity<ResponseHandler> createPackChannel(@PathVariable("id") Long id, HttpServletRequest request,
			@RequestParam("networkChannel_id") Long networkChannelId) {
		Pack pack = packRepository.getOne(id);
		if (pack != null) {
			pack.getNetworkChannels().add(NetworkChannel.builder().id(networkChannelId).createdAt(Instant.now()).build());
			packRepository.save(pack);
			URI uri = new UriTemplate("{requestUrl}").expand(request.getRequestURL().toString());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		//return new ResponseEntity<>(HttpStatus.CONFLICT);
		return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
				.errorCode(HttpStatus.CONFLICT.value())
				.errorCause("Network Channel with name already exists.")
				.build(), HttpStatus.CONFLICT);
	}

	@GetMapping("/getAllPacks")
	public @ResponseBody Map<Long, Pack> getAllStreets() {
		List<Pack> packs = packRepository.findAll();
		return packs.stream().filter(n -> n != null && n.getName() != null)
				.collect(Collectors.toMap(Pack::getId, p -> p));
	}
	
}
