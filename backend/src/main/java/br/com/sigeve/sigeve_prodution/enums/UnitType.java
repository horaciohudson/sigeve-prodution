package br.com.sigeve.sigeve_prodution.enums;

/**
 * Tipo de Unidade de Medida
 */
public enum UnitType {
    UN("Unidade"),
    KG("Quilograma"),
    G("Grama"),
    M("Metro"),
    M2("Metro Quadrado"),
    M3("Metro Cúbico"),
    L("Litro"),
    ML("Mililitro"),
    PC("Peça"),
    CX("Caixa"),
    FD("Fardo"),
    RL("Rolo"),
    HR("Hora");

    private final String displayName;

    UnitType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
