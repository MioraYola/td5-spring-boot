package org.example.td5_spring_boo.datasource;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    public Connection connection(){
        try{
            Dotenv dotenv = Dotenv.load();
            String jdbcURl = dotenv.get("JDBC_URL");
            String user = dotenv.get("JDBC_USER");
            String password = dotenv.get("JDBC_PSWD");
            return DriverManager.getConnection(jdbcURl, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
