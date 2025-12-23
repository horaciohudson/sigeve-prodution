package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionProductDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionProductDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionProductDTO;
import br.com.sigeve.sigeve_prodution.service.ProductionProductService;
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
@RequestMapping("/api/production-products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductionProductController {

    private final ProductionProductService productionProductService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Lista todos os produtos de produção de uma empresa
     */
    @GetMapping
    public ResponseEntity<List<ProductionProductDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        try {
            log.info("Listando produtos de produção da empresa: {}", companyId);

            List<ProductionProductDTO> products = activeOnly
                    ? productionProductService.findActiveByCompany(companyId)
                    : productionProductService.findAllByCompany(companyId);

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Erro ao listar produtos de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductionProductDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando produto de produção por ID: {}", id);

            Optional<ProductionProductDTO> product = productionProductService.findById(id);
            return product.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar produto de produção por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca produto por SKU
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductionProductDTO> getBySku(
            @PathVariable String sku,
            @RequestParam UUID companyId) {
        try {
            log.info("Buscando produto por SKU: {} na empresa: {}", sku, companyId);

            Optional<ProductionProductDTO> product = productionProductService.findBySku(companyId, sku);
            return product.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar produto por SKU: {}", sku, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria novo produto de produção
     */
    @PostMapping
    public ResponseEntity<ProductionProductDTO> create(
            @Valid @RequestBody CreateProductionProductDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo produto de produção: {} por usuário: {}", request.getDescription(), username);

            ProductionProductDTO created = productionProductService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar produto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar produto de produção", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza produto de produção existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductionProductDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductionProductDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando produto de produção: {} por usuário: {}", id, username);

            ProductionProductDTO updated = productionProductService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar produto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar produto de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deleta produto de produção (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando produto de produção: {} por usuário: {}", id, username);

            productionProductService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar produto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar produto de produção: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extrai username do token JWT
     */
    private String extractUsernameFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Extrai token do cabeçalho Authorization
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}
