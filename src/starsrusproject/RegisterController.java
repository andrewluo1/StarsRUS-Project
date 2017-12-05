/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starsrusproject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import static starsrusproject.CurrentUser.HOST;
import static starsrusproject.CurrentUser.PWD;
import static starsrusproject.CurrentUser.USER;

/**
 * FXML Controller class
 *
 * @author Andrew
 */
public class RegisterController implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private Button cancelButton;
    
    @FXML
    private TextField ssnField;
    
    @FXML
    private TextField addressField;

    @FXML
    private TextField taxidField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> stateComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneField;
    
    @FXML
    private Label errorLabel;
    
    private boolean error = false;

    @FXML
    void handleRegisterButtonAction(ActionEvent event) throws IOException, SQLException {
        if(!nameField.getText().trim().isEmpty() &&
                !stateComboBox.getSelectionModel().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !taxidField.getText().trim().isEmpty() &&
                !usernameField.getText().trim().isEmpty() &&
                !passwordField.getText().trim().isEmpty()) {   
            error = false;
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);
                String query;
                if(!addressField.getText().trim().isEmpty() && !ssnField.getText().trim().isEmpty()){
                    query = "insert into Customers(cname, username, password, state, phone, email, taxid, address, ssn) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(8, addressField.getText());
                    statement.setString(9, ssnField.getText());
                }
                else if (!addressField.getText().trim().isEmpty()){
                    query = "insert into Customers(cname, username, password, state, phone, email, taxid, address) values (?, ?, ?, ?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(8, addressField.getText());
                }
                else if (!ssnField.getText().trim().isEmpty()){
                    query = "insert into Customers(cname, username, password, state, phone, email, taxid, ssn) values (?, ?, ?, ?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(8, ssnField.getText());
                }
                else{
                    query = "insert into Customers(cname, username, password, state, phone, email, taxid) values (?, ?, ?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    
                }
                statement.setString(1, nameField.getText());
                statement.setString (2,usernameField.getText());
                statement.setString(3, passwordField.getText());
                statement.setString(4, stateComboBox.getValue());
                statement.setString(5, phoneField.getText());
                statement.setString(6, emailField.getText());
                statement.setString(7, taxidField.getText());
                System.out.println(query);
                statement.execute();
            } catch (SQLException e){
                error = true;
                e.printStackTrace();
                errorLabel.setText("Unable to register user.");
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            }
            if(!error) {
                Parent login_parent = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
                Scene login_scene = new Scene(login_parent);
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.hide();
                primaryStage.setScene(login_scene);
                primaryStage.show();
            }
        }
        else {
            errorLabel.setText("Please fill out all fields.");
        }
        
    }

    @FXML
    void handleStateChoice(ActionEvent event) {
        
    }

    @FXML
    void handleCancelButtonAction(ActionEvent event) throws IOException {
        Parent login_parent = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        Scene login_scene = new Scene(login_parent);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.hide();
        primaryStage.setScene(login_scene);
        primaryStage.show();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        stateComboBox.setItems(FXCollections.observableArrayList("AL","AK",
                "AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN",
                "IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT",
                "NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA",
                "RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY"));
    }    
    
}
