package com.example.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserSeeder {

    // =========================================================
    // 1. SEED USER
    // =========================================================

    public void seedUser(
            String name,
            String loginPassword,
            String paymentPassword,
            String email,
            double initialBalance
    ) {

        try (Connection conn = DBUtil.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // -------------------------------------------------
                // FIND NEXT ID
                // -------------------------------------------------
int nextId = 1;

String idSql = """
        SELECT MAX(
            CAST(
                SUBSTRING(user_id, 8)
                AS UNSIGNED
            )
        )
        FROM users
        WHERE user_id REGEXP '^USER-ID[0-9]+$'
        """;

try (PreparedStatement ps =
             conn.prepareStatement(idSql);
     ResultSet rs = ps.executeQuery()) {

    if (rs.next()) {
        int maxId = rs.getInt(1);

        if (!rs.wasNull()) {
            nextId = maxId + 1;
        }
    }
}


                // -------------------------------------------------
                // IDs
                // -------------------------------------------------

                String userId = "USER-ID" + nextId;
                String accountId = "ACCOUNT-ID" + nextId;
                String username = "user" + nextId;

                // -------------------------------------------------
                // HASH PASSWORDS
                // -------------------------------------------------

                String loginHash =
                        PasswordUtil.hash(loginPassword);

                String paymentHash =
                        PasswordUtil.hash(paymentPassword);

                // -------------------------------------------------
                // INSERT USER
                // -------------------------------------------------

                String userSql = """
                        INSERT INTO users
                        (
                            user_id,
                            username,
                            password_hash,
                            payment_password_hash,
                            role,
                            status,
                            email,
                            name
                        )
                        VALUES
                        (?, ?, ?, ?, 'USER', 'ACTIVE', ?, ?)
                        """;

                try (PreparedStatement ps =
                             conn.prepareStatement(userSql)) {

                    ps.setString(1, userId);
                    ps.setString(2, username);
                    ps.setString(3, loginHash);
                    ps.setString(4, paymentHash);
                    ps.setString(5, email);
                    ps.setString(6, name);

                    ps.executeUpdate();
                }

                // -------------------------------------------------
                // INSERT ACCOUNT
                // -------------------------------------------------

                String accountSql = """
                        INSERT INTO accounts
                        (
                            account_id,
                            user_id,
                            username
                        )
                        VALUES (?, ?, ?)
                        """;

                try (PreparedStatement ps =
                             conn.prepareStatement(accountSql)) {

                    ps.setString(1, accountId);
                    ps.setString(2, userId);
                    ps.setString(3, username);

                    ps.executeUpdate();
                }

                // -------------------------------------------------
                // INSERT BALANCE
                // -------------------------------------------------

                String balanceSql = """
                        INSERT INTO balances
                        (
                            account_id,
                            balance,
                            version
                        )
                        VALUES (?, ?, 0)
                        """;

                try (PreparedStatement ps =
                             conn.prepareStatement(balanceSql)) {

                    ps.setString(1, accountId);
                    ps.setDouble(2, initialBalance);

                    ps.executeUpdate();
                }

                // -------------------------------------------------
                // COMMIT
                // -------------------------------------------------

                conn.commit();

                // -------------------------------------------------
                // OUTPUT
                // -------------------------------------------------

                System.out.println();
                System.out.println(
                        "===================================="
                );

                System.out.println(
                        "USER CREATED SUCCESSFULLY"
                );

                System.out.println(
                        "===================================="
                );

                System.out.println(
                        "Name       : " + name
                );

                System.out.println(
                        "Username   : " + username
                );

                System.out.println(
                        "User ID    : " + userId
                );

                System.out.println(
                        "Account ID : " + accountId
                );

                System.out.println(
                        "Email      : " + email
                );

                System.out.println(
                        "Balance    : " + initialBalance
                );

                System.out.println();

                System.out.println("Login Hash:");
                System.out.println(loginHash);

                System.out.println();

                System.out.println("Payment Hash:");
                System.out.println(paymentHash);

                System.out.println(
                        "===================================="
                );

            } catch (Exception e) {

                conn.rollback();
                throw e;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // 2. CHANGE LOGIN PASSWORD
    // =========================================================

    public void changeLoginPassword(
            String username,
            String newPassword
    ) {

        String newHash =
                PasswordUtil.hash(newPassword);

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE username = ?
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            if (rows == 0) {

                System.out.println(
                        "User not found: " + username
                );

            } else {

                System.out.println();
                System.out.println(
                        "Login password changed successfully."
                );

                System.out.println(
                        "Username: " + username
                );

                System.out.println();
                System.out.println("New Login Hash:");
                System.out.println(newHash);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // 3. CHANGE PAYMENT PASSWORD
    // =========================================================

    public void changePaymentPassword(
            String username,
            String newPassword
    ) {

        String newHash =
                PasswordUtil.hash(newPassword);

        String sql = """
                UPDATE users
                SET payment_password_hash = ?
                WHERE username = ?
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            if (rows == 0) {

                System.out.println(
                        "User not found: " + username
                );

            } else {

                System.out.println();
                System.out.println(
                        "Payment password changed successfully."
                );

                System.out.println(
                        "Username: " + username
                );

                System.out.println();
                System.out.println("New Payment Hash:");
                System.out.println(newHash);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // 4. CHANGE EMAIL
    // =========================================================

    public void changeEmail(
            String username,
            String newEmail
    ) {

        String sql = """
                UPDATE users
                SET email = ?
                WHERE username = ?
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, newEmail);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            if (rows == 0) {

                System.out.println(
                        "User not found: " + username
                );

            } else {

                System.out.println();
                System.out.println(
                        "Email changed successfully."
                );

                System.out.println(
                        "Username : " + username
                );

                System.out.println(
                        "New Email: " + newEmail
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // 5. LIST ALL USERS
    // =========================================================

    public void listUsers() {

        String sql = """
                SELECT
                    u.user_id,
                    u.username,
                    u.password_hash,
                    u.payment_password_hash,
                    u.role,
                    u.status,
                    u.email,
                    u.name,

                    a.account_id,
                    a.username AS account_username,

                    b.balance,
                    b.version

                FROM users u

                LEFT JOIN accounts a
                    ON u.user_id = a.user_id

                LEFT JOIN balances b
                    ON a.account_id = b.account_id

                ORDER BY
                    CAST(
                        SUBSTRING(u.user_id, 9)
                        AS UNSIGNED
                    )
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;

            System.out.println();
            System.out.println(
                    "============================================================"
            );

            System.out.println("ALL USERS");

            System.out.println(
                    "============================================================"
            );

            while (rs.next()) {

                count++;

                System.out.println();

                System.out.println(
                        "--------------------------------------------"
                );

                // USERS TABLE

                System.out.println("USERS TABLE");

                System.out.println(
                        "User ID       : "
                                + rs.getString("user_id")
                );

                System.out.println(
                        "Username      : "
                                + rs.getString("username")
                );

                System.out.println(
                        "Name          : "
                                + rs.getString("name")
                );

                System.out.println(
                        "Email         : "
                                + rs.getString("email")
                );

                System.out.println(
                        "Role          : "
                                + rs.getString("role")
                );

                System.out.println(
                        "Status        : "
                                + rs.getString("status")
                );

                System.out.println();

                System.out.println("Login Hash:");
                System.out.println(
                        rs.getString("password_hash")
                );

                System.out.println();

                System.out.println("Payment Hash:");
                System.out.println(
                        rs.getString(
                                "payment_password_hash"
                        )
                );

                // ACCOUNTS TABLE

                System.out.println();
                System.out.println("ACCOUNTS TABLE");

                System.out.println(
                        "Account ID    : "
                                + rs.getString("account_id")
                );

                System.out.println(
                        "Account User  : "
                                + rs.getString(
                                        "account_username"
                                )
                );

                // BALANCES TABLE

                System.out.println();
                System.out.println("BALANCES TABLE");

                System.out.println(
                        "Balance       : "
                                + rs.getDouble("balance")
                );

                System.out.println(
                        "Version       : "
                                + rs.getInt("version")
                );
            }

            System.out.println();

            System.out.println(
                    "--------------------------------------------"
            );

            System.out.println(
                    "Total Users: " + count
            );

            System.out.println(
                    "============================================================"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }



// =========================================================
// 6. FIND USER
// =========================================================

public void findUser(String searchValue) {

    String sql = """
            SELECT
                u.user_id,
                u.username,
                u.password_hash,
                u.payment_password_hash,
                u.role,
                u.status,
                u.email,
                u.name,

                a.account_id,
                a.username AS account_username,

                b.balance,
                b.version

            FROM users u

            LEFT JOIN accounts a
                ON u.user_id = a.user_id

            LEFT JOIN balances b
                ON a.account_id = b.account_id

            WHERE u.username = ?
               OR u.user_id = ?
               OR u.email = ?
            """;

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps =
                 conn.prepareStatement(sql)) {

        // Same value is checked against all 3 columns
        ps.setString(1, searchValue);
        ps.setString(2, searchValue);
        ps.setString(3, searchValue);

        try (ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {

                System.out.println();
                System.out.println(
                        "===================================="
                );

                System.out.println(
                        "USER NOT FOUND"
                );

                System.out.println(
                        "Search Value : " + searchValue
                );

                System.out.println(
                        "===================================="
                );

                return;
            }

            System.out.println();
            System.out.println(
                    "============================================"
            );

            System.out.println(
                    "USER FOUND"
            );

            System.out.println(
                    "============================================"
            );

            // USERS TABLE

            System.out.println();
            System.out.println("USERS TABLE");

            System.out.println(
                    "User ID       : "
                            + rs.getString("user_id")
            );

            System.out.println(
                    "Username      : "
                            + rs.getString("username")
            );

            System.out.println(
                    "Name          : "
                            + rs.getString("name")
            );

            System.out.println(
                    "Email         : "
                            + rs.getString("email")
            );

            System.out.println(
                    "Role          : "
                            + rs.getString("role")
            );

            System.out.println(
                    "Status        : "
                            + rs.getString("status")
            );

            System.out.println();

            System.out.println("Login Hash:");
            System.out.println(
                    rs.getString("password_hash")
            );

            System.out.println();

            System.out.println("Payment Hash:");
            System.out.println(
                    rs.getString("payment_password_hash")
            );

            // ACCOUNTS TABLE

            System.out.println();
            System.out.println("ACCOUNTS TABLE");

            System.out.println(
                    "Account ID    : "
                            + rs.getString("account_id")
            );

            System.out.println(
                    "Account User  : "
                            + rs.getString("account_username")
            );

            // BALANCES TABLE

            System.out.println();
            System.out.println("BALANCES TABLE");

            System.out.println();
            System.out.println("BALANCES TABLE");

            System.out.println(
                    "Balance       : "
                            + rs.getDouble("balance")
            );

            System.out.println(
                    "Version       : "
                            + rs.getInt("version")
            );

            System.out.println();
            System.out.println(
                    "============================================"
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}






    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        UserSeeder seeder = new UserSeeder();


        // =====================================================
        // CREATE USER
        // =====================================================

        seeder.seedUser(
                "Abhishek",
                "1234",
                "4321",
                "example@gmail.com",
                5000.0
        );


        // =====================================================
        // CHANGE LOGIN PASSWORD
        // =====================================================

        /*
        seeder.changeLoginPassword(
                "user1",
                "5678"
        );
        */


        // =====================================================
        // CHANGE PAYMENT PASSWORD
        // =====================================================

        /*
        seeder.changePaymentPassword(
                "user1",
                "8765"
        );
        */


        // =====================================================
        // CHANGE EMAIL
        // =====================================================

        /*
        seeder.changeEmail(
                "user1",
                "yourmail@gmail.com"
        );
        */


        // =====================================================
        // LIST ALL USERS
        // =====================================================


// =====================================================
// FIND USER
// =====================================================

// Search using username
seeder.findUser("USER-ID22");

// Search using user ID
// seeder.findUser("USER-ID1");

// Search using email
// seeder.findUser("example@gmail.com");




    }



}
