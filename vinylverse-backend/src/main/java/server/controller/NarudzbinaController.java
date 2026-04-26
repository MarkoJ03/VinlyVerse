package server.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import server.DTOs.NarudzbinaDTO;
import server.DTOs.PayPalCaptureResponseDTO;
import server.DTOs.PayPalCreateOrderResponseDTO;
import server.model.Narudzbina;
import server.service.NarudzbinaService;

@Controller
@RequestMapping("/api/narudzbina")
public class NarudzbinaController extends BaseController<Narudzbina, NarudzbinaDTO, Long> {

    @Autowired
    private NarudzbinaService narudzbinaService;

    @Override
    protected NarudzbinaService getService() {
        return narudzbinaService;
    }

    @PostMapping("/guest")
    public ResponseEntity<NarudzbinaDTO> createGuest(@RequestBody NarudzbinaDTO dto) {
        NarudzbinaDTO saved = narudzbinaService.saveGuestOrder(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<NarudzbinaDTO> getGuestOrder(@PathVariable Long id, @RequestParam("token") String token) {
        return ResponseEntity.ok(narudzbinaService.getGuestNarudzbinaPregled(id, token));
    }

    @Override
    @PostMapping
    public ResponseEntity<NarudzbinaDTO> add(@RequestBody NarudzbinaDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !isAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(narudzbinaService.saveAdminNarudzbinu(dto), HttpStatus.CREATED);
    }

    @GetMapping("/moje")
    public ResponseEntity<List<NarudzbinaDTO>> mojeNarudzbine(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(narudzbinaService.findMojeNarudzbineByEmail(principal.getName()));
    }

    @PostMapping("/{id}/pay-mock")
    public ResponseEntity<NarudzbinaDTO> payMock(
            @PathVariable Long id,
            @RequestHeader(value = "X-Order-Token", required = false) String orderToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication != null && isAdmin(authentication);
        String email = currentUserEmail(authentication);
        NarudzbinaDTO result = narudzbinaService.payMock(id, email, admin, orderToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/paypal/create-order")
    public ResponseEntity<PayPalCreateOrderResponseDTO> createPayPalOrder(
            @PathVariable Long id,
            @RequestHeader(value = "X-Order-Token", required = false) String orderToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication != null && isAdmin(authentication);
        String email = currentUserEmail(authentication);
        PayPalCreateOrderResponseDTO result = narudzbinaService.createPayPalOrder(id, email, admin, orderToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/paypal/capture")
    public ResponseEntity<PayPalCaptureResponseDTO> capturePayPalOrder(
            @PathVariable Long id,
            @RequestHeader(value = "X-Order-Token", required = false) String orderToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication != null && isAdmin(authentication);
        String email = currentUserEmail(authentication);
        PayPalCaptureResponseDTO result = narudzbinaService.capturePayPalOrder(id, email, admin, orderToken);
        return ResponseEntity.ok(result);
    }

    private String currentUserEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object p = authentication.getPrincipal();
        if (p != null && "anonymousUser".equals(p.toString())) {
            return null;
        }
        return authentication.getName();
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
