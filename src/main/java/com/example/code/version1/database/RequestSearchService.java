package com.example.code.version1.finalmodule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.code.version1.dto.RequestStatusResponse;

public class RequestSearchService {

    public static RequestStatusResponse searchRequest(
            Connection conn,
            String requestId) {

        RequestStatusResponse dto = new RequestStatusResponse();

        try {

            // ===========================================
            // Search Successful Transactions
            // ===========================================

            String sql = "SELECT * FROM transactions WHERE request_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, requestId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                dto.setFound(true);

                dto.setStatus(rs.getString("status"));

                dto.setRequestId(rs.getString("request_id"));

                dto.setTransactionId(rs.getString("txn_id"));

                dto.setFromUsername(rs.getString("from_username"));

                dto.setToUsername(rs.getString("to_username"));

                dto.setFromAccount(rs.getString("from_account"));

                dto.setToAccount(rs.getString("to_account"));

                dto.setAmount(rs.getDouble("amount"));

                rs.close();
                ps.close();

                return dto;
            }

            rs.close();
            ps.close();

            // ===========================================
            // Search Failed Requests
            // ===========================================

            sql = "SELECT * FROM failed_requests WHERE request_id = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, requestId);

            rs = ps.executeQuery();

            if (rs.next()) {

                dto.setFound(true);

                dto.setStatus("FAILED");

                dto.setRequestId(rs.getString("request_id"));

                dto.setFromUsername(rs.getString("username"));

                dto.setReason(rs.getString("reason"));

                rs.close();
                ps.close();

                return dto;
            }

            rs.close();
            ps.close();

            // ===========================================
            // Not Found
            // ===========================================

            dto.setFound(false);
            dto.setStatus("NOT_FOUND");

            return dto;

        }
        catch (SQLException e) {

            e.printStackTrace();

            dto.setFound(false);
            dto.setStatus("DATABASE_ERROR");
            dto.setReason(e.getMessage());

            return dto;
        }

    }

}