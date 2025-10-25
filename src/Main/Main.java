package Main;

import config.config;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        config con = new config();

        while (true) {
            System.out.println("\n=== BOOK RATING SYSTEM ===");
            System.out.println("[1] Login as User");
            System.out.println("[2] Register as User");
            System.out.println("[3] Login as Admin");
            System.out.println("[4] Register as Admin");
            System.out.println("[5] Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    userLogin(con, sc);
                    break;
                case 2:
                    registerUser(con, sc);
                    break;
                case 3:
                    adminLogin(con, sc);
                    break;
                case 4:
                    registerAdmin(con, sc);
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice, please try again!");
                    break;
            }
        }
    }

    // ================= USER FUNCTIONS =================

    private static void registerUser(config con, Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        // Check if email already exists
        String checkEmailSQL = "SELECT COUNT(*) FROM tbl_user WHERE LOWER(u_email) = LOWER(?)";
        double count = con.getSingleValue(checkEmailSQL, email);

        if (count > 0) {
            System.out.println("\n⚠️ That email is already in use. Please use another email.\n");
            return;
        }

        String hashedPass = config.hashPassword(password);
        String sql = "INSERT INTO tbl_user (u_username, u_email, u_password, u_type, u_status) VALUES (?, ?, ?, ?, ?)";
        con.addRecord(sql, username, email, hashedPass, "User", "Pending");

        System.out.println("\n✅ Registration successful! Please wait for admin approval.\n");
    }

    private static void userLogin(config con, Scanner sc) {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String hashedPass = config.hashPassword(password);
        String sql = "SELECT * FROM tbl_user WHERE u_email = ? AND u_password = ?";
        java.util.List<java.util.Map<String, Object>> result = con.fetchRecords(sql, email, hashedPass);

        if (!result.isEmpty()) {
            String status = result.get(0).get("u_status").toString();

            if (status.equalsIgnoreCase("Active")) {
                System.out.println("\n✅ Welcome, " + result.get(0).get("u_username") + "!\n");
                userMenu(con, sc, result.get(0).get("user_id"));
            } else if (status.equalsIgnoreCase("Pending")) {
                System.out.println("\n⚠️ Your account is still pending admin approval.\n");
            } else if (status.equalsIgnoreCase("Declined")) {
                System.out.println("\n❌ Your account has been declined by admin.\n");
            }
        } else {
            System.out.println("\n❌ Invalid user credentials.\n");
        }
    }

    private static void userMenu(config con, Scanner sc, Object userId) {
    while (true) {
        System.out.println("\n=== USER MENU ===");
        System.out.println("[1] View Books");
        System.out.println("[2] Add Book");
        System.out.println("[3] Update Book");
        System.out.println("[4] Rate a Book");
        System.out.println("[5] View Ratings");
        System.out.println("[6] Logout");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                viewBooks(con);
                break;
            case 2:
                addBook(con, sc); // ✅ Allow user to add book
                break;
            case 3:
                updateBook(con, sc); // ✅ Allow user to update book
                break;
            case 4:
                rateBook(con, sc, userId);
                break;
            case 5:
                viewRatings(con);
                break;
            case 6:
                System.out.println("Logging out...");
                return;
            default:
                System.out.println("Invalid option!");
                break;
        }
    }
}

        
    

    private static void viewBooks(config con) {
        String sql = "SELECT b_id, b_title, b_author, b_genre FROM tbl_books";
        String[] headers = {"Book ID", "Title", "Author", "Genre"};
        String[] cols = {"b_id", "b_title", "b_author", "b_genre"};
        con.viewRecords(sql, headers, cols);
    }

    private static void rateBook(config con, Scanner sc, Object userId) {
        viewBooks(con);
        System.out.print("Enter the Book ID to rate: ");
        int bookId = sc.nextInt();
        System.out.print("Enter rating (1–10): ");
        int rating = sc.nextInt();
        sc.nextLine();

        // Insert with current date
        String sql = "INSERT INTO tbl_ratings (user_id, book_id, rating, r_date) VALUES (?, ?, ?, DATE('now'))";
        con.addRecord(sql, userId, bookId, rating);

        System.out.println("\n✅ Rating submitted successfully!\n");
    }

    private static void viewRatings(config con) {
        String sql = "SELECT r.r_id, u.u_username, b.b_title, r.rating, r.r_date " +
                     "FROM tbl_ratings r " +
                     "LEFT JOIN tbl_user u ON r.user_id = u.user_id " +
                     "LEFT JOIN tbl_books b ON r.book_id = b.b_id";
        String[] headers = {"Rating ID", "User", "Book", "Rating", "Date Rated"};
        String[] cols = {"r_id", "u_username", "b_title", "rating", "r_date"};
        con.viewRecords(sql, headers, cols);
    }

    // ================= ADMIN FUNCTIONS =================

    private static void registerAdmin(config con, Scanner sc) {
        System.out.print("Enter admin username: ");
        String username = sc.nextLine();
        System.out.print("Enter admin email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String checkEmailSQL = "SELECT COUNT(*) FROM tbl_admin WHERE LOWER(a_email) = LOWER(?)";
        double count = con.getSingleValue(checkEmailSQL, email);

        if (count > 0) {
            System.out.println("\n⚠️ That email is already in use. Please use another email.\n");
            return;
        }

        String hashedPass = config.hashPassword(password);
        String sql = "INSERT INTO tbl_admin (a_user, a_email, a_password) VALUES (?, ?, ?)";
        con.addRecord(sql, username, email, hashedPass);

        System.out.println("\n✅ Admin registered successfully!\n");
    }

    private static void adminLogin(config con, Scanner sc) {
        System.out.print("Enter admin email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String hashedPass = config.hashPassword(password);
        String sql = "SELECT * FROM tbl_admin WHERE a_email = ? AND a_password = ?";
        java.util.List<java.util.Map<String, Object>> result = con.fetchRecords(sql, email, hashedPass);

        if (!result.isEmpty()) {
            System.out.println("\n✅ Welcome Admin " + result.get(0).get("a_user") + "!\n");
            adminMenu(con, sc);
        } else {
            System.out.println("\n❌ Invalid admin credentials.\n");
        }
    }

    private static void adminMenu(config con, Scanner sc) {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("[1] View All Users");
            System.out.println("[2] Approve/Decline Pending Users");
            System.out.println("[3] Add Book");
            System.out.println("[4] Update Book");
            System.out.println("[5] Delete Book");
            System.out.println("[6] View Ratings");
            System.out.println("[7] Logout");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewUsers(con);
                    break;
                case 2:
                    approveDeclineUsers(con, sc);
                    break;
                case 3:
                    addBook(con, sc);
                    break;
                case 4:
                    updateBook(con, sc);
                    break;
                case 5:
                    deleteBook(con, sc);
                    break;
                case 6:
                    viewRatings(con);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static void viewUsers(config con) {
        String sql = "SELECT user_id, u_username, u_email, u_type, u_status FROM tbl_user";
        String[] headers = {"ID", "Username", "Email", "Type", "Status"};
        String[] cols = {"user_id", "u_username", "u_email", "u_type", "u_status"};
        con.viewRecords(sql, headers, cols);
    }

    private static void approveDeclineUsers(config con, Scanner sc) {
        System.out.println("\n=== PENDING USERS ===");
        String sql = "SELECT user_id, u_username, u_email, u_status FROM tbl_user WHERE u_status = 'Pending'";
        String[] headers = {"ID", "Username", "Email", "Status"};
        String[] cols = {"user_id", "u_username", "u_email", "u_status"};
        con.viewRecords(sql, headers, cols);

        System.out.print("\nEnter User ID to update status: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Approve or Decline (A/D): ");
        String decision = sc.nextLine().trim().toUpperCase();

        String newStatus = decision.equals("A") ? "Active" : "Declined";
        String updateSQL = "UPDATE tbl_user SET u_status = ? WHERE user_id = ?";
        con.updateRecord(updateSQL, newStatus, id);

        System.out.println("\n✅ User status updated to: " + newStatus);
    }

    private static void addBook(config con, Scanner sc) {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Genre: ");
        String genre = sc.nextLine();

        String sql = "INSERT INTO tbl_books (b_title, b_author, b_genre) VALUES (?, ?, ?)";
        con.addRecord(sql, title, author, genre);
        System.out.println("\n✅ Book added successfully!");
    }

    private static void updateBook(config con, Scanner sc) {
        viewBooks(con);
        System.out.print("Enter Book ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new title: ");
        String title = sc.nextLine();
        System.out.print("Enter new author: ");
        String author = sc.nextLine();
        System.out.print("Enter new genre: ");
        String genre = sc.nextLine();

        String sql = "UPDATE tbl_books SET b_title = ?, b_author = ?, b_genre = ? WHERE b_id = ?";
        con.updateRecord(sql, title, author, genre, id);
        System.out.println("\n✅ Book updated successfully!");
    }

    private static void deleteBook(config con, Scanner sc) {
        viewBooks(con);
        System.out.print("Enter Book ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_books WHERE b_id = ?";
        con.deleteRecord(sql, id);
        System.out.println("\n✅ Book deleted successfully!");
    }
}
