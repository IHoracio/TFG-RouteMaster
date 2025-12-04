//package es.metrica.sept25.evolutivo.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
//import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
//import es.metrica.sept25.evolutivo.service.GasolineraService;
//import es.metrica.sept25.evolutivo.service.GasolineraServiceImpl;
//
//@Controller
//public class PruebaController {
//	
//	@GetMapping("/prueba")
//	@ResponseBody
//	public String test() {
//		return "hola buenos dias que tal";
//	}
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String prueba() {
////		GasolineraService gService = new GasolineraServiceImpl();
//		Gasolinera g = new Gasolinera("pepe");
//		System.out.println(g);
////		gService.save(g);
////		System.out.println(gService.getGasolineraList());
//		return "";
//	}
//}
