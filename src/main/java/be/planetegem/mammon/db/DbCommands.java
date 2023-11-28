package be.planetegem.mammon.db;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;

public class DbCommands extends DbConnection {
    public static int FAIL = 0;
    public static int CREATE = 1;
    public static int UPDATE = 2;

    // 1. profile interactions
    public void checkUsersTable() {
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, null,"USERS", new String[] {"TABLE"});
            
            if (!resultSet.next()){
                String query = "CREATE TABLE Users ("
                    + "userId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "companyName VARCHAR(45), vatNumber VARCHAR(25), accountNumber VARCHAR (25), "
                    + "familyName VARCHAR(45), firstName VARCHAR(45), logoPath VARCHAR(255), "
                    + "streetName VARCHAR(45), houseNumber VARCHAR(10), boxNumber VARCHAR(10), "
                    + "postalCode VARCHAR(10), placeName VARCHAR(45), countryName VARCHAR(45), "
                    + "UNIQUE (userId))";
                
                logEvent("Attempting:\n" + query);
                conn.createStatement().executeUpdate(query);
                logEvent("Table added: USERS");
            } else {
                logEvent("Table exists: USERS"); 
            }
        } catch (SQLException e){
            logEvent("Exception while creating USERS table: " + e.getErrorCode() + " " + e.getLocalizedMessage()); 
        }
    }

    public int addProfile(HashMap<String, String> formData){
        String query = 
            "INSERT INTO Users (" +
                "companyName, vatNumber, accountNumber, familyName, firstName," +
                "streetName, houseNumber, boxNumber, postalCode, placeName, countryName, logoPath" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        logEvent("Attempting to add profile");
                                
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, formData.get("companyName"));
            stmt.setString(2, formData.get("vatNumber"));
            stmt.setString(3, formData.get("accountNumber"));
            stmt.setString(4, formData.get("familyName"));
            stmt.setString(5, formData.get("firstName"));
            stmt.setString(6, formData.get("streetName"));
            stmt.setString(7, formData.get("houseNumber"));
            stmt.setString(8, formData.get("boxNumber"));
            stmt.setString(9, formData.get("postalCode"));
            stmt.setString(10, formData.get("placeName"));
            stmt.setString(11, formData.get("countryName"));
            stmt.setString(12, formData.get("logoPath"));

            stmt.executeUpdate();
            logEvent("Succesfully added new profile");
            
            return CREATE;
        } catch (SQLException e){
            logEvent("Exception while adding new profile: " + e.getErrorCode() + " " + e.getLocalizedMessage());
            return FAIL;
        }
    }

    public int editProfile(HashMap<String, String> formData, String userId){
        String query = 
            "UPDATE Users SET CompanyName=?, vatNumber=?, accountNumber=?, familyName=?, firstName=?, " +
            "streetName=?, houseNumber=?, boxNumber=?, placeName=?, postalCode=?, countryName=?, logoPath=? " + 
            "WHERE userId=?";
        logEvent("Attempting to edit profile " + userId);

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, formData.get("companyName"));
            stmt.setString(2, formData.get("vatNumber"));
            stmt.setString(3, formData.get("accountNumber"));
            stmt.setString(4, formData.get("familyName"));
            stmt.setString(5, formData.get("firstName"));
            stmt.setString(6, formData.get("streetName"));
            stmt.setString(7, formData.get("houseNumber"));
            stmt.setString(8, formData.get("boxNumber"));
            stmt.setString(9, formData.get("placeName"));
            stmt.setString(10, formData.get("postalCode"));
            stmt.setString(11, formData.get("countryName"));
            stmt.setString(12, formData.get("logoPath"));
            stmt.setInt(13, Integer.parseInt(userId));

            stmt.executeUpdate();
            logEvent("Succesfully edited profile " + userId);
            return UPDATE;
        } catch (SQLException e){
            logEvent("Exception while editing profile: " + e.getErrorCode() + " " + e.getLocalizedMessage());
            e.printStackTrace();
            return FAIL;
        }
    }

    public ArrayList<HashMap<String, String>> getProfiles() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet query = stmt.executeQuery("SELECT * FROM Users");
            logEvent("Retrieved profiles");
            return convertSQL(query, false);
        } catch (SQLException e){
            logEvent("Exception while retrieving profiles: " + e.getErrorCode() + " " + e.getLocalizedMessage()); 
        }
        return new ArrayList<HashMap<String, String>>();
    }

    public void removeProfile(String userId) {
        String query = "DELETE FROM Users WHERE UserId=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(userId));
            int update = stmt.executeUpdate();
            logEvent("Deleted " + update + " record(s): profile " + userId);
        } catch (SQLException e){
            logEvent("Exception while removing profile " + userId + ": " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
    }

    // 2. Customer interactions
    public void checkCustomersTable() {
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, null, "CUSTOMERS", new String[] {"TABLE"});
            
            if (!resultSet.next()){
                String query = "CREATE TABLE Customers ("
                    + "customerId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "companyName VARCHAR(45), vatNumber VARCHAR(25), vatNumber2 VARCHAR(25), "
                    + "familyName VARCHAR(45), firstName VARCHAR(45), customerType VARCHAR(255), "
                    + "streetName VARCHAR(45), houseNumber VARCHAR(10), boxNumber VARCHAR(10), "
                    + "postalCode VARCHAR(10), placeName VARCHAR(45), countryName VARCHAR(45), "
                    + "UNIQUE (customerId))";

                logEvent("Attempting:\n" + query);
                conn.createStatement().executeUpdate(query);
                logEvent("Table created: CUSTOMERS");
            } else {
                logEvent("Table exists: CUSTOMERS"); 
            }
        } catch (SQLException e){
             logEvent("Exception while creating CUSTOMERS table: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }        
    }

    public int addCustomer(HashMap<String, String> formData) {
        String query = 
            "INSERT INTO Customers (" +
                "companyName, vatNumber, vatNumber2, familyName, firstName, customerType, " +
                "streetName, houseNumber, boxNumber, postalCode, placeName, countryName" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        logEvent("Attempting to add customer");
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, formData.get("companyName"));
            stmt.setString(2, formData.get("vatNumber"));
            stmt.setString(3, formData.get("vatNumber2"));
            stmt.setString(4, formData.get("familyName"));
            stmt.setString(5, formData.get("firstName"));
            stmt.setString(6, formData.get("customerType"));
            stmt.setString(7, formData.get("streetName"));
            stmt.setString(8, formData.get("houseNumber"));
            stmt.setString(9, formData.get("boxNumber"));
            stmt.setString(10, formData.get("postalCode"));
            stmt.setString(11, formData.get("placeName"));
            stmt.setString(12, formData.get("countryName"));

            stmt.executeUpdate();
            logEvent("Succesfully added new customer");
            return CREATE;
            
        } catch (SQLException e){
            logEvent("Exception while adding new customer: " + e.getErrorCode() + " " + e.getLocalizedMessage());
            return FAIL;
        }
    }

    public int editCustomer(HashMap<String, String> formData, String customerId){
        String query = 
            "UPDATE Customers SET CompanyName=?, vatNumber=?, vatNumber2=?, familyName=?, firstName=?, customerType=?, " +
            "streetName=?, houseNumber=?, boxNumber=?, postalCode=?, placeName=?, countryName=? " + 
            "WHERE customerId=?";
        logEvent("Attempting to edit customer " + customerId);

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, formData.get("companyName"));
            stmt.setString(2, formData.get("vatNumber"));
            stmt.setString(3, formData.get("vatNumber2"));
            stmt.setString(4, formData.get("familyName"));
            stmt.setString(5, formData.get("firstName"));
            stmt.setString(6, formData.get("customerType"));
            stmt.setString(7, formData.get("streetName"));
            stmt.setString(8, formData.get("houseNumber"));
            stmt.setString(9, formData.get("boxNumber"));
            stmt.setString(10, formData.get("postalCode"));
            stmt.setString(11, formData.get("placeName"));
            stmt.setString(12, formData.get("countryName"));
            stmt.setInt(13, Integer.parseInt(customerId));

            stmt.executeUpdate();
            logEvent("Succesfully edited customer with customerId " + customerId);
            return UPDATE;
        } catch (SQLException e){
            logEvent("Exception while editing customer with customerId " + customerId + ": " + e.getErrorCode());
            return FAIL;
        }
    }

    public ArrayList<HashMap<String, String>> getCustomers(){
        try {
            Statement stmt = conn.createStatement();
            ResultSet query = stmt.executeQuery("SELECT * FROM Customers");
            logEvent("Retrieved customers");
            return convertSQL(query, false);
        } catch (SQLException e){
            logEvent("Exception while retrieving customers: " + e.getErrorCode() + " " + e.getLocalizedMessage()); 
        }
        return new ArrayList<HashMap<String, String>>();
    }

    public void removeCustomer(String customerId) {
        String query = "DELETE FROM Customers WHERE customerId=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(customerId));
            int update = stmt.executeUpdate();
            logEvent("Deleted " + update + " record(s): customer " + customerId);
        } catch (SQLException e){
            logEvent("Exception while removing customer " + customerId + ": " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
    }

    // 3. Invoice interactions
    public void checkInvoicesTable(){
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, null, "INVOICES", new String[] {"TABLE"});
            
            if (!resultSet.next()){
                String query = "CREATE TABLE Invoices ("
                    + "invoiceId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "invoiceNumber VARCHAR(45), invoiceYear INTEGER, "
                    + "profileId INTEGER, customerId INTEGER, "
                    + "invoiceDate VARCHAR(15), invoicePlace VARCHAR(45), "
                    + "invoiceType VARCHAR(25), vatType VARCHAR(25), realType VARCHAR(15), "
                    + "invoiceTerm VARCHAR(10), invoiceAccount VARCHAR(45), "
                    + "invoicePaid BOOLEAN, UNIQUE (invoiceId))";
                
                logEvent("Attempting:\n" + query);
                conn.createStatement().executeUpdate(query);
                logEvent("Table created: INVOICES");
            } else {
                logEvent("Table exists: INVOICES"); 
            }
        } catch (SQLException e){
            logEvent("Exception while creating INVOICES table: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
    }

    public int addInvoice(HashMap<String, String> formData){
        String query = 
            "SELECT * FROM Invoices WHERE profileId=? AND invoiceNumber=? AND realtype=?";
        logEvent("Checking if invoice already exists");

        try {
            PreparedStatement stmt2 = conn.prepareStatement(query);
            stmt2.setInt(1, Integer.parseInt(formData.get("profileId")));
            stmt2.setString(2, formData.get("invoiceNumber"));
            stmt2.setString(3, formData.get("realType"));

            // 1. check if combination of invoiceNumber & profileID already exists
            ResultSet rs = stmt2.executeQuery();
            ArrayList<HashMap<String, String>> results = convertSQL(rs, false);
            
            if (results.size() > 0){
                logEvent("Duplicate invoice found");
                logEvent("Updating invoice");

                query = "UPDATE Invoices SET " +
                    "customerId=?, invoiceDate=?, invoicePlace=?, invoiceType=?, invoiceTerm=?, invoiceAccount=?, vatType=? " +
                    "WHERE invoiceId=?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(formData.get("customerId")));
                stmt.setString(2, formData.get("invoiceDate"));
                stmt.setString(3, formData.get("invoicePlace"));
                stmt.setString(4, formData.get("invoiceType"));
                stmt.setString(5, formData.get("invoiceTerm"));
                stmt.setString(6, formData.get("invoiceAccount"));
                stmt.setString(7, formData.get("vatType"));
                stmt.setInt(8, Integer.parseInt(results.get(0).get("INVOICEID")));
                stmt.executeUpdate();
                return UPDATE;
            } else {
                logEvent("No invoice found");
                logEvent("Creating new invoice");

                query = "INSERT INTO Invoices (" +
                                "invoiceNumber, invoiceYear, profileId, customerId, invoiceDate, invoicePlace, " +
                                "invoiceType, invoiceTerm, invoiceAccount, invoicePaid, vatType, realType" +
                            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, formData.get("invoiceNumber"));
                stmt.setInt(2, (Year.now().getValue() % 100));
                stmt.setInt(3, Integer.parseInt(formData.get("profileId")));
                stmt.setInt(4, Integer.parseInt(formData.get("customerId")));
                stmt.setString(5, formData.get("invoiceDate"));
                stmt.setString(6, formData.get("invoicePlace"));
                stmt.setString(7, formData.get("invoiceType"));
                stmt.setString(8, formData.get("invoiceTerm"));
                stmt.setString(9, formData.get("invoiceAccount"));
                stmt.setBoolean(10, false);
                stmt.setString(11, formData.get("vatType"));
                stmt.setString(12, formData.get("realType"));
                stmt.executeUpdate();
                return CREATE;
            }
        } catch (SQLException e){
            logEvent("Exception while inserting/updating invoice: " + e.getErrorCode() + " " + e.getLocalizedMessage());
            return FAIL;
        }
    }

    public ArrayList<HashMap<String, String>> getInvoices(String profileId) {
        String query = "SELECT * FROM Invoices WHERE profileId=?";
        logEvent("Retrieving invoices for profile " + profileId);

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(profileId));
            ResultSet result = stmt.executeQuery();
            return convertSQL(result, false);

        } catch (SQLException e){
            logEvent("Exception while retrieving invoices for profile " + profileId + ": " + e.getErrorCode() + " " + e.getLocalizedMessage()); 
        }
        return new ArrayList<HashMap<String, String>>();
    }

    public ArrayList<HashMap<String, String>> getPositiveInvoices(String profileId){
        String query = "SELECT * FROM Invoices WHERE profileId=? AND realType='POSITIVE'";
        logEvent("Retrieving positive invoices for profile " + profileId);

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(profileId));
            ResultSet result = stmt.executeQuery();
            return convertSQL(result, false);
            
        } catch (SQLException e){
            logEvent("Exception while retrieving positive invoices for profile " + profileId + ": " + e.getErrorCode() + " " + e.getLocalizedMessage()); 
        }
        return new ArrayList<HashMap<String, String>>();
    }

    public int getInvoiceNumber(String profileId) {
        String query = 
            "SELECT COUNT(*) AS COUNT FROM Invoices WHERE profileId=? AND invoiceYear=? AND realType='POSITIVE'";
        logEvent("Counting invoices for profile " + profileId);

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(profileId));
            stmt.setInt(2, (Year.now().getValue() % 100));
            ResultSet result = stmt.executeQuery();

            int proposedNumber = 1;
            while(result.next()) {
                proposedNumber = result.getInt("COUNT");
            }
            logEvent("counted " + proposedNumber + " invoices for profile " + profileId);
            return proposedNumber + 1;
        } catch (SQLException e){
            logEvent("Exception while counting invoices " + profileId + ": " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
        return 1;
    }

    public int getInvoiceId(String profileId, String invoiceNumber, String realType) {
        String query = 
            "SELECT InvoiceId FROM Invoices WHERE profileId=? AND invoiceNumber=? AND realType=?";
        logEvent("Retrieving invoiceId");

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(profileId));
            stmt.setString(2, invoiceNumber);
            stmt.setString(3, realType);
            ResultSet result = stmt.executeQuery();
            result.next();
            int invoiceId = result.getInt("InvoiceId");
            logEvent("Found invoiceId: " + invoiceId);
            return invoiceId;

        } catch (SQLException e){
            logEvent("Exception while retrieving invoiceId: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
        return FAIL;
    }

    //4. Invoiceline interactions
    public void checkInvoiceLinesTable() {
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, null, "INVOICELINES", new String[] {"TABLE"});
            
            if (!resultSet.next()){
                String query = "CREATE TABLE InvoiceLines ("
                    + "InvoiceLineId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "invoiceId INTEGER, description VARCHAR(255), "
                    + "date VARCHAR(15), amount VARCHAR(15), "
                    + "unit VARCHAR(15), price VARCHAR(15), "
                    + "UNIQUE (InvoiceLineId))";
                
                logEvent("Attempting:\n" + query);
                conn.createStatement().executeUpdate(query);
                logEvent("Table created: INVOICELINES");
            } else {
                logEvent("Table exists: INVOICELINES"); 
            }
        } catch (SQLException e){
            logEvent("Exception while creating INVOICELINES table: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
    }

    public void clearInvoiceLines(HashMap<String, String> formData) {
        String query = "DELETE FROM InvoiceLines WHERE invoiceId=?";
        logEvent("Cleaning invoice lines for invoice " + formData.get("invoiceId"));
    
        try {
            PreparedStatement delete = conn.prepareStatement(query);
            delete.setInt(1, Integer.parseInt(formData.get("invoiceId")));
            int update = delete.executeUpdate();
            logEvent("Cleared " + update + " lines");
        } catch (SQLException e){
            logEvent("Exception while clearing invoice lines: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
    }

    public int addInvoiceLine(HashMap<String, String> formData) {
        String query = 
            "INSERT INTO InvoiceLines (" +
            "invoiceId, description, date, amount, unit, price" +
            ") VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(formData.get("invoiceId")));
            stmt.setString(2, formData.get("description"));
            stmt.setString(3, formData.get("date"));
            stmt.setString(4, formData.get("amount"));
            stmt.setString(5, formData.get("unit"));
            stmt.setString(6, formData.get("price"));
            stmt.executeUpdate();
            logEvent("Invoice lines succesfully added");
            return CREATE;
        } catch (SQLException e) {
            logEvent("Exception while adding invoice lines: " + e.getErrorCode() + " " + e.getLocalizedMessage());
            return FAIL;
        }       
    }
    
    public ArrayList<HashMap<String, String>> getInvoiceLines(String invoiceId) {
        String query = "SELECT * FROM InvoiceLines WHERE invoiceId=?";
        logEvent("Attempting to retrieve invoice lines for invoice " + invoiceId);
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(invoiceId));
            ResultSet result = stmt.executeQuery();
            logEvent("Found invoice lines");
            return convertSQL(result, true);
        } catch (SQLException e){
            logEvent("Exception while adding invoice lines: " + e.getErrorCode() + " " + e.getLocalizedMessage());
        }
        return new ArrayList<HashMap<String, String>>();
    }
    


}
