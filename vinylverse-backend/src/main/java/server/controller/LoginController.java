package server.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.PermitAll;
import server.DTOs.DodeljenoPravoPristupaDTO;
import server.DTOs.KorisnikDTO;
import server.DTOs.PravoPristupaDTO;
import server.DTOs.UserLoginDTO;
import server.model.DodeljenoPravoPristupa;
import server.model.Korisnik;
import server.model.PravoPristupa;
import server.repository.PravoPristupaRepository;
import server.service.KorisnikService;
import server.service.PravoPristupaService;
import server.utils.TokenUtils;

@Controller
@RequestMapping("/api/auth")
@PermitAll
public class LoginController {
	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private KorisnikService korisnikService;
	
	@Autowired
	private PravoPristupaService pravoPristupaService;
	
	@Autowired
	private PravoPristupaRepository pravoPristupaRepository;

	

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO user) {
        System.out.println("Email: " + user.getEmail());
        System.out.println("Lozinka: " + user.getLozinka());

        Korisnik korisnik = korisnikService.findByEmail(user.getEmail());

        if (korisnik == null) {
            System.out.println("Korisnik nije pronađen.");
            return new ResponseEntity<>("Korisnik ne postoji", HttpStatus.UNAUTHORIZED);
        }

        boolean passwordMatches = passwordEncoder.matches(user.getLozinka(), korisnik.getLozinka());

        System.out.println("Lozinka iz baze: " + korisnik.getLozinka());
        System.out.println("Poklapanje lozinki: " + passwordMatches);

        if (passwordMatches) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(korisnik.getEmail());
            String token = tokenUtils.generateToken(userDetails);
            return ResponseEntity.ok(token);
        }

        return new ResponseEntity<>("Pogrešna lozinka", HttpStatus.UNAUTHORIZED);
    }
	
	
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody KorisnikDTO korisnikDTO) {

        PravoPristupaDTO adminPravo = pravoPristupaService.findByNaziv("ROLE_ADMIN");
        if (adminPravo == null) {

            adminPravo = pravoPristupaService.save(new PravoPristupaDTO(null,"ROLE_ADMIN",null, true));
        }


        DodeljenoPravoPristupaDTO dodeljenoPravo = new DodeljenoPravoPristupaDTO();
        dodeljenoPravo.setVidljiv(true);
        dodeljenoPravo.setPravoPristupa(adminPravo);


        Set<DodeljenoPravoPristupaDTO> prava = new HashSet<>();
        prava.add(dodeljenoPravo);
        korisnikDTO.setDodeljenaPravaPristupa(prava);


        KorisnikDTO saved = korisnikService.save(korisnikDTO);

        return ResponseEntity.ok(saved);
    }
}
