package br.com.sigeve.sigeve_prodution.enums;

/**
 * Origem do Movimento de Estoque
 */
public enum MovementOrigin {
    PURCHASE("Compra"),
    PRODUCTION("Produção"),
    ADJUSTMENT("Ajuste"),
    RETURN("Devolução"),
    TRANSFER("Transferência");

    private final String displayName;

    MovementOrigin(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
