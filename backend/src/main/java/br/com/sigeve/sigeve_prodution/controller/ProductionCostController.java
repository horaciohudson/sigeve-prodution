package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionCostDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionCostDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionCostDTO;
import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
import br.com.sigeve.sigeve_prodution.service.ProductionCostService;
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
@RequestMapping("/api/production-costs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductionCostController {

    private final ProductionCostService productionCostService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/production-order/{productionOrderId}")
    public ResponseEntity<List<ProductionCostDTO>> getByProductionOrder(@PathVariable UUID productionOrderId) {
        try {
            log.info("Listando custos da ordem: {}", productionOrderId);

            List<ProductionCostDTO> costs = productionCostService.findByProductionOrder(productionOrderId);
            return ResponseEntity.ok(costs);
        } catch (Exception e) {
            log.error("Erro ao listar custos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type")
    public ResponseEntity<List<ProductionCostDTO>> getByType(
            @RequestParam UUID companyId,
            @RequestParam String costType) {
        try {
            log.info("Listando custos por tipo: {} na empresa: {}", costType, companyId);

            ProductionCostType type = ProductionCostType.valueOf(costType.toUpperCase());
            List<ProductionCostDTO> costs = productionCostService.findByType(companyId, type);
            return ResponseEntity.ok(costs);
        } catch (Exception e) {
            log.error("Erro ao listar custos por tipo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionCostDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando custo por ID: {}", id);

            Optional<ProductionCostDTO> cost = productionCostService.findById(id);
            return cost.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar custo por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductionCostDTO> create(
            @Valid @RequestBody CreateProductionCostDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo custo de produção por usuário: {}", username);

            ProductionCostDTO created = productionCostService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar custo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar custo de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionCostDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductionCostDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando custo de produção: {} por usuário: {}", id, username);

            ProductionCostDTO updated = productionCostService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar custo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar custo de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando custo de produção: {} por usuário: {}", id, username);

            productionCostService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar custo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar custo de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ProductionCostDTO> approve(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Aprovando custo de produção: {} por usuário: {}", id, username);

            ProductionCostDTO approved = productionCostService.approve(id, username);
            return ResponseEntity.ok(approved);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao aprovar custo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao aprovar custo de produção: {}", id, e);
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
        return claims.get("username", String.class);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}
