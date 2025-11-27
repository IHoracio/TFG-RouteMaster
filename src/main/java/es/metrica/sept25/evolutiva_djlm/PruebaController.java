package es.metrica.sept25.evolutiva_djlm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PruebaController {
	
	@GetMapping("/prueba")
	@ResponseBody
	public String test() {
		return "hola buenos dias que tal";
	}
}
