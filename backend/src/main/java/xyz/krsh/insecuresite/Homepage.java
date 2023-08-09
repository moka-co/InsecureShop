package xyz.krsh.insecuresite;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import xyz.krsh.insecuresite.rest.entities.Boardgame;

@Controller
public class Homepage {

	@GetMapping("/")
	public String index(Model model, @RequestParam(name = "q", defaultValue = "") String q) {
		String uri = "http://localhost:8080/api/boardgames?q="; // URL to fetch

		// RestTemplate is used to fetch the API
		RestTemplate restTemplate = new RestTemplate();
		Boardgame[] bg = null;

		try {
			bg = restTemplate.getForObject(uri + q, Boardgame[].class);

		} catch (Exception e) {
			System.out.println("Error message is: " + e);
			model.addAttribute("noResultFound", true);

		}

		// Add result to template
		model.addAttribute("boardgames", bg);

		// Reflect attribute for testing reflected XSS
		model.addAttribute("searchedFor", q);

		return "Homepage";
	}

}