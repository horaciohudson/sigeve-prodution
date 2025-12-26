package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.model.Role;
import br.com.sigeve.sigeve_prodution.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Lista todos os roles disponíveis
     */
    public List<Role> findAll() {
        log.info("Listando todos os roles");
        return roleRepository.findAll();
    }
}
