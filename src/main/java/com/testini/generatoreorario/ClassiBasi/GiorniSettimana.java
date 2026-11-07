package com.testini.generatoreorario.ClassiBasi;

// Enum dei giorni della settimana
public enum GiorniSettimana {
    LUNEDI("Lunedi"),
    MARTEDI("Martedi"),
    MERCOLEDI("Mercoledi"),
    GIOVEDI("Giovedi"),
    VENERDI("Venerdi"),
    SABATO("Sabato"),
    DOMENICA("Domenica");

    private final String nome;

    GiorniSettimana(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    // Giorni e ore settimanali
    public static final int GIORNI_SCOLASTICI = 5;
    public static final int ORE_AL_GIORNO = 6;
}
