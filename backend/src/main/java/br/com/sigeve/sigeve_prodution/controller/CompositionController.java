package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CompositionDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateCompositionDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateCompositionDTO;
import br.com.sigeve.sigeve_prodution.service.CompositionService;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/compositions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompositionController {

    private final CompositionService compositionService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping
    public ResponseEntity<List<CompositionDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        try {
            log.info("Listando composições da empresa: {}", companyId);

            List<CompositionDTO> compositions = activeOnly
                    ? compositionService.findActiveByCompany(companyId)
                    : compositionService.findAllByCompany(companyId);

            return ResponseEntity.ok(compositions);
        } catch (Exception e) {
            log.error("Erro ao listar composições", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositionDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando composição por ID: {}", id);

            Optional<CompositionDTO> composition = compositionService.findById(id);
            return composition.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar composição por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CompositionDTO>> getByProduct(@PathVariable UUID productId) {
        try {
            log.info("Buscando composições do produto: {}", productId);

            List<CompositionDTO> compositions = compositionService.findByProduct(productId);
            return ResponseEntity.ok(compositions);
        } catch (Exception e) {
            log.error("Erro ao buscar composições por produto: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<CompositionDTO> create(
            @Valid @RequestBody CreateCompositionDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando nova composição: {} por usuário: {}", request.getName(), username);

            CompositionDTO created = compositionService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar composição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar composição", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositionDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompositionDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando composição: {} por usuário: {}", id, username);

            CompositionDTO updated = compositionService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar composição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar composição: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando composição: {} por usuário: {}", id, username);

            compositionService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar composição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar composição: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CompositionDTO> approve(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Aprovando composição: {} por usuário: {}", id, username);

            CompositionDTO approved = compositionService.approve(id, username);
            return ResponseEntity.ok(approved);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao aprovar composição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao aprovar composição: {}", id, e);
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
