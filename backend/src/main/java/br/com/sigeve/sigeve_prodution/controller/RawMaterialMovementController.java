package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateRawMaterialMovementDTO;
import br.com.sigeve.sigeve_prodution.dto.RawMaterialMovementDTO;
import br.com.sigeve.sigeve_prodution.service.RawMaterialMovementService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/raw-material-movements")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RawMaterialMovementController {

    private final RawMaterialMovementService rawMaterialMovementService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<RawMaterialMovementDTO>> getByCompany(@PathVariable UUID companyId) {
        try {
            log.info("Listando movimentos da empresa: {}", companyId);

            List<RawMaterialMovementDTO> movements = rawMaterialMovementService.findByCompany(companyId);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            log.error("Erro ao listar movimentos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    public ResponseEntity<List<RawMaterialMovementDTO>> getByRawMaterial(@PathVariable UUID rawMaterialId) {
        try {
            log.info("Listando movimentos da matéria-prima: {}", rawMaterialId);

            List<RawMaterialMovementDTO> movements = rawMaterialMovementService.findByRawMaterial(rawMaterialId);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            log.error("Erro ao listar movimentos da matéria-prima: {}", rawMaterialId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<RawMaterialMovementDTO> create(
            @Valid @RequestBody CreateRawMaterialMovementDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo movimento de estoque por usuário: {}", username);

            RawMaterialMovementDTO created = rawMaterialMovementService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar movimento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar movimento de estoque", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractUsernameFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}
