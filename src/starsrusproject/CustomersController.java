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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CustomersController implements Initializable {
    CurrentUser currentUser = CurrentUser.getInstance();
    private ObservableList<ObservableList> accounts;
    private int currentTaxid;
    @FXML
    private Button monthlyReportButton;

    @FXML
    private TableView resultTable;

    @FXML
    private ListView<String> customerList;

    @FXML
    private CheckBox activeCustomersCheckbox;

    @FXML
    private Button customerReportButton;

    @FXML
    private Button homeButton;
    
    private ObservableList<String> customers;

    @FXML
    void handleMonthlyReportButton(ActionEvent event) throws SQLException {
        genMonthlyStatement();
    }

    @FXML
    void handleCustomerReportButtonAction(ActionEvent event) throws SQLException {
        genCustomerReport();
    }

    @FXML
    void handleHomeButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLManagerInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);

    }

    @FXML
    void handleActiveCustomersCheckboxAction(ActionEvent event) throws SQLException {
        if(activeCustomersCheckbox.isSelected()){
            Connection connection = null;
            Statement statement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);

                customers = FXCollections.observableArrayList();
                String query = "select cname from Customers C, Accounts A where C.taxid = A.owner and A.aid in "
                        + "(select S.aid from StockTransactions S, StockAccounts A where S.aid = A.aid group by S.aid having sum(S.numshares) > 1000) ";
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while(resultSet.next()) {
                    customers.add(resultSet.getString("cname"));
                }
                customerList.setItems(customers);


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
        else {
            initCustomerList();
        }
    }
    
    private void initCustomerList() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            customers = FXCollections.observableArrayList();
            String query = "select cname from Customers ";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                customers.add(resultSet.getString("cname"));
            }
            customerList.setItems(customers);
            
            
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
    
    private void genCustomerReport () throws SQLException {
        if (customerList.getSelectionModel().getSelectedItem() != null){
            Connection connection = null;
            Statement statement = null;
            resultTable.getItems().clear();
            resultTable.getColumns().clear();
            accounts = FXCollections.observableArrayList();
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);
                
                String query1 = "select C.taxid from Customers as C where C.cname = '" + customerList.getSelectionModel().getSelectedItem() + "'";
                statement = connection.createStatement();
                ResultSet resultSet1 = statement.executeQuery(query1);

                while(resultSet1.next()) {
                    currentTaxid = resultSet1.getInt("taxid");
                }

                String query = "select M.aid, M.type, M.balance, ' ' as symbol from MarketAccounts as M, Accounts as A where A.aid = M.aid and A.owner = '" + currentTaxid + "' union "
                        + "select S.aid, S.type, S.numshares as balance, S.symbol from StockAccounts as S, Accounts where Accounts.aid = S.aid and Accounts.owner = '" + currentTaxid + "'";
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

                    resultTable.getColumns().addAll(col); 
                }

                while(resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    accounts.add(row);
                }

                resultTable.setItems(accounts);
                resultTable.refresh();
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
    
    private void genMonthlyStatement() throws SQLException {
        if (customerList.getSelectionModel().getSelectedItem() != null){
            Connection connection = null;
            Statement statement = null;
            resultTable.getItems().clear();
            resultTable.getColumns().clear();
            accounts = FXCollections.observableArrayList();
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);
                
                String query1 = "select C.taxid from Customers as C where C.cname = '" + customerList.getSelectionModel().getSelectedItem() + "'";
                statement = connection.createStatement();
                ResultSet resultSet1 = statement.executeQuery(query1);

                while(resultSet1.next()) {
                    currentTaxid = resultSet1.getInt("taxid");
                }

                String query = "select M.tid, T.date, A.type as transaction_type, M.type as transaction, ' ' as symbol, M.amount from MarketTransactions as M, Accounts as A, Transactions as T where M.tid = T.tid and A.aid = M.aid and A.owner = '" + currentTaxid + "' union "
                        + "select S.tid, Transactions.date, Accounts.type as transaction_type, S.type as transaction,  S.symbol, S.numshares as amount from StockTransactions as S, Accounts, Transactions where S.tid = Transactions.tid and Accounts.aid = S.aid and Accounts.owner = '" + currentTaxid + "'";
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

                    resultTable.getColumns().addAll(col); 
                }

                while(resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    accounts.add(row);
                }

                resultTable.setItems(accounts);
                resultTable.refresh();
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            initCustomerList();
        } catch (SQLException ex) {
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
