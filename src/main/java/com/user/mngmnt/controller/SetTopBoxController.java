package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.mapper.JqgridObjectMapper;
import com.user.mngmnt.model.JqgridFilter;
import com.user.mngmnt.model.SetTopBox;
import com.user.mngmnt.model.Street;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.CustomerRepository;
import com.user.mngmnt.repository.SetTopBoxRepository;

@Controller
public class SetTopBoxController {

	@Autowired
	private SetTopBoxRepository setTopBoxRepository;

	@Autowired
	private CustomerRepository customerRepository;
	
	@GetMapping("/setTopBox")
	public String area() {
		return "setTopBox";
	}

	@GetMapping("/allSetTopBoxes")
	public @ResponseBody ViewPage<SetTopBox> listSetTopBoxes(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredSetTopBoxes(filters, pageRequest);
		}
		return new ViewPage<>(setTopBoxRepository.findAll(pageRequest));
	}

	public ViewPage<SetTopBox> getFilteredSetTopBoxes(String filters, PageRequest pageRequest) {
		JqgridFilter jqgridFilter = JqgridObjectMapper.map(filters);
		for (JqgridFilter.Rule rule : jqgridFilter.getRules()) {

		}
		Page<SetTopBox> setTopBoxes = null;
		return new ViewPage<>(setTopBoxes);
	}

	@RequestMapping(value = "/setTopBox/{id}", method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateArea(@PathVariable("id") Long id, @ModelAttribute SetTopBox setTopBox) {
		setTopBoxRepository.findById(id).ifPresent(n -> {
			setTopBox.setUpdatedAt(Instant.now());
			setTopBox.setId(n.getId());
			setTopBoxRepository.save(setTopBox);
		});
	}

	@RequestMapping(value = "/setTopBox", method = POST)
	public ResponseEntity<String> createArea(HttpServletRequest request, @ModelAttribute SetTopBox setTopBox) {
		if (setTopBoxRepository.findBySetTopBoxNumber(setTopBox.getSetTopBoxNumber()) == null) {
			setTopBox.setCreatedAt(Instant.now());
			SetTopBox dbSetTopBox = setTopBoxRepository.save(setTopBox);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbSetTopBox.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@GetMapping("/getAllSetTopBoxes")
	public @ResponseBody Map<Long, String> getAllStreets() {
		List<SetTopBox> setTopBoxes = setTopBoxRepository.findBySetTopBoxStatus(SetTopBoxStatus.FREE);
		return setTopBoxes.stream().filter(n -> n != null && n.getSetTopBoxNumber() != null)
				.collect(Collectors.toMap(SetTopBox::getId, SetTopBox::getSetTopBoxNumber));
	}
	
	@PostMapping("/uploadSetTopBoxesFile")
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}

		try {
			List<SetTopBox> setTopBoxes = new ArrayList<>();
			BufferedReader br;
			String line;
			InputStream is = file.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] setTopBoxDetails = line.split(",");
				setTopBoxes.add(SetTopBox.builder().build());
			}
			setTopBoxRepository.saveAll(setTopBoxes);
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/uploadStatus";
	}
}
