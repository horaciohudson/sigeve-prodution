package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateRawMaterialDTO;
import br.com.sigeve.sigeve_prodution.dto.RawMaterialDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateRawMaterialDTO;
import br.com.sigeve.sigeve_prodution.service.RawMaterialService;
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
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Lista todas as matérias-primas de uma empresa
     */
    @GetMapping
    public ResponseEntity<List<RawMaterialDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        try {
            log.info("Listando matérias-primas da empresa: {}", companyId);

            List<RawMaterialDTO> materials = activeOnly
                    ? rawMaterialService.findActiveByCompany(companyId)
                    : rawMaterialService.findAllByCompany(companyId);

            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            log.error("Erro ao listar matérias-primas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca matéria-prima por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando matéria-prima por ID: {}", id);

            Optional<RawMaterialDTO> material = rawMaterialService.findById(id);
            return material.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar matéria-prima por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca matéria-prima por código
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<RawMaterialDTO> getByCode(
            @PathVariable String code,
            @RequestParam UUID companyId) {
        try {
            log.info("Buscando matéria-prima por código: {} na empresa: {}", code, companyId);

            Optional<RawMaterialDTO> material = rawMaterialService.findByCode(companyId, code);
            return material.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar matéria-prima por código: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca matérias-primas por categoria
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RawMaterialDTO>> getByCategory(@PathVariable UUID categoryId) {
        try {
            log.info("Buscando matérias-primas da categoria: {}", categoryId);

            List<RawMaterialDTO> materials = rawMaterialService.findByCategory(categoryId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            log.error("Erro ao buscar matérias-primas por categoria: {}", categoryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca matérias-primas por fornecedor
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<RawMaterialDTO>> getBySupplier(@PathVariable UUID supplierId) {
        try {
            log.info("Buscando matérias-primas do fornecedor: {}", supplierId);

            List<RawMaterialDTO> materials = rawMaterialService.findBySupplier(supplierId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            log.error("Erro ao buscar matérias-primas por fornecedor: {}", supplierId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria nova matéria-prima
     */
    @PostMapping
    public ResponseEntity<RawMaterialDTO> create(
            @Valid @RequestBody CreateRawMaterialDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando nova matéria-prima: {} por usuário: {}", request.getName(), username);

            RawMaterialDTO created = rawMaterialService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar matéria-prima: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar matéria-prima", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza matéria-prima existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRawMaterialDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando matéria-prima: {} por usuário: {}", id, username);

            RawMaterialDTO updated = rawMaterialService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar matéria-prima: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar matéria-prima: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deleta matéria-prima (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando matéria-prima: {} por usuário: {}", id, username);

            rawMaterialService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar matéria-prima: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar matéria-prima: {}", id, e);
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
