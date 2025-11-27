package es.metrica.sept25.evolutiva_djlm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.metrica.sept25.evolutiva_djlm.entity.Gasolinera;

@Controller
public class PruebaController {
	
	@GetMapping("/prueba")
	@ResponseBody
	public String test() {
		return "hola buenos dias que tal";
	}
	
	@GetMapping("/test")
	@ResponseBody
	public String prueba() {
		Gasolinera g = new Gasolinera(3, "pepe");
		System.out.println(g);
	}
}
