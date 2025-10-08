package config;

import java.sql.*;

public class config {

    // ✅ Connection Method to SQLITE
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:ben_BRS.db");
            System.out.println("✅ Connection Successful");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Connection Failed: " + e);
        }
        return con;
    }

    // ✅ Add Record Method
    public int addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < values.length; i++) {
                int idx = i + 1;
                Object val = values[i];

                if (val == null) pstmt.setNull(idx, java.sql.Types.NULL);
                else if (val instanceof Integer) pstmt.setInt(idx, (Integer) val);
                else if (val instanceof Double) pstmt.setDouble(idx, (Double) val);
                else if (val instanceof Float) pstmt.setFloat(idx, (Float) val);
                else if (val instanceof Long) pstmt.setLong(idx, (Long) val);
                else if (val instanceof Boolean) pstmt.setBoolean(idx, (Boolean) val);
                else if (val instanceof java.util.Date) pstmt.setDate(idx, new java.sql.Date(((java.util.Date) val).getTime()));
                else if (val instanceof java.sql.Date) pstmt.setDate(idx, (java.sql.Date) val);
                else if (val instanceof java.sql.Timestamp) pstmt.setTimestamp(idx, (java.sql.Timestamp) val);
                else if (val instanceof byte[]) pstmt.setBytes(idx, (byte[]) val);
                else pstmt.setString(idx, val.toString());
            }

            int rows = pstmt.executeUpdate();
            if (rows <= 0) {
                System.out.println("⚠️ No rows affected.");
                return -1;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs != null && rs.next()) {
                    int generatedId = rs.getInt(1);
                    System.out.println("✅ Record added successfully! Generated ID: " + generatedId);
                    return generatedId;
                }
            }

            try (Statement s = conn.createStatement();
                 ResultSet rs2 = s.executeQuery("SELECT last_insert_rowid()")) {
                if (rs2.next()) {
                    int lastId = rs2.getInt(1);
                    System.out.println("✅ Record added successfully! (fallback) Generated ID: " + lastId);
                    return lastId;
                }
            }

            return -1;
        } catch (SQLException e) {
            System.out.println("❌ Error adding record: " + e.getMessage());
            return -1;
        }
    }

    //-----------------------------------------------
    // ✅ UPDATE METHOD
    //-----------------------------------------------
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) pstmt.setInt(i + 1, (Integer) values[i]);
                else if (values[i] instanceof Double) pstmt.setDouble(i + 1, (Double) values[i]);
                else if (values[i] instanceof Float) pstmt.setFloat(i + 1, (Float) values[i]);
                else if (values[i] instanceof Long) pstmt.setLong(i + 1, (Long) values[i]);
                else if (values[i] instanceof Boolean) pstmt.setBoolean(i + 1, (Boolean) values[i]);
                else if (values[i] instanceof java.util.Date)
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
                else if (values[i] instanceof java.sql.Date) pstmt.setDate(i + 1, (java.sql.Date) values[i]);
                else if (values[i] instanceof java.sql.Timestamp) pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
                else pstmt.setString(i + 1, values[i].toString());
            }

            int affected = pstmt.executeUpdate();
            if (affected > 0)
                System.out.println("✅ Record updated successfully!");
            else
                System.out.println("⚠️ No record was updated.");
        } catch (SQLException e) {
            System.out.println("❌ Error updating record: " + e.getMessage());
        }
    }

    // ✅ Check Login Method
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM tbl_user WHERE u_username = ? AND u_password = ?";
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("❌ Error checking login: " + e.getMessage());
        }
        return false;
    }
    
    // ✅ Dynamic view method to display records from any table
public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
    // Check that columnHeaders and columnNames arrays are the same length
    if (columnHeaders.length != columnNames.length) {
        System.out.println("Error: Mismatch between column headers and column names.");
        return;
    }

    try (Connection conn = this.connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
         ResultSet rs = pstmt.executeQuery()) {

        // Print the headers dynamically
        StringBuilder headerLine = new StringBuilder();
        headerLine.append("--------------------------------------------------------------------------------\n| ");
        for (String header : columnHeaders) {
            headerLine.append(String.format("%-20s | ", header)); // Adjust formatting as needed
        }
        headerLine.append("\n--------------------------------------------------------------------------------");

        System.out.println(headerLine.toString());

        // Print the rows dynamically based on the provided column names
        while (rs.next()) {
            StringBuilder row = new StringBuilder("| ");
            for (String colName : columnNames) {
                String value = rs.getString(colName);
                row.append(String.format("%-20s | ", value != null ? value : "")); // Adjust formatting
            }
            System.out.println(row.toString());
        }
        System.out.println("--------------------------------------------------------------------------------");

    } catch (SQLException e) {
        System.out.println("Error retrieving records: " + e.getMessage());
    }
}

 // ✅ Add Book Recommendation
public int addBookRecommendation(String title, String author, String genre, String username) {
    String sql = "INSERT INTO tbl_books (b_title, b_author, b_genre, b_user) VALUES (?, ?, ?, ?)";
    return addRecord(sql, title, author, genre, username);
}

//-----------------------------------------------
// ✅ DELETE METHOD
//-----------------------------------------------
public void deleteRecord(String sql, Object... values) {
    try (Connection conn = this.connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Loop through the values and set them in the prepared statement dynamically
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]); // If the value is Integer
            } else {
                pstmt.setString(i + 1, values[i].toString()); // Default to String for other types
            }
        }

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0)
            System.out.println("🗑️ Record deleted successfully!");
        else
            System.out.println("⚠️ No record found to delete!");

    } catch (SQLException e) {
        System.out.println("❌ Error deleting record: " + e.getMessage());
    }
}

    
}
