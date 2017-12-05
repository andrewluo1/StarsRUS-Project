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
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
import static starsrusproject.CurrentUser.HOST;
import static starsrusproject.CurrentUser.PWD;
import static starsrusproject.CurrentUser.USER;

/**
 * FXML Controller class
 *
 * @author Andrew
 */
public class MarketAccountController implements Initializable {
    CurrentUser currentUser = CurrentUser.getInstance();
    private int currentAid;
    
    @FXML
    private Label accountIdLabel;

    @FXML
    private TextField withdrawAmountField;

    @FXML
    private Button transactionHistoryButton;

    @FXML
    private Button withdrawButton;

    @FXML
    private TextField depositAmountField;

    @FXML
    private Button depositButton;

    @FXML
    private Label accountBalanceLabel;

    @FXML
    private Button homeButton;
    
    @FXML
    private Label errorLabel;

    @FXML
    void handleHomeButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLTraderInterface.fxml"));
        Parent root  = loader.load();
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Scene scene = new Scene(root);  
        primaryStage.setScene(scene);
    }

    @FXML
    void handleDepositButtonAction(ActionEvent event) throws SQLException {
        if(!depositAmountField.getText().trim().isEmpty()) {   
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "insert into MarketTransactions(type, aid, amount) values (?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, "deposit");
                statement.setInt (2,currentAid);
                statement.setDouble(3, Double.parseDouble(depositAmountField.getText()));
                statement.execute();
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
            errorLabel.setText("");
            updateAccountBalanceLabel();
            depositAmountField.clear();
        }
    }

    @FXML
    void handleWithdrawButtonAction(ActionEvent event) throws SQLException {
        if(!withdrawAmountField.getText().trim().isEmpty()) {   
            Connection connection = null;
            PreparedStatement statement = null;
            errorLabel.setText("");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "insert into MarketTransactions(type, aid, amount) values (?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, "withdrawal");
                statement.setInt (2,currentAid);
                statement.setDouble(3, Double.parseDouble(withdrawAmountField.getText()));
                statement.execute();
            } catch (SQLException e){
                errorLabel.setText("Insufficient funds.");
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            }
            updateAccountBalanceLabel();
            withdrawAmountField.clear();
        }
    }

    @FXML
    void handleTransactionHIstoryButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLMarketTransactionHistory.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }
    
    private void updateAccountBalanceLabel() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select M.balance from MarketAccounts as M, Accounts as A where A.aid = M.aid and A.owner = '" + currentUser.gettaxid() + "'";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                accountBalanceLabel.setText("$" + resultSet.getString("balance"));
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
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Pattern decimalPattern = Pattern.compile("\\d*(\\.\\d{0,2})?");

        UnaryOperator<Change> filter = c -> {
            if (decimalPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
        errorLabel.setText("");    
        depositAmountField.setTextFormatter(new TextFormatter<>(filter));
        withdrawAmountField.setTextFormatter(new TextFormatter<>(filter));
        
        Connection connection = null;
        Statement statement = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select M.aid from MarketAccounts as M, Accounts as A where A.aid = M.aid and A.owner = '" + currentUser.gettaxid() + "'";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                currentAid = resultSet.getInt("aid");
            }
            
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MarketAccountController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MarketAccountController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        accountIdLabel.setText("" + currentAid);
        try {
            updateAccountBalanceLabel();
        } catch (SQLException ex) {
            Logger.getLogger(MarketAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
