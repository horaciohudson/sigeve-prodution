package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.ServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateServiceDTO;
import br.com.sigeve.sigeve_prodution.service.ServiceService;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllByCompany(
            @RequestParam UUID companyId,
            @RequestParam(required = false) Boolean activeOnly) {
        try {
            log.info("Listando serviços da empresa: {}", companyId);

            List<ServiceDTO> services;
            if (activeOnly != null && activeOnly) {
                services = serviceService.findByActiveStatus(companyId, true);
            } else {
                services = serviceService.findAllByCompany(companyId);
            }

            return ResponseEntity.ok(services);
        } catch (Exception e) {
            log.error("Erro ao listar serviços", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando serviço por ID: {}", id);

            Optional<ServiceDTO> service = serviceService.findById(id);
            return service.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar serviço por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ServiceDTO> getByCode(
            @PathVariable String code,
            @RequestParam UUID companyId) {
        try {
            log.info("Buscando serviço por código: {} na empresa: {}", code, companyId);

            Optional<ServiceDTO> service = serviceService.findByCode(companyId, code);
            return service.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar serviço por código: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> create(
            @Valid @RequestBody CreateServiceDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Criando novo serviço: {} por usuário: {}", request.getName(), username);

            ServiceDTO created = serviceService.create(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar serviço: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar serviço", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Atualizando serviço: {} por usuário: {}", id, username);

            ServiceDTO updated = serviceService.update(id, request, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar serviço: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Deletando serviço: {} por usuário: {}", id, username);

            serviceService.delete(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar serviço: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ServiceDTO> activate(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Ativando serviço: {} por usuário: {}", id, username);

            ServiceDTO activated = serviceService.activate(id, username);
            return ResponseEntity.ok(activated);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao ativar serviço: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao ativar serviço: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ServiceDTO> deactivate(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        try {
            String username = extractUsernameFromToken(httpRequest);

            log.info("Desativando serviço: {} por usuário: {}", id, username);

            ServiceDTO deactivated = serviceService.deactivate(id, username);
            return ResponseEntity.ok(deactivated);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao desativar serviço: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao desativar serviço: {}", id, e);
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
