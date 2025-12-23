package br.com.sigeve.sigeve_prodution.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        String method = req.getMethod();
        System.out.println("🔍 JwtAuthenticationFilter: " + method + " " + requestURI);

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("   ℹ️  Sem token JWT - permitindo acesso (deve ser rota pública)");
            chain.doFilter(req, res);
            return;
        }

        System.out.println("   🔑 Token JWT encontrado - validando...");

        String token = header.substring(7);
        try {
            Claims claims = jwtService.parse(token).getBody();

            String userId = claims.get("user_id", String.class);
            String username = claims.get("username", String.class);
            String tenantId = claims.get("tenant_id", String.class);
            List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());

            System.out.println("   📋 Claims extraídos:");
            System.out.println("      user_id: " + userId);
            System.out.println("      username: " + username);
            System.out.println("      tenant_id: " + tenantId);
            System.out.println("      roles: " + roles);

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                // Garantir que o role tenha o prefixo ROLE_ se não tiver
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
                System.out.println("      ✅ Authority adicionada: " + roleWithPrefix);
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            auth.setDetails(Map.of("user_id", userId, "tenant_id", tenantId));

            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("   ✅ Autenticação configurada no SecurityContext");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Erro de assinatura JWT - chave secreta incorreta
            System.err.println("❌ ERRO JWT - Assinatura inválida: " + e.getMessage());
            System.err.println("   Verifique se app.jwt.secret está correto no application.properties");
            SecurityContextHolder.clearContext();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expirado
            System.err.println("⏰ ERRO JWT - Token expirado: " + e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Token malformado
            System.err.println("🔧 ERRO JWT - Token malformado: " + e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // Outros erros
            System.err.println("❌ Erro ao processar token JWT: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(req, res);
    }
}