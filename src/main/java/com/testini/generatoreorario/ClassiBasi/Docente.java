package com.testini.generatoreorario.ClassiBasi;

import java.util.*;

// Rappresenta un docente
public class Docente {
    private String nomeCognome;
    private List<Assegnazione> assegnazioni;
    private Set<GiorniSettimana> giorniLiberi;

    public Docente(String nomeCognome) {
        this.nomeCognome = nomeCognome;
        this.assegnazioni = new ArrayList<>();
        this.giorniLiberi = new HashSet<>();
    }

    // Aggiunge un'assegnazione al docente
    public void aggiungiAssegnazione(Assegnazione assegnazione) {
        this.assegnazioni.add(assegnazione);
    }

    public void aggiungiAssegnazione(Classe classe, Materia materia, int ore) {
        this.assegnazioni.add(new Assegnazione(this, classe, materia, ore));
    }

    // Calcola il totale delle ore settimanale del docente
    public int getOreTotali() {
        int totale = 0;
        for (Assegnazione a : assegnazioni) {
            totale += a.getOreSettimanali();
        }
        return totale;
    }

    public String getNomeCognome() {
        return nomeCognome;
    }

    public void setNomeCognome(String nomeCognome) {
        this.nomeCognome = nomeCognome;
    }

    public List<Assegnazione> getAssegnazioni() {
        return assegnazioni;
    }

    public Set<GiorniSettimana> getGiorniLiberi() {
        return giorniLiberi;
    }

    public void setGiorniLiberi(Set<GiorniSettimana> giorniLiberi) {
        this.giorniLiberi = giorniLiberi;
    }

    public void aggiungiGiornoLibero(GiorniSettimana giorno) {
        this.giorniLiberi.add(giorno);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Docente docente = (Docente) o;
        return Objects.equals(nomeCognome, docente.nomeCognome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeCognome);
    }

    @Override
    public String toString() {
        return nomeCognome;
    }
}
