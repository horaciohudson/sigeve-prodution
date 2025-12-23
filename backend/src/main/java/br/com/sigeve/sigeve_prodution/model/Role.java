package br.com.sigeve.sigeve_prodution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tab_roles", indexes = {
    @Index(name = "idx_role_name", columnList = "role")
})
public class Role extends br.com.sigeve.sigeve_prodution.model.AuditSingle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, unique = true, length = 50)
    private br.com.sigeve.sigeve_prodution.enums.RoleType role;

    @Column(name = "description", length = 250)
    private String description;

}