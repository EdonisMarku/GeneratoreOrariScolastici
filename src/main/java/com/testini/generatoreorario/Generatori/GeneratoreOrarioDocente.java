package com.testini.generatoreorario.Generatori;

import com.testini.generatoreorario.ClassiBasi.*;

// Accesso e visualizzazione dell'orario di un singolo docente
public class GeneratoreOrarioDocente {
    private final Docente docente;
    private final GeneratoreOrarioScuola generatoreScuola;

    public GeneratoreOrarioDocente(Docente docente, GeneratoreOrarioScuola generatoreScuola) {
        this.docente = docente;
        this.generatoreScuola = generatoreScuola;
    }

    // Estrae e restituisce l'orario settimanale del docente
    public Lezione[][] getOrario() {
        return generatoreScuola.estraiOrarioDocente(docente);
    }

    // Stampa su console l'orario del docente
    public void stampaOrario() {
        generatoreScuola.stampaOrarioDocente(docente);
    }

    public Docente getDocente() {
        return docente;
    }
}
