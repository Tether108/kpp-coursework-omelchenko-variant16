package ua.nupp.defects.dao;

import ua.nupp.defects.model.Defect;
import ua.nupp.defects.model.DefectState;
import ua.nupp.defects.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefectDao {
    private static final String COLUMNS =
            "id, inspection_id, description, severity, area, safety_score, state, f1, f2, f3, risk_index";

    public List<Defect> findAll() throws SQLException {
        List<Defect> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM defect ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Defect findById(long id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM defect WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public long insert(Defect d) throws SQLException {
        String sql = "INSERT INTO defect (inspection_id, description, severity, area, safety_score, state, "
                + "f1, f2, f3, risk_index) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, d.getInspectionId());
            ps.setString(2, d.getDescription());
            ps.setInt(3, d.getSeverity());
            ps.setDouble(4, d.getArea());
            ps.setInt(5, d.getSafetyScore());
            ps.setString(6, d.getState().name());
            ps.setDouble(7, d.getF1());
            ps.setDouble(8, d.getF2());
            ps.setDouble(9, d.getF3());
            ps.setDouble(10, d.getRiskIndex());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    // стан тут не змінюється, для цього є updateState
    public boolean update(Defect d) throws SQLException {
        String sql = "UPDATE defect SET inspection_id = ?, description = ?, severity = ?, area = ?, safety_score = ?, "
                + "f1 = ?, f2 = ?, f3 = ?, risk_index = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, d.getInspectionId());
            ps.setString(2, d.getDescription());
            ps.setInt(3, d.getSeverity());
            ps.setDouble(4, d.getArea());
            ps.setInt(5, d.getSafetyScore());
            ps.setDouble(6, d.getF1());
            ps.setDouble(7, d.getF2());
            ps.setDouble(8, d.getF3());
            ps.setDouble(9, d.getRiskIndex());
            ps.setLong(10, d.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateState(long id, DefectState state) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE defect SET state = ? WHERE id = ?")) {
            ps.setString(1, state.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM defect WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Defect map(ResultSet rs) throws SQLException {
        return new Defect(
                rs.getLong("id"),
                rs.getLong("inspection_id"),
                rs.getString("description"),
                rs.getInt("severity"),
                rs.getDouble("area"),
                rs.getInt("safety_score"),
                DefectState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("risk_index"));
    }
}
