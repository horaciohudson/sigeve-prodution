package br.com.sigeve.sigeve_prodution.enums;

/**
 * Tipo de Item na Composição (BOM)
 */
public enum CompositionItemType {
    RAW_MATERIAL("Matéria-Prima"),
    SERVICE("Serviço Terceirizado");

    private final String displayName;

    CompositionItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
