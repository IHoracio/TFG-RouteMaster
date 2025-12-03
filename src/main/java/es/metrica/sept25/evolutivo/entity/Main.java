package es.metrica.sept25.evolutivo.entity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		HttpRequest provinciasReq = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create("https://api.precioil.es/provincias"))
				.build();
		
		HttpResponse<String> provinciasResp = HttpClient.newHttpClient()
				.send(provinciasReq, HttpResponse.BodyHandlers.ofString());

		HttpRequest municipiosReq = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create("https://api.precioil.es/municipios/provincia/49"))
				.build();
		
		HttpResponse<String> municipiosResp = HttpClient.newHttpClient()
				.send(municipiosReq, HttpResponse.BodyHandlers.ofString());

		String provincias = provinciasResp.body();
		String municipios = municipiosResp.body();

		ObjectMapper mapper = new ObjectMapper();
		List<Provincia> l = mapper.readValue(provincias, new TypeReference<List<Provincia>>() { });
		l = l.stream().filter(p -> p.getIdProvincia() < 100).toList();
		
		List<Municipio> m = mapper.readValue(municipios, new TypeReference<List<Municipio>>() { });
		m = m.stream().filter(muni -> muni.getIdProvincia() < 100).toList();

		System.out.println("PROVINCIAS ESPAÑOLAS");
		System.out.println(l);
		System.out.println(l.size());

		System.out.println("MUNICIPIOS ESPAÑOLES");
		System.out.println(m);
		System.out.println(m.size());
	}
}
