package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.model.Role;
import br.com.sigeve.sigeve_prodution.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Lista todos os roles disponíveis
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("GET /api/roles - Listando todos os roles");
        
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }
}
