package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CompanyDTO;
import br.com.sigeve.sigeve_prodution.service.CompanyService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping
    public ResponseEntity<Page<CompanyDTO>> findByTenant(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            Pageable pageable) {
        log.info("Processando busca de empresas para tenant: {}", tenantId);
        return ResponseEntity.ok(companyService.findByTenant(tenantId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> findById(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {
        return companyService.findById(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> create(
            @RequestBody CompanyDTO companyDTO,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            HttpServletRequest request) {
        
        String username = extractUsernameFromToken(request);
        log.info("Processando criação de empresa por: {}", username);
        
        // Forçar tenantId do contexto antes da validação
        companyDTO.setTenantId(tenantId);
        
        CompanyDTO created = companyService.create(companyDTO, tenantId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyDTO companyDTO,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            HttpServletRequest request) {
        
        String username = extractUsernameFromToken(request);
        log.info("Processando atualização de empresa: {} por: {}", id, username);
        
        CompanyDTO updated = companyService.update(id, companyDTO, tenantId, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            HttpServletRequest request) {
        
        String username = extractUsernameFromToken(request);
        log.info("Processando exclusão de empresa: {} por: {}", id, username);
        
        companyService.delete(id, tenantId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CompanyDTO>> search(
            @RequestParam String text,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.searchByText(text, tenantId, pageable));
    }

    // ==================== ENDPOINTS ESPECÍFICOS POR TIPO ====================

    @GetMapping("/suppliers")
    public ResponseEntity<Page<CompanyDTO>> findSuppliers(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.findSuppliers(tenantId, pageable));
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<CompanyDTO>> findCustomers(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.findCustomers(tenantId, pageable));
    }

    @GetMapping("/transporters")
    public ResponseEntity<Page<CompanyDTO>> findTransporters(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.findTransporters(tenantId, pageable));
    }

    @GetMapping("/suppliers/simple")
    public ResponseEntity<List<CompanyDTO>> findSuppliersSimple(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.ok(companyService.findSuppliersSimple(tenantId));
    }

    @GetMapping("/customers/simple")
    public ResponseEntity<List<CompanyDTO>> findCustomersSimple(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.ok(companyService.findCustomersSimple(tenantId));
    }

    @GetMapping("/transporters/simple")
    public ResponseEntity<List<CompanyDTO>> findTransportersSimple(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.ok(companyService.findTransportersSimple(tenantId));
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
