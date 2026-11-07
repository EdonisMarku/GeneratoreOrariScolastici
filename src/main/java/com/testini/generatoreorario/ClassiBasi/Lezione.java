package com.testini.generatoreorario.ClassiBasi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Rappresenta una lezione/slot orario con compresenza
public class Lezione {
    private List<Docente> docenti;
    private Materia materia;
    private Classe classe;

    // Costruttore per docente singolo
    public Lezione(Docente docente, Materia materia, Classe classe) {
        this.docenti = new ArrayList<>();
        if (docente != null) {
            this.docenti.add(docente);
        }
        this.materia = materia;
        this.classe = classe;
    }

    // Costruttore per compresenza
    public Lezione(List<Docente> docenti, Materia materia, Classe classe) {
        this.docenti = new ArrayList<>(docenti);
        this.materia = materia;
        this.classe = classe;
    }

    public List<Docente> getDocenti() {
        return docenti;
    }

    public Docente getDocente() {
        return docenti.isEmpty() ? null : docenti.getFirst();
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    @Override
    public String toString() {
        String docentiStr = docenti.stream()
                .map(Docente::getNomeCognome)
                .collect(Collectors.joining(", "));
        return materia.getNomeMateria() + " (" + docentiStr + ")";
    }
}
