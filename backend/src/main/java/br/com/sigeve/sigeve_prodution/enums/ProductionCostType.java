package br.com.sigeve.sigeve_prodution.enums;

/**
 * Tipo de Custo de Produção
 */
public enum ProductionCostType {
    MATERIAL("Material"),
    SERVICE("Serviço"),
    LABOR("Mão de Obra"),
    INDIRECT("Custo Indireto");

    private final String displayName;

    ProductionCostType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
