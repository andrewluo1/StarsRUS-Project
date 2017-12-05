/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starsrusproject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author Andrew
 */
public class MainController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleTraderButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setResizable(false);
        primaryStage.hide();
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @FXML
    private void handleManagerButtonAction(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("FXMLManagerLogin.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setResizable(false);
        primaryStage.hide();
        primaryStage.setScene(scene);
        primaryStage.show();        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
