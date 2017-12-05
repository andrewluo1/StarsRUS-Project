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
import java.util.ArrayList;
import java.util.Collection;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
public class StockMarketController implements Initializable {
    
    CurrentUser currentUser = CurrentUser.getInstance();
    private int currentAid;
    
    private ObservableList<ObservableList> stocks;
    private ObservableList<ObservableList> contracts;
    private ObservableList<String> availableStocks;
    
    private String actor;
    
    @FXML
    private Label adobLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label anameLabel;

    @FXML
    private ComboBox<String> stockComboBox;
    
    @FXML
    private Button returnButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField quantityField;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button confirmButton;

    @FXML
    private TableView marketTable;
    
    @FXML
    private Pane actorPane;

    @FXML
    private TableView contractTable;
    
    private void changeStackPane() {
        ObservableList<Node> children = stackPane.getChildren();
 
        if (children.size() > 1) {
           // Top Component
           Node topNode = children.get(children.size()-1);
           Node newTopNode = children.get(children.size()-2);
           topNode.setVisible(false);
           topNode.toBack();
           newTopNode.setVisible(true);
        }
    }
    
    @FXML
    void handleReturnButtonAction(ActionEvent event) {
        changeStackPane();
    }


    @FXML
    void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLTraderInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }

    @FXML
    void handleConfirmButtonAction(ActionEvent event) throws SQLException {
        if(!stockComboBox.getSelectionModel().isEmpty() && currentUser.marketIsOpen()){
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

                String query = "insert into StockTransactions(type, aid, symbol, numshares) values (?, ?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, "purchase");
                statement.setInt (2,currentAid);
                statement.setString(3, stockComboBox.getValue());
                statement.setInt(4, Integer.parseInt(quantityField.getText()));
                statement.execute();
                errorLabel.setStyle("-fx-text-fill: green");
                errorLabel.setText(quantityField.getText() + " " + stockComboBox.getValue() + " stocks successfully purchased.");
            } catch (SQLException e){
                errorLabel.setStyle("-fx-text-fill: red");
                errorLabel.setText("Insufficient funds.");
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
        else{
            errorLabel.setText("Market is currently closed.");
        }
    }
    
    private void displayActorInfo() throws SQLException {
        anameLabel.setText(actor);
        
        
        Connection connection = null;
        Statement statement = null;
        contractTable.getItems().clear();
        contractTable.getColumns().clear();
        contracts = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String dobquery = "select date_format(dob, '%m/%d/%Y') as dob from ActorStocks where ActorStocks.aname = '" + actor + " ' ";
            statement = connection.createStatement();
            ResultSet resultSetDOB = statement.executeQuery(dobquery);

            while(resultSetDOB.next()) {
                adobLabel.setText(resultSetDOB.getString("dob"));
            }
            
            String query = "select C.movie, C.role, C.year, C.totalvalue from Contracts as C where C.aname = '" + actor + "'";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            for(int i=0 ; i<resultSet.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table   
                int j = i;
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });
                

                contractTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                contracts.add(row);
            }
            
            contractTable.setItems(contracts);
            contractTable.refresh();
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
    
    private void initViews() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        marketTable.getItems().clear();
        stocks = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select A.symbol, A.aname, A.dailyclosingprice, A.currentprice from ActorStocks as A";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            for(int i=0 ; i<resultSet.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table   
                int j = i;
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });
                

                marketTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                stocks.add(row);
            }
            marketTable.setRowFactory( tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 2 && (! row.isEmpty()) ) {
                    ObservableList<String> rowData = row.getItem();
                    actor = rowData.get(1);
                    try {
                        displayActorInfo();
                    } catch (SQLException ex) {
                        Logger.getLogger(StockMarketController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    changeStackPane();
                }
            });
            return row ;
            });
            
            marketTable.setItems(stocks);
            marketTable.refresh();
            
            availableStocks = FXCollections.observableArrayList();
            String query2 = "select S.symbol from ActorStocks as S";
            statement = connection.createStatement();
            ResultSet resultSet2 = statement.executeQuery(query2);
            while(resultSet2.next()) {
                availableStocks.add(resultSet2.getString("symbol"));
            }
            stockComboBox.setItems(availableStocks);
            
            String query3 = "select A.aid from Accounts as A where A.type = 'stock' and A.owner = '" + currentUser.gettaxid() + "'";
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
        actorPane.setVisible(false);
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
            initViews();
        } catch (SQLException ex) {
            Logger.getLogger(StockMarketController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
