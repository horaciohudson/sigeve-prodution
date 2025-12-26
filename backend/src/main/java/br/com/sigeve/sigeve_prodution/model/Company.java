package br.com.sigeve.sigeve_prodution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidade que representa uma empresa dentro de um tenant
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tab_companies", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_company_cnpj_tenant", columnNames = {"cnpj", "tenant_id"})
    },
    indexes = {
        @Index(name = "idx_companies_tenant", columnList = "tenant_id"),
        @Index(name = "idx_companies_active", columnList = "tenant_id, is_active")
    })
public class Company extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "company_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // ==================== NOMES E IDENTIFICAÇÃO ====================
    
    @Column(name = "corporate_name", nullable = false)
    @NotBlank(message = "Razão social é obrigatória")
    private String corporateName;

    @Column(name = "trade_name")
    private String tradeName;

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "state_registration")
    private String stateRegistration;

    @Column(name = "municipal_registration")
    private String municipalRegistration;

    // ==================== CONTATOS ====================
    
    @Column(name = "phone")
    private String phone;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    @Email(message = "Email deve ser válido")
    private String email;

    @Column(name = "whatsapp")
    private String whatsapp;

    // ==================== CONFIGURAÇÕES FISCAIS ====================
    
    @Column(name = "iss_rate")
    private Float issRate;

    @Column(name = "funrural_rate")
    private Float funruralRate;

    // ==================== INFORMAÇÕES GERAIS ====================
    
    @Column(name = "manager")
    private String manager;

    @Column(name = "factory")
    private Boolean factory;

    // ==================== FLAGS DE TIPO ====================
    
    @Column(name = "supplier_flag", nullable = false, columnDefinition = "boolean default false")
    private Boolean supplierFlag = false;

    @Column(name = "customer_flag", nullable = false, columnDefinition = "boolean default false")
    private Boolean customerFlag = false;

    @Column(name = "transporter_flag", nullable = false, columnDefinition = "boolean default false")
    private Boolean transporterFlag = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ==================== MÉTODOS DE CICLO DE VIDA ====================

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.supplierFlag == null) {
            this.supplierFlag = false;
        }
        if (this.customerFlag == null) {
            this.customerFlag = false;
        }
        if (this.transporterFlag == null) {
            this.transporterFlag = false;
        }
        if (this.factory == null) {
            this.factory = false;
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    public String getDisplayName() {
        return this.tradeName != null && !this.tradeName.trim().isEmpty() 
               ? this.tradeName 
               : this.corporateName;
    }

    public boolean isSupplier() {
        return this.supplierFlag != null && this.supplierFlag;
    }

    public boolean isCustomer() {
        return this.customerFlag != null && this.customerFlag;
    }

    public boolean isTransporter() {
        return this.transporterFlag != null && this.transporterFlag;
    }

    public boolean isFactory() {
        return this.factory != null && this.factory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company company = (Company) o;
        return id != null && id.equals(company.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Company{id=%s, corporateName='%s', cnpj='%s', tenant=%s}", 
                           id, corporateName, cnpj, tenantId);
    }
}
