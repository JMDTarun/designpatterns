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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriTemplate;

import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.ResponseHandler;
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
	public ResponseEntity<ResponseHandler> createArea(HttpServletRequest request, @ModelAttribute SetTopBox setTopBox) {
		SetTopBox findBySetTopBoxNumber = setTopBoxRepository.findBySetTopBoxNumber(setTopBox.getSetTopBoxNumber());
		SetTopBox findByCardNumber = setTopBoxRepository.findByCardNumber(setTopBox.getCardNumber());
		SetTopBox findBySafeCode = setTopBoxRepository.findBySafeCode(setTopBox.getSafeCode());
		if (findBySetTopBoxNumber == null && findByCardNumber == null && findBySafeCode == null) {
			setTopBox.setCreatedAt(Instant.now());
			SetTopBox dbSetTopBox = setTopBoxRepository.save(setTopBox);
			URI uri = new UriTemplate("{requestUrl}/{id}").expand(request.getRequestURL().toString(),
					dbSetTopBox.getId());
			final HttpHeaders headers = new HttpHeaders();
			headers.put("Location", singletonList(uri.toASCIIString()));
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}
		String rootCause = null;
		if(findBySetTopBoxNumber != null) {
			rootCause = "Set Top Box with name number already exists";
		} else if(findByCardNumber != null) {
			rootCause = "Set Top Box with card number already exists";
		} else {
			rootCause = "Set Top Box with safe code already exists";
		}
		return new ResponseEntity<ResponseHandler>(ResponseHandler.builder()
				.errorCode(HttpStatus.CONFLICT.value())
				.errorCause(rootCause)
				.build(), HttpStatus.CONFLICT);
	}

	@GetMapping("/getAllSetTopBoxes")
	public @ResponseBody Map<Long, String> getAllStreets() {
		List<SetTopBox> setTopBoxes = setTopBoxRepository.findBySetTopBoxStatus(SetTopBoxStatus.FREE);
		return setTopBoxes.stream().filter(n -> n != null && n.getSetTopBoxNumber() != null)
				.collect(Collectors.toMap(SetTopBox::getId, SetTopBox::getSetTopBoxNumber));
	}

	@PostMapping("/uploadSetTopBoxesFile")
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IOException {

		if (file.isEmpty()) {
			redirectAttributes.addAttribute("message", "Please select a file to upload");
			return "redirect:setTopBox";
		}

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
			if (isFirstLineRead && setTopBoxDetails.length > 1) {
				setTopBoxes.add(SetTopBox.builder().setTopBoxNumber(setTopBoxDetails[setTopBoxNumberIndex].trim())
						.cardNumber(setTopBoxDetails[cardNumberIndex].trim())
						.safeCode(setTopBoxDetails[safeCodeIndex].trim())
						.setTopBoxStatus(SetTopBoxStatus.FREE).build());
			}
		}
		List<SetTopBox> errorSetTopBoxes = new ArrayList<>();
		for (SetTopBox stb : setTopBoxes) {
			try {
				setTopBoxRepository.save(stb);
			} catch (Exception e) {
				errorSetTopBoxes.add(stb);
			}
		}
		if(!CollectionUtils.isEmpty(errorSetTopBoxes)) {
			redirectAttributes.addAttribute("totalElements", setTopBoxes.size());
			redirectAttributes.addAttribute("savedElements", setTopBoxes.size() - errorSetTopBoxes.size());
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
