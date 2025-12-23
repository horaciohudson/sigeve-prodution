package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
import br.com.sigeve.sigeve_prodution.service.ProductionOrderService;
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
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping
    public ResponseEntity<List<ProductionOrderDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false) String status) {
        try {
            log.info("Listando ordens de produção da empresa: {}", companyId);

            List<ProductionOrderDTO> orders;
            if (status != null) {
                ProductionOrderStatus orderStatus = ProductionOrderStatus.valueOf(status.toUpperCase());
                orders = productionOrderService.findByStatus(companyId, orderStatus);
            } else {
                orders = productionOrderService.findAllByCompany(companyId);
            }

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Erro ao listar ordens de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando ordem de produção por ID: {}", id);

            Optional<ProductionOrderDTO> order = productionOrderService.findById(id);
            return order.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar ordem de produção por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductionOrderDTO> getByCode(
            @PathVariable String code,
            @RequestParam UUID companyId) {
        try {
            log.info("Buscando ordem por código: {} na empresa: {}", code, companyId);

            Optional<ProductionOrderDTO> order = productionOrderService.findByCode(companyId, code);
            return order.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar ordem por código: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductionOrderDTO> create(
            @Valid @RequestBody CreateProductionOrderDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando nova ordem de produção: {} por usuário: {}", request.getCode(), username);

            ProductionOrderDTO created = productionOrderService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar ordem de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionOrderDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductionOrderDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando ordem de produção: {} por usuário: {}", id, username);

            ProductionOrderDTO updated = productionOrderService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar ordem de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando ordem de produção: {} por usuário: {}", id, username);

            productionOrderService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar ordem de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ProductionOrderDTO> approve(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Aprovando ordem de produção: {} por usuário: {}", id, username);

            ProductionOrderDTO approved = productionOrderService.approve(id, username);
            return ResponseEntity.ok(approved);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao aprovar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao aprovar ordem de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ProductionOrderDTO> start(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Iniciando ordem de produção: {} por usuário: {}", id, username);

            ProductionOrderDTO started = productionOrderService.start(id, username);
            return ResponseEntity.ok(started);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao iniciar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao iniciar ordem de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ProductionOrderDTO> finish(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Finalizando ordem de produção: {} por usuário: {}", id, username);

            ProductionOrderDTO finished = productionOrderService.finish(id, username);
            return ResponseEntity.ok(finished);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao finalizar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao finalizar ordem de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ProductionOrderDTO> cancel(
            @PathVariable UUID id,
            @RequestParam String reason,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Cancelando ordem de produção: {} por usuário: {}", id, username);

            ProductionOrderDTO canceled = productionOrderService.cancel(id, reason, username);
            return ResponseEntity.ok(canceled);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao cancelar ordem: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao cancelar ordem de produção: {}", id, e);
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
