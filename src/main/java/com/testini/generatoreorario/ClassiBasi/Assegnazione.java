package com.testini.generatoreorario.ClassiBasi;

// Rappresenta l'assegnazione/slot di una materia a una classe con relativo docente e ore
public class Assegnazione {
    private Docente docente;
    private Classe classe;
    private Materia materia;
    private int oreSettimanali;

    public Assegnazione(Docente docente, Classe classe, Materia materia, int oreSettimanali) {
        this.docente = docente;
        this.classe = classe;
        this.materia = materia;
        this.oreSettimanali = oreSettimanali;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public int getOreSettimanali() {
        return oreSettimanali;
    }

    public void setOreSettimanali(int oreSettimanali) {
        this.oreSettimanali = oreSettimanali;
    }

    @Override
    public String toString() {
        return docente.getNomeCognome() + " -> " + classe.getNomeCompleto() + " [" + materia.getNomeMateria() + ", " + oreSettimanali + "h]";
    }
}
