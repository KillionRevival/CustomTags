package com.flyerzrule.mc.customtags.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.flyerzrule.mc.customtags.CustomTags;

public class Database {
    private Connection connection;

    public Database(String name) {
        this.connection = this.createConnection(name);
    }

    private Connection createConnection(String name) {
        Connection conn = null;
        try {
            File dbFile = new File(CustomTags.getPlugin().getDataFolder(), name + ".db");

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            conn = DriverManager.getConnection(url);
            CustomTags.getPlugin().getLogger().info("Connected to the database");
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
        }
        return conn;
    }

    protected void executeQuery(String query) throws Exception {
        try {
            Statement stmt = this.connection.createStatement();
            stmt.execute(query);
            // System.out.println("Query executed successfully.");
        } catch (SQLException e) {
            throw new Exception("executeQuery failed!");
        }
    }

    public void executeUpdate(String query, Object... params) throws Exception {
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            pstmt.executeUpdate();
            // System.out.println("Update executed successfully.");
        } catch (SQLException e) {
            throw new Exception("executeUpdate failed!");
        }
    }

    protected ResultSet fetchQuery(String query, Object... params) throws Exception {
        ResultSet rs = null;
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
            // System.out.println("Fetch executed successfully.");
        } catch (SQLException e) {
            throw new Exception("fetchQuery failed!");
        }
        return rs;
    }

    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
                CustomTags.getPlugin().getLogger().info("Database Connection closed");
            }
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
        }
    }
}
