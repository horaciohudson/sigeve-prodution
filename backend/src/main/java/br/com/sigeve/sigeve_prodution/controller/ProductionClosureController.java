package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionClosureDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionClosureDTO;
import br.com.sigeve.sigeve_prodution.service.ProductionClosureService;
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
@RequestMapping("/api/production-closures")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductionClosureController {

    private final ProductionClosureService productionClosureService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ProductionClosureDTO>> getByCompany(@PathVariable UUID companyId) {
        try {
            log.info("Listando fechamentos da empresa: {}", companyId);

            List<ProductionClosureDTO> closures = productionClosureService.findByCompany(companyId);
            return ResponseEntity.ok(closures);
        } catch (Exception e) {
            log.error("Erro ao listar fechamentos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/production-order/{productionOrderId}")
    public ResponseEntity<ProductionClosureDTO> getByProductionOrder(@PathVariable UUID productionOrderId) {
        try {
            log.info("Buscando fechamento da ordem: {}", productionOrderId);

            Optional<ProductionClosureDTO> closure = productionClosureService.findByProductionOrder(productionOrderId);
            return closure.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar fechamento da ordem: {}", productionOrderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export-status")
    public ResponseEntity<List<ProductionClosureDTO>> getByExportStatus(
            @RequestParam UUID companyId,
            @RequestParam Boolean exported) {
        try {
            log.info("Listando fechamentos por status de exportação: {} na empresa: {}", exported, companyId);

            List<ProductionClosureDTO> closures = productionClosureService.findByExportStatus(companyId, exported);
            return ResponseEntity.ok(closures);
        } catch (Exception e) {
            log.error("Erro ao listar fechamentos por status de exportação", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductionClosureDTO> create(
            @Valid @RequestBody CreateProductionClosureDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo fechamento de produção por usuário: {}", username);

            ProductionClosureDTO created = productionClosureService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar fechamento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar fechamento de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/export-to-financial")
    public ResponseEntity<ProductionClosureDTO> exportToFinancial(
            @PathVariable UUID id,
            @RequestParam UUID financialDocumentId,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Exportando fechamento para financeiro: {} por usuário: {}", id, username);

            ProductionClosureDTO exported = productionClosureService.exportToFinancial(id, financialDocumentId, username);
            return ResponseEntity.ok(exported);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao exportar fechamento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao exportar fechamento para financeiro: {}", id, e);
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
        return (String) claims.get("username");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}
