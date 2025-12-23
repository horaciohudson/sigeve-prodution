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
@Table(name = "tab_permissions", indexes = {
    @Index(name = "idx_permission_key", columnList = "permission_key")
})
public class Permission extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "permission_key", nullable = false, unique = true, length = 50)
    private String permissionKey;

    @Column(name = "description", length = 120)
    private String description;

    @Column(name = "level")
    private Integer level;

    // Construtor de conveniência
    public Permission(String permissionKey, String description, Integer level) {
        this.permissionKey = permissionKey;
        this.description = description;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionKey='" + permissionKey + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                '}';
    }
}