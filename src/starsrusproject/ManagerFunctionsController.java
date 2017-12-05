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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static starsrusproject.CurrentUser.HOST;
import static starsrusproject.CurrentUser.PWD;
import static starsrusproject.CurrentUser.USER;

/**
 * FXML Controller class
 *
 * @author Andrew
 */
public class ManagerFunctionsController implements Initializable {
    
    @FXML
    private Button deleteButton;

    @FXML
    private Button interestButton;
    
    @FXML
    private Label label;

    @FXML
    private Button homeButton;

    @FXML
    void handleInterestButtonAction(ActionEvent event) throws SQLException {
        Connection connection = null;
        Statement statement1 = null;
        PreparedStatement statement = null;
        Statement amountStatement;
        double monthlyInterest = 0.0025;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            connection = DriverManager.getConnection(HOST, USER, PWD);
            String query1 = "select M.aid from MarketAccounts M";
            
            statement1 = connection.createStatement();
            ResultSet resultSet = statement1.executeQuery(query1);
            
            while(resultSet.next()) {
                
                double amount = 0.0;
                String amountQuery = "select " + monthlyInterest + "*M.monthlysum/day(C.currentDate) as amount from MarketAccounts M, CurrentDate C where M.aid = " + resultSet.getString("aid");
                amountStatement = connection.createStatement();
                ResultSet amountResultSet = amountStatement.executeQuery(amountQuery);
                while(amountResultSet.next()){
                    amount = amountResultSet.getDouble("amount");
                }
            
                String query = "insert into MarketTransactions(type, aid, amount) values ('interest', ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, resultSet.getString("aid"));
                statement.setString(2, ""+amount); 
                statement.execute();
                label.setText("Interest added to all accounts.");
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

    @FXML
    void handleDeleteButtonAction(ActionEvent event) throws SQLException {
        Connection connection = null;
            PreparedStatement statement = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "delete from Transactions";
                statement = connection.prepareStatement(query);
                statement.execute();
                label.setText("All transactions deleted.");
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
    
    @FXML
    void handleHomeButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLManagerInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        label.setText("");
    }    
    
}
