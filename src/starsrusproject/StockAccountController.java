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
public class StockAccountController implements Initializable {
    
    CurrentUser currentUser = CurrentUser.getInstance();
    
    private int currentAid;
    
    private ObservableList<ObservableList> ownedStocks;
    private ObservableList<String> availableStocks;
    private ObservableList<String> purchasePrices;
    
    @FXML
    private ComboBox<String> stockComboBox;
    
    @FXML
    private ComboBox<String> ppComboBox;
    
    @FXML
    private TableView stocksTable;
    
    @FXML
    private Label errorLabel;

    @FXML
    private Button stockTransactionHistoryButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField quantityField;

    @FXML
    private Button confirmButton;

    @FXML
    void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLTraderInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }

    @FXML
    void handleConfirmButtonAction(ActionEvent event) throws SQLException {
        if(!stockComboBox.getSelectionModel().isEmpty() && !ppComboBox.getSelectionModel().isEmpty() && currentUser.marketIsOpen()){
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

                String query = "insert into StockTransactions(type, aid, symbol, numshares, purchaseprice) values (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, "sale");
                statement.setInt (2,currentAid);
                statement.setString(3, stockComboBox.getValue());
                statement.setInt(4, Integer.parseInt(quantityField.getText()));
                statement.setString(5, ppComboBox.getValue());
                statement.execute();
                
                initViews();
            } catch (SQLException e){
                errorLabel.setText("Not enough stocks.");
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }

            }
            ;
            quantityField.clear();
        }
        else {
            errorLabel.setText("Market is currently closed.");
        }
    }

    @FXML
    void handleStockHistoryButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLStockTransactionHistory.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }
    
    @FXML
    void handleSelectStock(ActionEvent event) throws SQLException {
        if(true){
            Connection connection = null;
            Statement statement = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);    
                purchasePrices = FXCollections.observableArrayList();
                String query4 = "select distinct P.purchaseprice from PurchasePrices as P, Accounts as A, StockAccounts as S "
                        + "where A.aid = P.aid and S.aid = A.aid and P.symbol = '" + stockComboBox.getValue() + "' and A.owner = '" + currentUser.gettaxid() + "'";
                statement = connection.createStatement();
                ResultSet resultSet4 = statement.executeQuery(query4);
                while(resultSet4.next()) {
                    purchasePrices.add(resultSet4.getString("purchaseprice"));
                }
                ppComboBox.setItems(purchasePrices);
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
    }
    
    
    private void initViews() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        stocksTable.getItems().clear();
        stocksTable.getColumns().clear();
        ownedStocks = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select S.aid, S.type, S.symbol, S.numshares from StockAccounts as S, Accounts as A where A.aid = S.aid and A.owner = '" + currentUser.gettaxid() + "'";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            for(int i=0 ; i<resultSet.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                stocksTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                ownedStocks.add(row);
            }
            
            stocksTable.setItems(ownedStocks);
            stocksTable.refresh();
            
            availableStocks = FXCollections.observableArrayList();
            String query2 = "select S.symbol from StockAccounts as S, Accounts as A where A.aid = S.aid and A.owner = '" + currentUser.gettaxid() + "'";
            statement = connection.createStatement();
            ResultSet resultSet2 = statement.executeQuery(query2);
            while(resultSet2.next()) {
                availableStocks.add(resultSet2.getString("symbol"));
            }
            stockComboBox.setItems(availableStocks);
            
            String query3 = "select S.aid from StockAccounts as S, Accounts as A where A.aid = S.aid and A.owner = '" + currentUser.gettaxid() + "'";
            statement = connection.createStatement();
            ResultSet resultSet3 = statement.executeQuery(query3);
            
            while(resultSet3.next()) {
                currentAid = resultSet3.getInt("aid");
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
        Pattern decimalPattern = Pattern.compile("\\d*?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (decimalPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        };   
        quantityField.setTextFormatter(new TextFormatter<>(filter));
        errorLabel.setText("");
        try {
            // TODO
            initViews();
        } catch (SQLException ex) {
            Logger.getLogger(MarketTransactionHistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
