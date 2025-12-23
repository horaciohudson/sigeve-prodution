package br.com.sigeve.sigeve_prodution.enums;

/**
 * Status de Compra de Serviço
 */
public enum BuyServiceStatus {
    OPEN("Aberta"),
    APPROVED("Aprovada"),
    CLOSED("Fechada");

    private final String displayName;

    BuyServiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
