/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starsrusproject;

import java.sql.*;

/**
 *
 * @author Andrew
 */
public class CurrentUser{
    private static final CurrentUser instance = new CurrentUser();
    public static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/andrewluoDB";
    public static final String USER = "andrewluo";
    public static final String PWD = "134";
    
    private String taxid;
    private String cname;
    private String currentdate;
    private boolean marketOpen = true;
    
    private CurrentUser() {
        this.taxid = "0000";
        this.cname = "";
    }
    
    public static CurrentUser getInstance() {
        return instance;
    }
    
    public String getCurrentDate(){
        return this.currentdate;
    }
    
    public boolean marketIsOpen() {
        return marketOpen;
    }
    
    public void changeMarketStatus() {
        marketOpen = !marketOpen;
    }
    
    public void setCurrentDate() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select date_format(currentDate, '%m/%d/%Y') as thisdate from CurrentDate where CurrentDate.restriction = ' ' ";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                this.currentdate = resultSet.getString("thisdate");
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
    
    public String getcname(){
        return cname;
    }
    
    
    public void setcname(String name){
        this.cname = name;
    }
    
    public String gettaxid() {
        return this.taxid;
    }
    
    public void settaxid() throws SQLException{
        Connection connection = null;
        Statement statement = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            
            connection = DriverManager.getConnection(HOST, USER, PWD);
            
            String query = "select C.taxid from Customers as C where C.cname = '" + cname + "'";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                this.taxid = resultSet.getString("taxid");
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
    
}
