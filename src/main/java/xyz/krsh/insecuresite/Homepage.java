package xyz.krsh.insecuresite;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Homepage {

	@GetMapping("/")
	public String index(Model model ) {
		return "Homepage";
	}

}