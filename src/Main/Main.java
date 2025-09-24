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

            switch (choice) {
                case 1:
                    // === LOGIN ===
                    System.out.println("\n=== Login ===");
                    System.out.print("Enter Username: ");
                    String loginUn = sc.next();

                    System.out.print("Enter Password: ");
                    String loginPw = sc.next();

                    boolean loggedIn = db.checkLogin(loginUn, loginPw);
                    if (loggedIn) {
                        System.out.println("✅ Login Successful! Welcome, " + loginUn);
                        // TODO: Call other features here after successful login
                    } else {
                        System.out.println("❌ Login Failed! Invalid username or password.");
                    }
                    break;

                case 2:
                    // === REGISTRATION ===
                    System.out.println("\n=== User Registration ===");
                    System.out.print("Enter Username: ");
                    String un = sc.next();

                    System.out.print("Enter Password: ");
                    String pw = sc.next();

                    System.out.print("Enter Email: ");
                    String em = sc.next();

                    // Insert new account (ADD ACTION)
                    String sql = "INSERT INTO tbl_user (u_username, u_password, u_email) VALUES (?, ?, ?)";
                    int newUserId = db.addRecord(sql, un, pw, em);

                    if (newUserId != -1) {
                        System.out.println("🎉 Registration Successful! Your User ID is: " + newUserId);

                        // === Ask the user to login with the new account ===
                        System.out.println("\n=== Please Login to Continue ===");
                        System.out.print("Enter Username: ");
                        String regLoginUn = sc.next();

                        System.out.print("Enter Password: ");
                        String regLoginPw = sc.next();

                        boolean regLoggedIn = db.checkLogin(regLoginUn, regLoginPw);
                        if (regLoggedIn) {
                            System.out.println("✅ Login Successful! Welcome, " + regLoginUn);
                            // TODO: Call other features here after successful login
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
}
