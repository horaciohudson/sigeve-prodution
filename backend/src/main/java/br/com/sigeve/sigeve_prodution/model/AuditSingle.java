package br.com.sigeve.sigeve_prodution.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuditSingle {
    @Column(name="created_at", updatable=false, nullable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        this.createdAt = now;
    }
}

