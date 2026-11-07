package com.testini.generatoreorario.GUI;

import com.testini.generatoreorario.ClassiBasi.GiorniSettimana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class VincoliController {

    @FXML
    private Label labelVincoli;

    @FXML
    private Label primaOra;
    @FXML
    private Label ultimaOra;
    @FXML
    private CheckBox primaOraCheck;
    @FXML
    private CheckBox ultimaOraCheck;

    @FXML
    private Label oreCons;
    @FXML
    private Spinner<Integer> oreConsecSlider;
    @FXML
    private Label oreConsMax;
    @FXML
    private Spinner<Integer> oreConsMaxSpinner;

    @FXML
    private Label maxOreGiornoLabel;
    @FXML
    private ComboBox<GiorniSettimana> maxOreGiornoCB;
    @FXML
    private Spinner<Integer> maxOreGiornoSpinner;

    @FXML
    protected void initialize() {
        oreConsecSlider.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 2));
        oreConsMaxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 4));
        maxOreGiornoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 4));

        maxOreGiornoCB.getItems().setAll(
                GiorniSettimana.LUNEDI,
                GiorniSettimana.MARTEDI,
                GiorniSettimana.MERCOLEDI,
                GiorniSettimana.GIOVEDI,
                GiorniSettimana.VENERDI
        );
        maxOreGiornoCB.setValue(GiorniSettimana.LUNEDI);

        primaOraCheck.setSelected(true);
        ultimaOraCheck.setSelected(true);
    }

    public void setDocente(String docente) {
        if (docente != null && !docente.isEmpty()) {
            labelVincoli.setText("Vincoli per docente: " + docente);
        } else {
            labelVincoli.setText("Scegli i vincoli");
        }
    }

    @FXML
    protected void onChiudi(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
