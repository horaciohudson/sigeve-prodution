package br.com.sigeve.sigeve_prodution.enums;

/**
 * Nível de Prioridade
 */
public enum PriorityLevel {
    LOW("Baixa"),
    MEDIUM("Média"),
    HIGH("Alta"),
    URGENT("Urgente");

    private final String displayName;

    PriorityLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
