package com.testini.generatoreorario.ClassiBasi;

import java.util.Objects;

// Rappresenta una materia
public class Materia {
    private String nomeMateria;

    public Materia(String nomeMateria) {
        this.nomeMateria = nomeMateria;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public void setNomeMateria(String nomeMateria) {
        this.nomeMateria = nomeMateria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        return Objects.equals(nomeMateria, materia.nomeMateria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeMateria);
    }

    @Override
    public String toString() {
        return nomeMateria;
    }
}
