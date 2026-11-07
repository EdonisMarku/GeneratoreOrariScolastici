package com.testini.generatoreorario.Parser;

import com.testini.generatoreorario.ClassiBasi.*;
import com.testini.generatoreorario.Generatori.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

// Gestisce l'esportazione degli orari generati in formato CSV
public class EsportatoreCSV {

    // Esporta l'orario completo di tutte le classi in un file CSV a griglia
    public static boolean esportaOrarioClassi(Collection<Classe> classi, File fileDestinazione) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileDestinazione))) {
            bw.write("Classe;Ora;Lunedi;Martedi;Mercoledi;Giovedi;Venerdi");
            bw.newLine();

            for (Classe c: classi) {
                Lezione[][] tab = c.getTabellaLezioni();
                for (int ora = 0; ora < GiorniSettimana.ORE_AL_GIORNO; ora++) {
                    StringBuilder riga = new StringBuilder();
                    riga.append(c.getNomeCompleto()).append(";").append(ora + 1).append("° ora");

                    for (int g = 0; g < GiorniSettimana.GIORNI_SCOLASTICI; g++) {
                        riga.append(";");
                        if (tab != null && tab[ora][g] != null) {
                            riga.append(tab[ora][g].toString().replace(";", ","));
                        } else {
                            riga.append("-");
                        }
                    }
                    bw.write(riga.toString());
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Errore durante l'esportazione: " + e.getMessage());
            return false;
        }
    }

    // Esporta l'orario di tutti i docenti in un file CSV
    public static boolean esportaOrarioDocenti(Collection<Docente> docenti, GeneratoreOrarioScuola generatore, File fileDestinazione) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileDestinazione))) {
            bw.write("Docente;Ora;Lunedi;Martedi;Mercoledi;Giovedi;Venerdi");
            bw.newLine();

            for (Docente d : docenti) {
                Lezione[][] tab = generatore.estraiOrarioDocente(d);
                for (int ora = 0; ora < GiorniSettimana.ORE_AL_GIORNO; ora++) {
                    StringBuilder riga = new StringBuilder();
                    riga.append(d.getNomeCognome()).append(";").append(ora + 1).append("° ora");

                    for (int g = 0; g < GiorniSettimana.GIORNI_SCOLASTICI; g++) {
                        riga.append(";");
                        if (tab != null && tab[ora][g] != null) {
                            Lezione l = tab[ora][g];
                            riga.append(l.getMateria().getNomeMateria())
                                .append(" (")
                                .append(l.getClasse().getNomeCompleto())
                                .append(")");
                        } else {
                            riga.append("-");
                        }
                    }
                    bw.write(riga.toString());
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Errore durante l'esportazione docenti: " + e.getMessage());
            return false;
        }
    }
}
