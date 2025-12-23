package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.BuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateBuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateBuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.service.BuyServiceItemService;
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
@RequestMapping("/api/buy-service-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BuyServiceItemController {

    private final BuyServiceItemService buyServiceItemService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/buy-service/{buyServiceId}")
    public ResponseEntity<List<BuyServiceItemDTO>> getByBuyService(@PathVariable UUID buyServiceId) {
        try {
            log.info("Listando itens da compra de serviço: {}", buyServiceId);

            List<BuyServiceItemDTO> items = buyServiceItemService.findByBuyService(buyServiceId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Erro ao listar itens da compra", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyServiceItemDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando item de compra por ID: {}", id);

            Optional<BuyServiceItemDTO> item = buyServiceItemService.findById(id);
            return item.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar item de compra por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<BuyServiceItemDTO> create(
            @Valid @RequestBody CreateBuyServiceItemDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo item de compra por usuário: {}", username);

            BuyServiceItemDTO created = buyServiceItemService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar item de compra", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyServiceItemDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBuyServiceItemDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando item de compra: {} por usuário: {}", id, username);

            BuyServiceItemDTO updated = buyServiceItemService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar item de compra: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando item de compra: {} por usuário: {}", id, username);

            buyServiceItemService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar item de compra: {}", id, e);
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
