package be.planetegem.mammon.db;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import be.planetegem.mammon.Mammon;

public class DbConnection {
    Mammon parent;
    JDesktopPane ctx;
    JInternalFrame consoleContainer;
    JScrollPane scrollPane;
    JTextArea console;
    Connection conn;
    ArrayList<String> log;

    // Get timestamp for logging purposes
    public void logEvent(String event){
        String timestamp = "[" + new Timestamp(System.currentTimeMillis()) + "] ";
        log.add(timestamp + event);
        if (consoleContainer.isVisible()){
            console.append(timestamp + event + "\n");

            // scroll to bottom
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.addAdjustmentListener(new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent e){
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    bar.removeAdjustmentListener(this);
                }
            });
        }
    }

    // Convert resultset to arraylist/hashmap
    protected ArrayList<HashMap<String, String>> convertSQL(ResultSet query, boolean lowerCase) throws SQLException {
        ResultSetMetaData meta = query.getMetaData();
        int columnCount = meta.getColumnCount();
        
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

        while(query.next()){
            HashMap<String, String> currentLine = new HashMap<String, String>();
            for (int i = 1; i <= columnCount; i++){
                String name = meta.getColumnName(i);
                String value = query.getString(i);
                if (lowerCase){
                    name = name.toLowerCase();
                    if (value == null){
                        value= "";
                    }
                }
                currentLine.put(name, value);
            }
            result.add(currentLine);
        }
        return result;
    }
    // Clean resultset to avoid sql error
    protected HashMap<String, String> cleanSQL(HashMap<String, String> formData){
        for(Map.Entry<String, String> entry : formData.entrySet()){
            String value = entry.getValue();
            if (value.equals("")){
                value = "null";
            } else {
                value = "'" + value + "'";
            }
            entry.setValue(value);
        }
        return formData;
    }
    // drop table (for testing purposes)
    public void dropTable(String tableName) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE " + tableName);
            logEvent("Dropped table: " + tableName);
        } catch (SQLException e){
            logEvent("Exception while dropping " + tableName + " table: " + e.getErrorCode());
        }

    }

    protected DbConnection(){
        this.log = new ArrayList<String>();
        this.consoleContainer = new JInternalFrame("Database log", true, true, false, false);
        
        // Establish connection to db
        try {
            String dbUrl = "jdbc:derby:database;create=true";
            this.conn = DriverManager.getConnection(dbUrl);
            logEvent("Connection established");
        } catch (SQLException e){
            logEvent("Exception while establishing connection: " + e.getErrorCode());
        }
    }    
}
