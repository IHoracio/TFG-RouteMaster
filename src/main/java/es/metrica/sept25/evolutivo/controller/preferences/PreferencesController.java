package es.metrica.sept25.evolutivo.controller.preferences;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.service.traductor.PreferencesService;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

	@Autowired
	private PreferencesService preferencesService;

	@GetMapping("/languages")
	public List<EnumOptionDTO> getLanguages() {
		return preferencesService.getLanguages(LocaleContextHolder.getLocale());
	}

	@GetMapping("/themes")
	public List<EnumOptionDTO> getThemes() {
		return preferencesService.getThemes(LocaleContextHolder.getLocale());
	}
}
