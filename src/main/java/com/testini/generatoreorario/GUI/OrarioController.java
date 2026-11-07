package com.testini.generatoreorario.GUI;

import com.testini.generatoreorario.ClassiBasi.Docente;
import com.testini.generatoreorario.ClassiBasi.GiorniSettimana;
import com.testini.generatoreorario.ClassiBasi.Lezione;
import com.testini.generatoreorario.ClassiBasi.Scuola;
import com.testini.generatoreorario.Generatori.GeneratoreOrarioScuola;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class OrarioController {

    @FXML
    private Label labelOrario;

    @FXML
    private ComboBox<String> comboBoxDocenti;

    @FXML
    private GridPane grigliaOrario;

    private GeneratoreOrarioScuola generatore;
    private Scuola scuola;

    @FXML
    protected void initialize() {
        comboBoxDocenti.setOnAction(event -> {
            String nomeDocente = comboBoxDocenti.getValue();
            if (nomeDocente != null && scuola != null && generatore != null) {
                Docente d = scuola.getDocenti().get(nomeDocente);
                if (d != null) {
                    mostraOrarioDocente(d);
                }
            }
        });
    }

    public void setDati(GeneratoreOrarioScuola generatore, Scuola scuola, String docenteIniziale) {
        this.generatore = generatore;
        this.scuola = scuola;

        comboBoxDocenti.getItems().clear();
        if (scuola != null) {
            comboBoxDocenti.getItems().addAll(scuola.getDocenti().keySet());
        }

        if (docenteIniziale != null && comboBoxDocenti.getItems().contains(docenteIniziale)) {
            comboBoxDocenti.setValue(docenteIniziale);
        } else if (!comboBoxDocenti.getItems().isEmpty()) {
            comboBoxDocenti.setValue(comboBoxDocenti.getItems().getFirst());
        }
    }

    public void setDocente(String docente) {
        if (docente != null && !docente.isEmpty()) {
            comboBoxDocenti.setValue(docente);
        }
    }

    private void mostraOrarioDocente(Docente docente) {
        labelOrario.setText("Orario di: " + docente.getNomeCognome());
        grigliaOrario.getChildren().clear();

        String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì"};

        // Cella intestazione ORA
        Label lblOraH = new Label("Ora");
        lblOraH.getStyleClass().add("cella-header");
        lblOraH.setPrefSize(70, 35);
        lblOraH.setAlignment(Pos.CENTER);
        grigliaOrario.add(lblOraH, 0, 0);

        // Intestazioni dei giorni
        for (int g = 0; g < giorni.length; g++) {
            Label lblG = new Label(giorni[g]);
            lblG.getStyleClass().add("cella-header");
            lblG.setPrefSize(130, 35);
            lblG.setAlignment(Pos.CENTER);
            grigliaOrario.add(lblG, g + 1, 0);
        }

        // Estrazione matrice lezioni [6][5] dal generatore del backend
        Lezione[][] orario = generatore.estraiOrarioDocente(docente);

        for (int o = 0; o < GiorniSettimana.ORE_AL_GIORNO; o++) {
            // Etichetta dell'ora
            Label lblOra = new Label((o + 1) + "ª ora");
            lblOra.getStyleClass().add("cella-ora");
            lblOra.setPrefSize(70, 55);
            lblOra.setAlignment(Pos.CENTER);
            grigliaOrario.add(lblOra, 0, o + 1);

            // Singole lezioni per ogni giorno
            for (int g = 0; g < GiorniSettimana.GIORNI_SCOLASTICI; g++) {
                Lezione l = (orario != null) ? orario[o][g] : null;
                Label cella = new Label();
                cella.setPrefSize(130, 55);
                cella.setAlignment(Pos.CENTER);
                cella.setTextAlignment(TextAlignment.CENTER);
                cella.setWrapText(true);

                if (l != null) {
                    cella.setText(l.getMateria().getNomeMateria() + "\n(" + l.getClasse().getNomeCompleto() + ")");
                    cella.getStyleClass().add("cella-lezione");
                    cella.getStyleClass().add(getClasseStileMateria(l.getMateria().getNomeMateria()));
                } else {
                    cella.setText("-");
                    cella.getStyleClass().add("cella-libera");
                }
                grigliaOrario.add(cella, g + 1, o + 1);
            }
        }
    }

    private String getClasseStileMateria(String nomeMateria) {
        if (nomeMateria == null) return "cella-lezione";
        String s = nomeMateria.toLowerCase().trim();

        if (s.contains("matematica")) return "materia-matematica";
        if (s.contains("italiano")) return "materia-italiano";
        if (s.contains("storia")) return "materia-storia";
        if (s.contains("bio") || s.contains("igiene") || s.contains("scienze umane") || s.contains("scienze integrate")) return "materia-biologia";
        if (s.contains("geografia") || s.contains("geopedologia")) return "materia-geografia";
        if (s.contains("inglese")) return "materia-inglese";
        if (s.contains("diritto") || s.contains("econ")) return "materia-diritto";
        if (s.contains("chimica")) return "materia-chimica";
        if (s.contains("fisica")) return "materia-fisica";
        if (s.contains("informatica") || s.contains("telecomunicazioni") || s.contains("sistemi") || s.contains("tpsit") || s.contains("gpoi")) return "materia-informatica";
        if (s.contains("motori") || s.contains("sport")) return "materia-motoria";
        if (s.contains("religione") || s.contains("irc")) return "materia-religione";
        if (s.contains("tedesco") || s.contains("francese") || s.contains("spagnolo")) return "materia-lingue";
        if (s.contains("tec") || s.contains("cantiere") || s.contains("topografia") || s.contains("estimo") || s.contains("prog.cost")) return "materia-tecnica";

        return "cella-lezione";
    }
}
