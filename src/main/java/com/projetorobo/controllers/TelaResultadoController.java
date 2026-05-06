package com.projetorobo.controllers;

import com.projetorobo.model.enums.Modo;
import com.projetorobo.model.robo.Robo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaResultadoController {

    @FXML
    private Button btnSair;

    @FXML
    private Label labelInvalidosRobo1;

    @FXML
    private Label labelInvalidosRobo2;

    @FXML
    private Label labelNomeRobo1;

    @FXML
    private Label labelNomeRobo2;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label labelValidosRobo1;

    @FXML
    private Label labelValidosRobo2;

    @FXML
    private Label labelVencedor;

    @FXML
    private VBox VboxRobo2;

    @FXML
    void fecharJogo(ActionEvent event) {
        ((Stage) (btnSair.getScene().getWindow())).close();
    }

    public void receberDados(Robo robo1, Robo robo2, Modo modoDeJogo){
        labelNomeRobo1.setText("Robô " + robo1.getCor().toString().substring(0, 1).toLowerCase() +
                robo1.getCor().toString().substring(1).toLowerCase());
        if(modoDeJogo == Modo.USUARIO){
            if(robo1.getAchouAlimento())
                labelVencedor.setText(robo1.getCor().toString().substring(0, 1).toUpperCase() +
                        robo1.getCor().toString().substring(1).toLowerCase() + " Venceu!");
            else if(robo1.isExplodiu()){
                labelVencedor.setText(robo1.getCor().toString().substring(0, 1).toUpperCase() +
                        robo1.getCor().toString().substring(1).toLowerCase() + " Perdeu!");
                labelStatus.setText("O robô explodiu!");
            }
        }

        if(modoDeJogo == Modo.COMPETITIVO){
            if(robo1.getAchouAlimento())
                labelVencedor.setText(robo1.getCor().toString().substring(0, 1).toUpperCase() +
                        robo1.getCor().toString().substring(1).toLowerCase() + " Venceu!!");
            else if(robo2.getAchouAlimento())
                labelVencedor.setText(robo2.getCor().toString().substring(0, 1).toUpperCase() +
                        robo2.getCor().toString().substring(1).toLowerCase() + " Venceu!!");

            if(robo1.isExplodiu() && robo2.isExplodiu()) {
                labelVencedor.setText("Game Over!");
                labelStatus.setText("Os robos explodiram!");
            }
        }

        if(modoDeJogo == Modo.COOPERATIVO){
            if(robo1.getAchouAlimento() || robo2.getAchouAlimento()) {
                labelVencedor.setText("A dupla Venceu!");
                labelStatus.setText("Acharam o alimento!");
            }

            if(robo1.isExplodiu() && robo2.isExplodiu()) {
                labelVencedor.setText("Game Over!");
                labelStatus.setText("Os robos explodiram!");
            }
        }


        if(modoDeJogo != Modo.USUARIO) {
            labelNomeRobo2.setText("Robô " + robo2.getCor().toString().substring(0, 1).toLowerCase() +
                    robo2.getCor().toString().substring(1).toLowerCase());
            labelValidosRobo2.setText(String.format("%d", robo2.getMovimentosValidos()));
            labelInvalidosRobo2.setText(String.format("%d", robo2.getMovimentosInvalidos()));
        } else
            VboxRobo2.setVisible(false);

        labelValidosRobo1.setText( String.format("%d", robo1.getMovimentosValidos()));
        labelInvalidosRobo1.setText( String.format("%d", robo1.getMovimentosInvalidos()));
    }
}
