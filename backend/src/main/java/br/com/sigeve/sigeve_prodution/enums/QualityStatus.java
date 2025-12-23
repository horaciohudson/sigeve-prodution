package br.com.sigeve.sigeve_prodution.enums;

/**
 * Status de Qualidade
 */
public enum QualityStatus {
    APPROVED("Aprovado"),
    REJECTED("Rejeitado"),
    REWORK("Retrabalho");

    private final String displayName;

    QualityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
