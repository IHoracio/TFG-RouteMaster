package es.metrica.sept25.evolutivo.user;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences.Brands;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.repository.UserRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.user.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GasolineraRepository gasolineraRepository;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_userWithPassword_encodesAndSaves() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertEquals("encoded1234", saved.getPassword());
        verify(passwordEncoder).encode("1234");
        verify(userRepository).save(user);
    }

    @Test
    void save_userWithEncodedPassword_savesDirectly() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("$2a$something");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertEquals("$2a$something", saved.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
    }

    @Test
    void getByEmail_userExists_returnsUser() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getByEmail("test@mail.com");

        assertTrue(result.isPresent());
        assertEquals("test@mail.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@mail.com");
    }

    @Test
    void getByEmail_userNotExists_returnsEmpty() {
        when(userRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getByEmail("x@mail.com");

        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail("x@mail.com");
    }

    @Test
    void getAll_returnsAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    public final static String CORREO_EXISTENTE = "EXISTE@gmail.com";
    @Test
    void deleteByEmail_userExists() {
    	
    	doAnswer(a-> {
    		if(CORREO_EXISTENTE.equals(a.getArgument(0))) {
    			User user = new User();
    	        user.setEmail(a.getArgument(0));
    			Optional.of(user);
    		}
    		return Optional.empty();
    	}).
    	when(userRepository.findByEmail(anyString()));
    	
    	userService.deleteByEmail("EXISTE@gmail.com");
    	verify(userRepository, times(1)).deleteByEmail("EXISTE@gmail.com");
        
    	userService.deleteByEmail("del@mail.com");
        verify(userRepository, times(0)).deleteByEmail("del@mail.com");
    }

    @Test
    void deleteByEmail_userNotExists() {
        when(userRepository.findByEmail("del@mail.com")).thenReturn(Optional.empty());

        userService.deleteByEmail("del@mail.com");

        verify(userRepository).findByEmail("del@mail.com");
    }

    @Test
    void createUser_newUser_returnsUser() {
        UserDTO dto = new UserDTO();
        dto.setEmail("new@mail.com");
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setPassword("1234");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.createUser(dto);

        assertTrue(result.isPresent());
        assertEquals("new@mail.com", result.get().getEmail());
        assertEquals("encoded1234", result.get().getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_existingUser_returnsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setEmail("exists@mail.com");

        when(userRepository.findByEmail("exists@mail.com")).thenReturn(Optional.of(new User()));

        Optional<User> result = userService.createUser(dto);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateRoutePreferences_updatesUserPrefs() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.updateRoutePreferences(user, List.of(Brands.CEPSA), 10, "DIESEL", 1.2, RoutePreferences.MapViewType.SATELLITE);

        assertEquals(10, user.getRoutePreferences().getRadioKm());
        assertEquals("DIESEL", user.getRoutePreferences().getFuelType());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserPreferences_updatesThemeAndLanguage() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.updateUserPreferences(user, "Oscuro", "en");

        assertEquals("Oscuro", user.getUserPreferences().getTheme());
        assertEquals("en", user.getUserPreferences().getLanguage());
        verify(userRepository).save(user);
    }

    @Test
    void removeGasStation_existingUser_removesStation() {
        User user = new User();
        UserSavedGasStation sg = new UserSavedGasStation();
        sg.setAlias("Home");
        user.setSavedGasStations(new ArrayList<>(List.of(sg)));

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.removeGasStation("u@mail.com", "Home");

        assertEquals(0, user.getSavedGasStations().size());
        verify(userRepository).save(user);
    }

    @Test
    void removeGasStation_userNotExists() {
        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.empty());

        userService.removeGasStation("u@mail.com", "Home");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getSavedGasStations_returnsDtos() {
        User user = new User();
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);
        gas.setNombreEstacion("Station1");
        gas.setMarca("REPSOL");
        UserSavedGasStation sg = new UserSavedGasStation();
        sg.setAlias("Home");
        sg.setGasolinera(gas);
        user.setSavedGasStations(List.of(sg));

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));

        List<UserSavedGasStationDto> result = userService.getSavedGasStations("u@mail.com");

        assertEquals(1, result.size());
        assertEquals("Home", result.get(0).getAlias());
        verify(userRepository).findByEmail("u@mail.com");
    }

    @Test
    void getSavedGasStations_userNotExists_returnsEmpty() {
        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.empty());

        List<UserSavedGasStationDto> result = userService.getSavedGasStations("u@mail.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void saveGasStation_success() {
        User user = new User();
        user.setSavedGasStations(new java.util.ArrayList<>());
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));
        when(gasolineraRepository.findByIdEstacion(1L)).thenReturn(Optional.of(gas));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<String> result = userService.saveGasStation("u@mail.com", "Home", 1L);

        assertTrue(result.isEmpty());
        assertEquals(1, user.getSavedGasStations().size());
        assertEquals("Home", user.getSavedGasStations().get(0).getAlias());
    }

    @Test
    void saveGasStation_userNotExists_returnsError() {
        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.empty());

        Optional<String> result = userService.saveGasStation("u@mail.com", "Home", 1L);

        assertTrue(result.isPresent());
        assertEquals("Usuario no encontrado", result.get());
    }

    @Test
    void saveGasStation_gasNotFoundExternally_returnsError() {
        User user = new User();
        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));
        when(gasolineraRepository.findByIdEstacion(1L)).thenReturn(Optional.empty());
        when(gasolineraService.getGasolineraForId(1L)).thenReturn(Optional.empty());

        Optional<String> result = userService.saveGasStation("u@mail.com", "Home", 1L);

        assertEquals("Gasolinera no encontrada", result.get());
    }

    @Test
    void saveGasStation_aliasExists_returnsError() {
        User user = new User();
        user.setSavedGasStations(new ArrayList<>());
        
        UserSavedGasStation sg = new UserSavedGasStation();
        sg.setAlias("Home");
        user.getSavedGasStations().add(sg);
        
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));
        when(gasolineraRepository.findByIdEstacion(1L)).thenReturn(Optional.of(gas));

        Optional<String> result = userService.saveGasStation("u@mail.com", "Home", 1L);

        assertEquals("Alias ya existente", result.get());
    }

}
