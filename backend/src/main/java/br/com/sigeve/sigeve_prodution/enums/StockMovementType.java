package br.com.sigeve.sigeve_prodution.enums;

/**
 * Tipo de Movimento de Estoque
 */
public enum StockMovementType {
    IN("Entrada"),
    OUT("Saída");

    private final String displayName;

    StockMovementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
