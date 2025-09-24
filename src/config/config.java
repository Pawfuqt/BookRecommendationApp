package config;

import java.sql.*;

public class config {

    // Connection Method to SQLITE
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:ben_BRS.db"); // Establish connection
            System.out.println("✅ Connection Successful");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Connection Failed: " + e);
        }
        return con;
    }

    /**
     * Insert a record with type-aware binding and return the generated key (if any).
     * Returns generated id on success, or -1 on failure / if no generated key.
     */
    public int addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Bind parameters with type checks
            for (int i = 0; i < values.length; i++) {
                int idx = i + 1;
                Object val = values[i];

                if (val == null) {
                    // Unknown SQL type; use NULL. If you know the SQL type, consider using setNull(idx, Types.VARCHAR) etc.
                    pstmt.setNull(idx, java.sql.Types.NULL);
                } else if (val instanceof Integer) {
                    pstmt.setInt(idx, (Integer) val);
                } else if (val instanceof Double) {
                    pstmt.setDouble(idx, (Double) val);
                } else if (val instanceof Float) {
                    pstmt.setFloat(idx, (Float) val);
                } else if (val instanceof Long) {
                    pstmt.setLong(idx, (Long) val);
                } else if (val instanceof Boolean) {
                    pstmt.setBoolean(idx, (Boolean) val);
                } else if (val instanceof java.util.Date) {
                    pstmt.setDate(idx, new java.sql.Date(((java.util.Date) val).getTime()));
                } else if (val instanceof java.sql.Date) {
                    pstmt.setDate(idx, (java.sql.Date) val);
                } else if (val instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(idx, (java.sql.Timestamp) val);
                } else if (val instanceof byte[]) {
                    pstmt.setBytes(idx, (byte[]) val);
                } else {
                    pstmt.setString(idx, val.toString()); // fallback to string
                }
            }

            int rows = pstmt.executeUpdate();
            if (rows <= 0) {
                System.out.println("⚠️ No rows affected.");
                return -1;
            }

            // Try to read generated keys
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs != null && rs.next()) {
                    int generatedId = rs.getInt(1);
                    System.out.println("✅ Record added successfully! Generated ID: " + generatedId);
                    return generatedId;
                }
            }

            // Fallback for SQLite: if getGeneratedKeys() returned nothing, read last_insert_rowid()
            try {
                String dbProduct = conn.getMetaData().getDatabaseProductName().toLowerCase();
                if (dbProduct.contains("sqlite")) {
                    try (Statement s = conn.createStatement();
                         ResultSet rs2 = s.executeQuery("SELECT last_insert_rowid()")) {
                        if (rs2.next()) {
                            int lastId = rs2.getInt(1);
                            System.out.println("✅ Record added successfully! (fallback) Generated ID: " + lastId);
                            return lastId;
                        }
                    }
                }
            } catch (SQLException ignore) {
                // ignore fallback errors
            }

            // No generated key available
            return -1;

        } catch (SQLException e) {
            System.out.println("❌ Error adding record: " + e.getMessage());
            return -1;
        }
    }

    // ✅ Check login credentials
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM tbl_user WHERE u_username = ? AND u_password = ?";
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true if a match is found
            }

        } catch (SQLException e) {
            System.out.println("❌ Error checking login: " + e.getMessage());
        }
        return false;
    }
}
