package xyz.krsh.insecuresite;

//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Homepage {

	@GetMapping("/")
	public String index(Model model) {

		// Temporary testing for db connection

		return "Homepage";
	}

}