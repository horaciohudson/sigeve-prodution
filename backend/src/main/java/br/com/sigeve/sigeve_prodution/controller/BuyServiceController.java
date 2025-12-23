package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.BuyServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateBuyServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateBuyServiceDTO;
import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
import br.com.sigeve.sigeve_prodution.service.BuyServiceService;
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
@RequestMapping("/api/buy-services")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BuyServiceController {

    private final BuyServiceService buyServiceService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping
    public ResponseEntity<List<BuyServiceDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false) String status) {
        try {
            log.info("Listando compras de serviço da empresa: {}", companyId);

            List<BuyServiceDTO> services;
            if (status != null) {
                BuyServiceStatus serviceStatus = BuyServiceStatus.valueOf(status.toUpperCase());
                services = buyServiceService.findByStatus(companyId, serviceStatus);
            } else {
                services = buyServiceService.findAllByCompany(companyId);
            }

            return ResponseEntity.ok(services);
        } catch (Exception e) {
            log.error("Erro ao listar compras de serviço", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyServiceDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando compra de serviço por ID: {}", id);

            Optional<BuyServiceDTO> service = buyServiceService.findById(id);
            return service.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar compra de serviço por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BuyServiceDTO> getByCode(
            @PathVariable String code,
            @RequestParam UUID companyId) {
        try {
            log.info("Buscando compra por código: {} na empresa: {}", code, companyId);

            Optional<BuyServiceDTO> service = buyServiceService.findByCode(companyId, code);
            return service.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar compra por código: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<BuyServiceDTO> create(
            @Valid @RequestBody CreateBuyServiceDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando nova compra de serviço: {} por usuário: {}", request.getCode(), username);

            BuyServiceDTO created = buyServiceService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar compra de serviço", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyServiceDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBuyServiceDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando compra de serviço: {} por usuário: {}", id, username);

            BuyServiceDTO updated = buyServiceService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar compra de serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando compra de serviço: {} por usuário: {}", id, username);

            buyServiceService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar compra de serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BuyServiceDTO> approve(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Aprovando compra de serviço: {} por usuário: {}", id, username);

            BuyServiceDTO approved = buyServiceService.approve(id, username);
            return ResponseEntity.ok(approved);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao aprovar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao aprovar compra de serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<BuyServiceDTO> close(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Fechando compra de serviço: {} por usuário: {}", id, username);

            BuyServiceDTO closed = buyServiceService.close(id, username);
            return ResponseEntity.ok(closed);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Erro ao fechar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao fechar compra de serviço: {}", id, e);
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
