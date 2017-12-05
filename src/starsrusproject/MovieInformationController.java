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
public class MovieInformationController implements Initializable {
    
    private ObservableList<ObservableList> movies;
    private ObservableList<ObservableList> reviews;
    private String moviename;
    
    @FXML
    private Button topMovieButton;

    @FXML
    private TextField year1Field;

    @FXML
    private Button backButton;

    @FXML
    private TextField year2Field;

    @FXML
    private Pane reviewPane;

    @FXML
    private Label movieNameLabel;

    @FXML
    private TableView reviewsTable;

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView movieTable;
    
    @FXML
    private Button allMovieButton;


    @FXML
    private Button backToMoviesButton;
    
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
    
    private void initTable() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        movieTable.getItems().clear();
        movieTable.getColumns().clear();
        movies = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            String host= "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB";
            connection = DriverManager.getConnection(host, USER, PWD);
            
            String query = "select * from Movies";
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
                

                movieTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                movies.add(row);
            }
            movieTable.setRowFactory(tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 2 && (! row.isEmpty()) ) {
                    ObservableList<String> rowData = row.getItem();
                    moviename = rowData.get(1);
                    try {
                        displayReviewInfo();
                    } catch (SQLException ex) {
                        Logger.getLogger(StockMarketController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    changeStackPane();
                }
            });
            return row ;
            });
            
            movieTable.setItems(movies);
            movieTable.refresh();
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
    
    private void displayReviewInfo() throws SQLException {
        movieNameLabel.setText(moviename);
        
        
        Connection connection = null;
        Statement statement = null;
        reviewsTable.getItems().clear();
        reviewsTable.getColumns().clear();
        reviews = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            String host= "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB";
            connection = DriverManager.getConnection(host, USER, PWD);
            
            String query = "select Reviews.id, Reviews.author, Reviews.review from Reviews, Movies where Reviews.movie_id = Movies.id and Movies.title = '" + moviename + " ' ";
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
                

                reviewsTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                reviews.add(row);
            }
            
            reviewsTable.setItems(reviews);
            reviewsTable.refresh();
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
    void handleTopMovieButtonAction(ActionEvent event) throws SQLException {
        if(!year1Field.getText().trim().isEmpty() && !year2Field.getText().trim().isEmpty() ) {
            Connection connection = null;
        Statement statement = null;
        movieTable.getItems().clear();
        movieTable.getColumns().clear();
        movies = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            String host= "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB";
            connection = DriverManager.getConnection(host, USER, PWD);
            
            String query = "select * from Movies where Movies.rating = 5 and Movies.production_year >" + year1Field.getText() + " and Movies.production_year < " + year2Field.getText();
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
                

                movieTable.getColumns().addAll(col); 
            }
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                movies.add(row);
            }
            
            
            movieTable.setItems(movies);
            movieTable.refresh();
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

    @FXML
    void handleBackToMoviesButtonAction(ActionEvent event) {
        changeStackPane();
    }
    
    @FXML
    void handleAllMovieButtonAction(ActionEvent event) throws SQLException {
        initTable();
    }

    @FXML
    void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLTraderInterface.fxml"));
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
        Pattern decimalPattern = Pattern.compile("\\d*{4}");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (decimalPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        };   
        year1Field.setTextFormatter(new TextFormatter<>(filter));
        year2Field.setTextFormatter(new TextFormatter<>(filter));
        reviewPane.setVisible(false);
        try {
            initTable();
        } catch (SQLException ex) {
            Logger.getLogger(MovieInformationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
