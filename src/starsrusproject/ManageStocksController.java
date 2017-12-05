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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import static starsrusproject.CurrentUser.HOST;
import static starsrusproject.CurrentUser.PWD;
import static starsrusproject.CurrentUser.USER;

/**
 * FXML Controller class
 *
 * @author Andrew
 */
public class ManageStocksController implements Initializable {
    CurrentUser currentUser = CurrentUser.getInstance();
    
    private ObservableList<String> stocks;
    
    @FXML
    private Button closeMarketButton;

    @FXML
    private ComboBox<String> stockComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private Button openMarketButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button changeDateButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button homeButton;
    
    @FXML
    private Label label;

    @FXML
    void handleHomeButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLManagerInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }

    @FXML
    void handleConfirmButtonAction(ActionEvent event) throws SQLException {
        if(!priceField.getText().trim().isEmpty() && !stockComboBox.getSelectionModel().isEmpty()){
            Connection connection = null;
            PreparedStatement statement = null;
            label.setText("");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "update ActorStocks set currentprice = ? where ActorStocks.symbol = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, priceField.getText());
                statement.setString(2, stockComboBox.getValue());
                statement.execute();
                label.setText(stockComboBox.getValue() + " stock price set to " + priceField.getText());
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
            ;
            priceField.clear();
        }
    }

    @FXML
    void handleOpenMarketButtonAction(ActionEvent event) throws SQLException {
        if(!currentUser.marketIsOpen())  { 
            currentUser.changeMarketStatus();
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "update CurrentDate set currentDate = date_add(currentDate, interval 1 day)";
                statement = connection.prepareStatement(query);
                statement.execute();
                label.setText("Market opened for the day.");
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            };
        }
        else {
            label.setText("Market is already open.");
        }
    }

    @FXML
    void handleCloseMarketButtonAction(ActionEvent event) throws SQLException {
        if(currentUser.marketIsOpen())  { 
            currentUser.changeMarketStatus();
            Connection connection = null;
            PreparedStatement statement = null;
            label.setText("");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "update ActorStocks set dailyClosingPrice = currentPrice";
                statement = connection.prepareStatement(query);
                statement.execute();
                label.setText("Market closed for the day. Daily closing price updated.");
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            };
        }
        else {
            label.setText("Market is already closed.");
        }
    }

    @FXML
    void handleChangeDateButtonAction(ActionEvent event) throws SQLException {
        if(datePicker.getValue() != null){
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                String query = "update CurrentDate set currentDate = '" +  datePicker.getValue() + "'";
                statement = connection.prepareStatement(query);
                statement.execute();
                label.setText("Date changed to " + datePicker.getValue() + ".");
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            };
        }
    }
    
    private void initViews() throws SQLException{
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            stocks = FXCollections.observableArrayList();
            String query = "select S.symbol from ActorStocks as S";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                stocks.add(resultSet.getString("symbol"));
            }
            stockComboBox.setItems(stocks);
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
        Pattern decimalPattern = Pattern.compile("\\d*(\\.\\d{0,2})?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (decimalPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
        label.setText("");    
        priceField.setTextFormatter(new TextFormatter<>(filter));
        try {
            // TODO
            initViews();
        } catch (SQLException ex) {
            Logger.getLogger(ManageStocksController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
