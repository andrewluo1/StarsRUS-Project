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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
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
public class ManagerLoginController implements Initializable {
    CurrentUser currentUser = CurrentUser.getInstance();
    
    @FXML
    private TextField usernameField;

    @FXML
    private Button mainSystemButton;

    @FXML
    private Label invalidLabel;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    void handleMainSystemButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.hide();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) throws IOException, SQLException {
        if(isValidUser()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLManagerInterface.fxml"));
            Parent root  = loader.load();
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
            Scene scene = new Scene(root);  
            primaryStage.setScene(scene);
        }
        else {
            usernameField.clear();
            passwordField.clear();
            invalidLabel.setText("Invalid username and password. Please try again.");
        }
    }
    
    private boolean isValidUser() throws SQLException {
        boolean valid = false;
        if(!usernameField.getText().trim().isEmpty() && !passwordField.getText().trim().isEmpty()) {
            Connection connection = null;
            Statement statement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "select C.cname from Customers as C, Administrator A where A.adminID = C.taxid and C.username = '" + usernameField.getText() + "' and C.password = '" + passwordField.getText() +"' ";
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);    

                while(resultSet.next()) {
                    valid = true;
                    currentUser.setcname(resultSet.getString("cname")); 
                    currentUser.settaxid();
                    currentUser.setCurrentDate();
                }

            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            }
        }
        return valid;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
