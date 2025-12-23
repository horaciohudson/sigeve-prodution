package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.service.ProductionExecutionService;
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
@RequestMapping("/api/production-executions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductionExecutionController {

    private final ProductionExecutionService productionExecutionService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/production-order/{productionOrderId}")
    public ResponseEntity<List<ProductionExecutionDTO>> getByProductionOrder(@PathVariable UUID productionOrderId) {
        try {
            log.info("Listando execuções da ordem: {}", productionOrderId);

            List<ProductionExecutionDTO> executions = productionExecutionService.findByProductionOrder(productionOrderId);
            return ResponseEntity.ok(executions);
        } catch (Exception e) {
            log.error("Erro ao listar execuções", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/step/{stepId}")
    public ResponseEntity<List<ProductionExecutionDTO>> getByStep(@PathVariable UUID stepId) {
        try {
            log.info("Listando execuções da etapa: {}", stepId);

            List<ProductionExecutionDTO> executions = productionExecutionService.findByStep(stepId);
            return ResponseEntity.ok(executions);
        } catch (Exception e) {
            log.error("Erro ao listar execuções da etapa: {}", stepId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionExecutionDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando execução por ID: {}", id);

            Optional<ProductionExecutionDTO> execution = productionExecutionService.findById(id);
            return execution.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar execução por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductionExecutionDTO> create(
            @Valid @RequestBody CreateProductionExecutionDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando nova execução de produção por usuário: {}", username);

            ProductionExecutionDTO created = productionExecutionService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar execução: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar execução de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionExecutionDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductionExecutionDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando execução de produção: {} por usuário: {}", id, username);

            ProductionExecutionDTO updated = productionExecutionService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar execução: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar execução de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando execução de produção: {} por usuário: {}", id, username);

            productionExecutionService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar execução: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar execução de produção: {}", id, e);
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
