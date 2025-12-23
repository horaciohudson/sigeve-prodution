package br.com.sigeve.sigeve_prodution.enums;

/**
 * Status da Ordem de Produção
 */
public enum ProductionOrderStatus {
    PLANNED("Planejada"),
    IN_PROGRESS("Em Andamento"),
    FINISHED("Finalizada"),
    CANCELED("Cancelada");

    private final String displayName;

    ProductionOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
