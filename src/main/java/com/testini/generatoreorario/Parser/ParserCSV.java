package com.testini.generatoreorario.Parser;

import com.testini.generatoreorario.ClassiBasi.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

// Gestisce la lettura, validazione e costruzione del modello Scuola da file CSV
public class ParserCSV {
    public static File fileCSV;
    public static List<String[]> datiFile = new ArrayList<>();
    public static List<String> errori = new ArrayList<>();
    public static List<String> avvisi = new ArrayList<>();

    // Legge il file
    public static List<String[]> parse() {
        if (fileCSV == null) {
            errori.add("File CSV non selezionato.");
            return null;
        }

        List<String[]> data = new ArrayList<>();
        errori.clear();
        avvisi.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileCSV))) {
            String line;
            boolean primaRiga = true;
            int numRiga = 0;

            while ((line = br.readLine()) != null) {
                numRiga++;
                if (primaRiga) {
                    primaRiga = false;
                    continue; // Salta intestazione CSV
                }
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(";");
                if (values.length < 6) {
                    errori.add("Riga " + numRiga + " malformata (campi insufficienti): " + line);
                    continue;
                }
                data.add(values);
            }
        } catch (IOException e) {
            errori.add("Errore di lettura I/O: " + e.getMessage());
            return null;
        }

        datiFile = data;
        return data;
    }

    public static ArrayList<String> getDocenti() {
        ArrayList<String> nomi = new ArrayList<>();
        for(String[] s: datiFile) {
            nomi.add(s[0]);
        }

        return new ArrayList<>(new LinkedHashSet<>(nomi));
    }

    public Scuola costruisciScuola() {
        if (datiFile == null) {
            parse();
        }
        if (datiFile == null) {
            return null;
        }

        Scuola scuola = new Scuola();
        int rigaIdx = 1;

        for (String[] riga : datiFile) {
            rigaIdx++;
            String nomeDocente = riga[0].trim();
            String annoStr = riga[1].trim();
            String sezStr = riga[2].trim();
            String indirizzo = riga[3].trim();
            String nomeMateria = riga[4].trim();
            String oreStr = riga[5].trim();

            if (nomeDocente.isEmpty() || sezStr.isEmpty() || nomeMateria.isEmpty()) {
                errori.add("Riga " + rigaIdx + ": dati anagrafici mancanti.");
                continue;
            }

            int anno;
            try {
                anno = Integer.parseInt(annoStr);
                if (anno <= 0 || anno > 5) {
                    errori.add("Riga " + rigaIdx + ": anno non valido (" + annoStr + ").");
                    continue;
                }
            } catch (NumberFormatException e) {
                errori.add("Riga " + rigaIdx + ": anno non numerico (" + annoStr + ").");
                continue;
            }

            char sezione = sezStr.charAt(0);

            int ore;
            try {
                ore = Integer.parseInt(oreStr);
                if (ore <= 0) {
                    errori.add("Riga " + rigaIdx + ": numero di ore minore o uguale a zero (" + oreStr + ").");
                    continue;
                }
            } catch (NumberFormatException e) {
                errori.add("Riga " + rigaIdx + ": ore non numeriche (" + oreStr + ").");
                continue;
            }

            Docente docente = scuola.getOCreaDocente(nomeDocente);
            Classe classe = scuola.getOCreaClasse(anno, sezione, indirizzo);
            Materia materia = new Materia(nomeMateria);

            // Crea e aggiunge la nuova assegnazione
            Assegnazione assegnazione = new Assegnazione(docente, classe, materia, ore);
            scuola.aggiungiAssegnazione(assegnazione);
        }

        // Segnala eventuali docenti con ore eccessive
        for (Docente d : scuola.getTuttiIDocenti()) {
            if (d.getOreTotali() > GiorniSettimana.GIORNI_SCOLASTICI * GiorniSettimana.ORE_AL_GIORNO) {
                avvisi.add("Docente " + d.getNomeCognome() + " ha " + d.getOreTotali() + " ore (supera il limite di 30 ore).");
            }
        }

        return scuola;
    }
}
