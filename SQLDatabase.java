

package LoginServer;


import java.io.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author Marius
 */
public class SQLDatabase {

    protected String owner;
    protected Properties sqldata = new Properties();
    protected Connection con;
    protected Statement st;
    protected String ip, port, user, pass, database;


    public SQLDatabase(String createdby) {
        this.owner = createdby;
    }


    protected void debug(String msg) {
        Main.debug("["+owner+"]", msg);
    }

     /**
      * Loads the configs out of "configs/mysql.conf"
      */
    private void loadconfigs()
    {
        try
        {
        FileInputStream fin = new FileInputStream("configs/mysql.conf");
        sqldata.load(fin);
        fin.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        ip = sqldata.getProperty("5.73.69.243");
        port = sqldata.getProperty("3306");
        user = sqldata.getProperty("root");
        pass = sqldata.getProperty("test");
        database = sqldata.getProperty("bout_evolution");
    }

    public void start()
    {
        loadconfigs();

         /**
          * setup the basic connection
          */
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database, user,pass);
            st = con.createStatement();
            if (!con.equals(null))
                debug("SQL Connection started successful");
            //ResultSet rs = st.executeQuery("SELECT * FROM bout_users WHERE username='"+ user +"' LIMIT 1");
        }
        catch (Exception ex)
        {
            //debug("Error : " + ex.getMessage());
        }
    }

    public ResultSet doquery(String query)
    {
        ResultSet rs = null;
        try
        {
             /**
              * execute the query and return it
              */
            rs = st.executeQuery(query);
        }
        catch (Exception ex)
        {
            debug("Error (query): " + ex.getMessage());
        }
        return rs;
    }

    public void doupdate(String query)
    {
        try
        {
             /**
              * execute the updatequery
              */
            st.executeUpdate(query);
        }
        catch (Exception ex)
        {
            debug("Error (update): " + ex.getMessage());
        }
    }


}
