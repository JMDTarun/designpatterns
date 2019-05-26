package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.Channel;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.model.Network;
import com.user.mngmnt.model.NetworkChannel;
import com.user.mngmnt.model.SubArea;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.ChannelRepository;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.NetworkChannelRepository;
import com.user.mngmnt.repository.NetworkRepository;

@Controller
public class NetworkController {

	@Autowired
	private NetworkRepository networkRepository;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private NetworkChannelRepository networkChannelRepository;

	@Autowired
	private GenericRepository genericRepository;

	@Value("${max.result.per.page}")
	private int maxResults;

	@Value("${max.card.display.on.pagination.tray}")
	private int maxPaginationTraySize;

	@GetMapping("/network")
	public String area() {
		return "network";
	}

	@GetMapping("/allNetworks")
	public @ResponseBody ViewPage<Network> listNetworks(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredNetworks(filters, pageRequest);
		}
		return new ViewPage<>(networkRepository.findAll(pageRequest));
	}

	public ViewPage<Network> getFilteredNetworks(String filters, PageRequest pageRequest) throws ParseException {
		long count = networkRepository.count();
		List<Network> records = genericRepository.findAllWithCriteria(filters, Network.class, pageRequest);
		return ViewPage.<Network>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();

	}

	@RequestMapping(value = "/network/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNetwork(@PathVariable("id") Long id, @ModelAttribute Network network) {
		networkRepository.findById(id).ifPresent(n -> {
			network.setUpdatedAt(Instant.now());
			network.setId(n.getId());
			networkRepository.save(network);
		});
	}

	@RequestMapping(value = "/network", method = POST)
	public ResponseEntity<String> createNetwork(HttpServletRequest request, @ModelAttribute Network network) {
		if (networkRepository.findByName(network.getName()) == null) {
			network.setCreatedAt(Instant.now());
			Network dbNetwork = networkRepository.save(network);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbNetwork.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allChannels")
	public @ResponseBody ViewPage<Channel> listChannels(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredChannels(filters, pageRequest);
		}
		return new ViewPage<>(channelRepository.findAll(pageRequest));
	}

	public ViewPage<Channel> getFilteredChannels(String filters, PageRequest pageRequest) throws ParseException {
		long count = channelRepository.count();
		List<Channel> records = genericRepository.findAllWithCriteria(filters, Channel.class, pageRequest);
		return ViewPage.<Channel>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/channel/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateChannel(@PathVariable("id") Long id, @ModelAttribute Channel channel) {
		channelRepository.findById(id).ifPresent(n -> {
			channel.setUpdatedAt(Instant.now());
			channel.setId(n.getId());
			channelRepository.save(channel);
		});
	}

	@RequestMapping(value = "/channel", method = POST)
	public ResponseEntity<String> createChannel(HttpServletRequest request, @ModelAttribute Channel channel) {
		if (channelRepository.findByName(channel.getName()) == null) {
			channel.setCreatedAt(Instant.now());
			Channel dbChannel = channelRepository.save(channel);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbChannel.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/allNetworkChannels")
	public @ResponseBody ViewPage<NetworkChannel> listNetworkChannels(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredNetworkChannel(filters, pageRequest);
		}
		return new ViewPage<>(networkChannelRepository.findAll(pageRequest));
	}

	public ViewPage<NetworkChannel> getFilteredNetworkChannel(String filters, PageRequest pageRequest) throws ParseException {
		long count = networkChannelRepository.count();
		List<NetworkChannel> records = genericRepository.findAllWithCriteria(filters, NetworkChannel.class,
				pageRequest);
		return ViewPage.<NetworkChannel>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();
	}

	@RequestMapping(value = "/networkChannel/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNetworkChannel(@PathVariable("id") Long id, @ModelAttribute NetworkChannel networkNhannel)
			throws JsonParseException, JsonMappingException, IOException {
		networkChannelRepository.findById(id).ifPresent(n -> {
			networkNhannel.setUpdatedAt(Instant.now());
			networkNhannel.setId(n.getId());
			networkChannelRepository.save(networkNhannel);
		});
	}

	@RequestMapping(value = "/networkChannel", method = POST)
	public ResponseEntity<String> createNetworkChannel(HttpServletRequest request,
			@ModelAttribute NetworkChannel networkChannel) {
		if (networkChannelRepository.findByName(networkChannel.getName()) == null) {
			networkChannel.setCreatedAt(Instant.now());
			NetworkChannel dbNetworkChannel = networkChannelRepository.save(networkChannel);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbNetworkChannel.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/networkManager")
	public String addNewUser(Model model) {
		model.addAttribute("networkChannel", new NetworkChannel());
		model.addAttribute("networkList", networkRepository.findAll());
		model.addAttribute("channelList", channelRepository.findAll());
		return "network-manager";
	}

	@PostMapping("/addNetwork")
	public String addNetwork(@ModelAttribute Network network, Model model) {
		String result = "redirect:networkManager";
		Network dbNetwork = networkRepository.findByName(network.getName());
		if (dbNetwork == null) {
			network.setCreatedAt(Instant.now());
			networkRepository.save(network);
			model.addAttribute("success", "Successfully Saved!");
		} else {
			model.addAttribute("error", "Network Already Exits!");
		}
		return result;
	}

	@PostMapping("/addChannel")
	public String addChannel(@ModelAttribute Channel channel, Model model) {
		String result = "redirect:networkManager";
		Channel dbChannel = channelRepository.findByName(channel.getName());
		if (dbChannel == null) {
			channel.setCreatedAt(Instant.now());
			channelRepository.save(channel);
			model.addAttribute("success", "Successfully Saved!");
		} else {
			model.addAttribute("error", "Network Already Exits!");
		}
		return result;
	}

	@PostMapping("/addNetworkChannel")
	public String addNetworkChannel(@ModelAttribute NetworkChannel networkChannel, Model model) {
		String result = "redirect:networkManager";
		NetworkChannel dbNetworkChannel = networkChannelRepository.findByName(networkChannel.getName());
		if (dbNetworkChannel == null) {
			networkChannelRepository.save(networkChannel);
			model.addAttribute("success", "Successfully Saved!");
		} else {
			model.addAttribute("error", "Network Already Exits!");
		}
		return result;
	}

	@GetMapping("/getAllChannels")
	public @ResponseBody Map<Long, String> getAllChannels() {
		List<Channel> channels = channelRepository.findAll();
		return channels.stream().filter(c -> c != null && c.getName() != null)
				.collect(Collectors.toMap(Channel::getId, Channel::getName));
	}

	@GetMapping("/getAllNetworks")
	public @ResponseBody Map<Long, String> getAllNetworks() {
		List<Network> networks = networkRepository.findAll();
		return networks.stream().filter(n -> n != null && n.getName() != null)
				.collect(Collectors.toMap(Network::getId, Network::getName));
	}

	@GetMapping("/getAllNetworkChannels")
	public @ResponseBody Map<Long, Object> getAllNetworkChannels() {
		List<NetworkChannel> networkChannels = networkChannelRepository.findAll();
		return networkChannels.stream().filter(c -> c != null && c.getName() != null)
				.collect(Collectors.toMap(NetworkChannel::getId, c -> c));
	}
}
