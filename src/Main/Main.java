package Main;

import config.config;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        config db = new config();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Welcome to Book Recommendation App ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear newline buffer

            switch (choice) {
                case 1:
                    System.out.println("\n=== Login ===");
                    System.out.print("Enter Username: ");
                    String loginUn = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String loginPw = sc.nextLine();

                    boolean loggedIn = db.checkLogin(loginUn, loginPw);
                    if (loggedIn) {
                        System.out.println("✅ Login Successful! Welcome, " + loginUn);
                        userMenu(db, sc, loginUn);
                    } else {
                        System.out.println("❌ Login Failed! Invalid username or password.");
                    }
                    break;

                case 2:
                    System.out.println("\n=== User Registration ===");
                    System.out.print("Enter Username: ");
                    String un = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String pw = sc.nextLine();

                    System.out.print("Enter Email: ");
                    String em = sc.nextLine();

                    String sql = "INSERT INTO tbl_user (u_username, u_password, u_email) VALUES (?, ?, ?)";
                    int newUserId = db.addRecord(sql, un, pw, em);

                    if (newUserId != -1) {
                        System.out.println("🎉 Registration Successful! Your User ID is: " + newUserId);
                        System.out.println("\nPlease login to continue...");

                        System.out.print("Enter Username: ");
                        String regLoginUn = sc.nextLine();
                        System.out.print("Enter Password: ");
                        String regLoginPw = sc.nextLine();

                        boolean regLoggedIn = db.checkLogin(regLoginUn, regLoginPw);
                        if (regLoggedIn) {
                            System.out.println("✅ Login Successful! Welcome, " + regLoginUn);
                            userMenu(db, sc, regLoginUn);
                        } else {
                            System.out.println("❌ Login Failed! Invalid username or password.");
                        }
                    } else {
                        System.out.println("⚠️ Registration failed!");
                    }
                    break;

                case 3:
                    System.out.println("👋 Exiting... Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("⚠️ Invalid choice! Please select 1, 2, or 3.");
            }
        }
    }

    // 🧩 USER MENU
    public static void userMenu(config db, Scanner sc, String username) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Add Book Recommendation");
            System.out.println("2. View All Book Recommendations");
            System.out.println("3. Update Book Recommendation");
            System.out.println("4. Delete Book Recommendation"); // 🆕 NEW OPTION
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear newline buffer

            switch (choice) {
                case 1:
                    System.out.println("\n=== Add Book Recommendation ===");
                    System.out.print("Enter Book Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter Genre: ");
                    String genre = sc.nextLine();

                    int bookId = db.addBookRecommendation(title, author, genre, username);
                    if (bookId != -1)
                        System.out.println("📚 Book added successfully! (ID: " + bookId + ")");
                    else
                        System.out.println("⚠️ Failed to add book recommendation.");
                    break;

                case 2:
                    System.out.println("\n=== View All Book Recommendations ===");
                    String sql = "SELECT b_id, b_title, b_author, b_genre, b_user FROM tbl_books";
                    String[] headers = {"Book ID", "Title", "Author", "Genre", "Recommended By"};
                    String[] cols = {"b_id", "b_title", "b_author", "b_genre", "b_user"};
                    db.viewRecords(sql, headers, cols);
                    break;

                case 3:
                    System.out.println("\n=== Update Book Recommendation ===");
                    System.out.print("Enter Book ID to Update: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter New Title: ");
                    String newTitle = sc.nextLine();
                    System.out.print("Enter New Author: ");
                    String newAuthor = sc.nextLine();
                    System.out.print("Enter New Genre: ");
                    String newGenre = sc.nextLine();

                    String updateSQL = "UPDATE tbl_books SET b_title = ?, b_author = ?, b_genre = ? WHERE b_id = ?";
                    db.updateRecord(updateSQL, newTitle, newAuthor, newGenre, updateId);
                    break;

                case 4:
                    System.out.println("\n=== Delete Book Recommendation ===");
                    System.out.print("Enter Book ID to Delete: ");
                    int deleteId = sc.nextInt();

                    String deleteSQL = "DELETE FROM tbl_books WHERE b_id = ?";
                    db.deleteRecord(deleteSQL, deleteId); // ✅ Using your deleteRecord() method
                    break;

                case 5:
                    System.out.println("🔒 Logging out...");
                    return;

                default:
                    System.out.println("⚠️ Invalid choice! Try again.");
            }
        }
    }
}
