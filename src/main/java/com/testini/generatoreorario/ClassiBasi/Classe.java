package com.testini.generatoreorario.ClassiBasi;

import java.util.*;

// Rappresenta una classe scolastica con orario e docenti
public class Classe {
    private int anno;
    private char sezione;
    private String indirizzo;
    private List<Docente> docenti;
    private List<Assegnazione> assegnazioni;
    private Map<Materia, Integer> materieOre;
    private Lezione[][] tabellaLezioni;

    public Classe(int anno, char sezione, String indirizzo) {
        this.anno = anno;
        this.sezione = sezione;
        this.indirizzo = indirizzo;
        this.docenti = new ArrayList<>();
        this.assegnazioni = new ArrayList<>();
        this.materieOre = new LinkedHashMap<>();
        this.tabellaLezioni = new Lezione[GiorniSettimana.ORE_AL_GIORNO][GiorniSettimana.GIORNI_SCOLASTICI];
    }

    // Restituisce il nome della classe (es. "5A INFO")
    public String getNomeCompleto() {
        return anno + "" + sezione + " " + indirizzo;
    }

    // Registra un docente se non gia presente
    public void aggiungiDocente(Docente docente) {
        if (!docenti.contains(docente)) {
            docenti.add(docente);
        }
    }

    // Registra un'assegnazione per la classe
    public void aggiungiAssegnazione(Assegnazione assegnazione) {
        assegnazioni.add(assegnazione);
        aggiungiDocente(assegnazione.getDocente());
        aggiungiMateriaOre(assegnazione.getMateria(), assegnazione.getOreSettimanali());
    }

    // Aggiorna il totale ore per una materia
    public void aggiungiMateriaOre(Materia materia, int ore) {
        materieOre.merge(materia, ore, Integer::sum);
    }

    // Calcola il totale delle ore previste per la classe
    public int getTotaleOrePreviste() {
        return materieOre.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getAnno() {
        return anno;
    }

    public char getSezione() {
        return sezione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public List<Docente> getDocenti() {
        return docenti;
    }

    public List<Assegnazione> getAssegnazioni() {
        return assegnazioni;
    }

    public Map<Materia, Integer> getMaterieOre() {
        return materieOre;
    }

    public Lezione[][] getTabellaLezioni() {
        return tabellaLezioni;
    }

    public void setTabellaLezioni(Lezione[][] tabellaLezioni) {
        this.tabellaLezioni = tabellaLezioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classe classe = (Classe) o;
        return anno == classe.anno && sezione == classe.sezione && Objects.equals(indirizzo, classe.indirizzo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anno, sezione, indirizzo);
    }

    @Override
    public String toString() {
        return getNomeCompleto();
    }
}
