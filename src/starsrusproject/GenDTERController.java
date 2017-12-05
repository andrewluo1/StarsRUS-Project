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
public class GenDTERController implements Initializable {
    
    private ObservableList<ObservableList> customers;
    
    @FXML
    private TableView reportTable;

    @FXML
    private Button backButton;

    @FXML
    void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLManagerInterface.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
    }
    
    private void initTable() throws SQLException {
        Connection connection = null;
            Statement statement = null;
            reportTable.getItems().clear();
            reportTable.getColumns().clear();
            customers = FXCollections.observableArrayList();
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(HOST, USER, PWD);
                
                String query = "select C.cname, C.address, C.state from Customers C where C.taxid in "
                        + "(select A.owner from StockTransactions S, Accounts A where A.aid = S.aid and S.type = 'sale' group by S.type, A.owner having sum(S.numshares *S.saleprice) - sum(S.numshares * purchaseprice) > 10000)";
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

                    reportTable.getColumns().addAll(col); 
                }

                while(resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    customers.add(row);
                }

                reportTable.setItems(customers);
                reportTable.refresh();
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
        try {
            // TODO
            initTable();
        } catch (SQLException ex) {
            Logger.getLogger(GenDTERController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
