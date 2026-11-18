package ua.nupp.defects.dao;

import ua.nupp.defects.model.RepairTask;
import ua.nupp.defects.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairTaskDao {

    public List<RepairTask> findAll() throws SQLException {
        List<RepairTask> list = new ArrayList<>();
        String sql = "SELECT id, defect_id, description, executor, planned_date FROM repair_task ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public RepairTask findById(long id) throws SQLException {
        String sql = "SELECT id, defect_id, description, executor, planned_date FROM repair_task WHERE id = ?";
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

    public long insert(RepairTask t) throws SQLException {
        String sql = "INSERT INTO repair_task (defect_id, description, executor, planned_date) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, t.getDefectId());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getExecutor());
            ps.setDate(4, Date.valueOf(t.getPlannedDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public boolean update(RepairTask t) throws SQLException {
        String sql = "UPDATE repair_task SET defect_id = ?, description = ?, executor = ?, planned_date = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, t.getDefectId());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getExecutor());
            ps.setDate(4, Date.valueOf(t.getPlannedDate()));
            ps.setLong(5, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM repair_task WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private RepairTask map(ResultSet rs) throws SQLException {
        return new RepairTask(
                rs.getLong("id"),
                rs.getLong("defect_id"),
                rs.getString("description"),
                rs.getString("executor"),
                rs.getDate("planned_date").toString());
    }
}
