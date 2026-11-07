package com.testini.generatoreorario.GUI;

import com.testini.generatoreorario.ClassiBasi.Scuola;
import com.testini.generatoreorario.Generatori.GeneratoreOrarioScuola;
import com.testini.generatoreorario.Parser.ParserCSV;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    ParserCSV parser = new ParserCSV();
    Scuola saraceno = new Scuola("Saraceno");
    Scuola romegialli = new Scuola("Romegialli");
    GeneratoreOrarioScuola generatoreSaraceno = new GeneratoreOrarioScuola(saraceno);
    GeneratoreOrarioScuola generatoreRomegialli = new GeneratoreOrarioScuola(romegialli);

    @FXML
    protected Label fileName;
    @FXML
    protected Label titoloPrincipale;
    @FXML
    protected Label profScelto;
    @FXML
    protected TableView<List<String>> tabellaIniziale = new TableView<>();

    @FXML
    protected ComboBox<String> comboBoxDocenti;

    @FXML
    protected void inizializzaComboboxDocenti() {
        comboBoxDocenti.getItems().setAll(ParserCSV.getDocenti());
    }

    @FXML
    @SuppressWarnings("unchecked")
    protected void onOpenFilePicker(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File CSV (*.csv)", "*.csv")
        );

        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            fileName.setText("File: " + selectedFile.getName());
            ParserCSV.fileCSV = selectedFile;
            List<String[]> listaOggetti = ParserCSV.parse();

            tabellaIniziale.getItems().clear();

            var colonneEsistenti = tabellaIniziale.getColumns();
            for (int i = 0; i < colonneEsistenti.size(); i++) {
                TableColumn<List<String>, String> colonna = (TableColumn<List<String>, String>) colonneEsistenti.get(i);

                final int colIndex = i;
                colonna.setCellValueFactory(cellData -> {
                    List<String> riga = cellData.getValue();
                    return new javafx.beans.property.SimpleStringProperty(riga.get(colIndex));
                });
            }

            assert listaOggetti != null;
            for(String[] s: listaOggetti) {
                List<String> nuovaRiga = List.of(s[0], s[1], s[2], s[3], s[4], s[5]);
                tabellaIniziale.getItems().add(nuovaRiga);
            }

            inizializzaComboboxDocenti();
            this.saraceno = this.parser.costruisciScuola();
            this.generatoreSaraceno = null;
        }

    }


    @FXML
    protected void finestraVincoli(ActionEvent event) {
        if (comboBoxDocenti.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Seleziona prima un docente dalla lista!");
            alert.setHeaderText(null);
            alert.setTitle("Docente non selezionato");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/testini/generatoreorario/VincoliView.fxml"));
            Parent root = loader.load();

            VincoliController controller = loader.getController();
            controller.setDocente(comboBoxDocenti.getValue());

            Stage stage = new Stage();
            stage.setTitle("Vincoli");
            stage.setScene(new Scene(root, 460, 360));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Finestra orario docenti
    @FXML
    protected void finestraOrariDocenti(ActionEvent event) {
        if (saraceno == null || saraceno.getAssegnazioni().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona prima un file CSV con le assegnazioni!");
            alert.setHeaderText(null);
            alert.setTitle("Attenzione");
            alert.showAndWait();
            return;
        }

        try {
            // Esegue l'algoritmo del backend per generare l'orario se non ancora calcolato
            if (this.generatoreSaraceno == null) {
                this.generatoreSaraceno = new GeneratoreOrarioScuola(this.saraceno);
                this.generatoreSaraceno.generaOrario();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/testini/generatoreorario/OrarioView.fxml"));
            Parent root = loader.load();

            OrarioController controller = loader.getController();
            controller.setDati(this.generatoreSaraceno, this.saraceno, comboBoxDocenti.getValue());

            Stage stage = new Stage();
            stage.setTitle("Orario Settimanale Docenti");
            stage.setScene(new Scene(root, 800, 560));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Finestra orario classi
    @FXML
    protected void finestraOrariClassi(ActionEvent event) {
        if (saraceno == null || saraceno.getAssegnazioni().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona prima un file CSV con le assegnazioni!");
            alert.setHeaderText(null);
            alert.setTitle("Attenzione");
            alert.showAndWait();
            return;
        }

        try {
            // Esegue l'algoritmo del backend per generare l'orario se non ancora calcolato
            if (this.generatoreSaraceno == null) {
                this.generatoreSaraceno = new GeneratoreOrarioScuola(this.saraceno);
                this.generatoreSaraceno.generaOrario();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/testini/generatoreorario/OrarioClassiView.fxml"));
            Parent root = loader.load();

            OrarioClassiController controller = loader.getController();
            controller.setDati(this.saraceno, null);

            Stage stage = new Stage();
            stage.setTitle("Orario Settimanale Classi");
            stage.setScene(new Scene(root, 800, 560));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
