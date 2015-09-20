package com.almasb.java.framework.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// MUST HAVE derby.jar && derbytools.jar in CP

public class DerbyDemo {

    private static final String URLdb = "jdbc:derby:derbytest.db";
    //private static final String URLdbCreate = "jdbc:derby:derbytest.db;create=true";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static void main(String[] args) throws Exception {

        Class.forName(DRIVER).newInstance();
        Connection conn = DriverManager.getConnection(URLdb, "almas", "test");

        // when creating
        //Connection conn = DriverManager.getConnection(URLdbCreate, "almas", "test");

        Statement stmt = conn.createStatement();

        //stmt.execute("CREATE TABLE TestTable (name Char(10), age Float)");
        /*stmt.execute("insert into TestTable values ('John Doe', 30)");
        stmt.execute("insert into TestTable values ('Jane Doe', 32)");
        stmt.execute("insert into TestTable values ('John Smi', 50)");
        stmt.execute("insert into TestTable values ('John Cal', 20)");
        stmt.execute("insert into TestTable values ('John Koe', 35)");*/

        ResultSet result = stmt.executeQuery("select * from TestTable");
        while (result.next()) {
            String user = result.getString("name");
            String age = result.getString("age");
            System.out.printf("User: %s Age: %s\n", user, age);
        }


        result.close();
        stmt.close();
        conn.close();
    }
}
