package com.user.mngmnt.controller;

import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.SetTopBox;
import com.user.mngmnt.model.ViewPage;
import com.user.mngmnt.repository.GenericRepository;
import com.user.mngmnt.repository.SetTopBoxRepository;

@Controller
public class SetTopBoxController {

	@Autowired
	private SetTopBoxRepository setTopBoxRepository;

	@Autowired
	private GenericRepository genericRepository;

	@GetMapping("/setTopBox")
	public String area() {
		return "setTopBox";
	}

	@GetMapping("/allSetTopBoxes")
	public @ResponseBody ViewPage<SetTopBox> listSetTopBoxes(@RequestParam("_search") Boolean search,
			@RequestParam(value = "filters", required = false) String filters,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "2", required = false) Integer size,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sort) throws ParseException {
		PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.ASC, sort);
		if (search) {
			return getFilteredSetTopBoxes(filters, pageRequest);
		}
		return new ViewPage<>(setTopBoxRepository.findAll(pageRequest));
	}

	public ViewPage<SetTopBox> getFilteredSetTopBoxes(String filters, PageRequest pageRequest) throws ParseException {
		long count = setTopBoxRepository.count();
		List<SetTopBox> records = genericRepository.findAllWithCriteria(filters, SetTopBox.class, pageRequest);
		return ViewPage.<SetTopBox>builder().rows(records).max(pageRequest.getPageSize())
				.page(pageRequest.getPageNumber() + 1).total(count).build();

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
			return "redirect:setTopBox";
		}

		try {
			List<SetTopBox> setTopBoxes = new ArrayList<>();
			BufferedReader br;
			String line;
			InputStream is = file.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			boolean isFirstLineRead = false;
			int setTopBoxNumberIndex = -1;
			int cardNumberIndex = -1;
			int safeCodeIndex = -1;
			while ((line = br.readLine()) != null) {
				String[] setTopBoxDetails = line.split(",");
				if (!isFirstLineRead) {
					for (int i = 0; i < setTopBoxDetails.length; i++) {
						if (setTopBoxDetails[i].trim().equalsIgnoreCase("Set Top Box Number")) {
							setTopBoxNumberIndex = i;
						} else if (setTopBoxDetails[i].trim().equalsIgnoreCase("Card Number")) {
							cardNumberIndex = i;
						} else if (setTopBoxDetails[i].trim().equalsIgnoreCase("Safe Code")) {
							safeCodeIndex = i;
						}
					}
					isFirstLineRead = true;
					continue;
				}
				if(isFirstLineRead && setTopBoxDetails.length > 1) {
					setTopBoxes.add(SetTopBox.builder()
							.setTopBoxNumber(setTopBoxDetails[setTopBoxNumberIndex].trim())
							.cardNumber(setTopBoxDetails[cardNumberIndex].trim())
							.safeCode(setTopBoxDetails[safeCodeIndex].trim())
							.createdAt(Instant.now())
							.setTopBoxStatus(SetTopBoxStatus.FREE).build());
				}
			}
			setTopBoxRepository.saveAll(setTopBoxes);
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/setTopBox";
	}
}
