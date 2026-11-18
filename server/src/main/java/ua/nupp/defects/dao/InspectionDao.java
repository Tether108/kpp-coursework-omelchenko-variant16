package ua.nupp.defects.dao;

import ua.nupp.defects.model.Inspection;
import ua.nupp.defects.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InspectionDao {
    private static final String COLUMNS = "id, object_id, inspection_date, inspector, condition_category, notes";

    public List<Inspection> findAll() throws SQLException {
        List<Inspection> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM inspection ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Inspection findById(long id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM inspection WHERE id = ?";
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

    public long insert(Inspection i) throws SQLException {
        String sql = "INSERT INTO inspection (object_id, inspection_date, inspector, condition_category, notes) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, i);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public boolean update(Inspection i) throws SQLException {
        String sql = "UPDATE inspection SET object_id = ?, inspection_date = ?, inspector = ?, "
                + "condition_category = ?, notes = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fill(ps, i);
            ps.setLong(6, i.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM inspection WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void fill(PreparedStatement ps, Inspection i) throws SQLException {
        ps.setLong(1, i.getObjectId());
        ps.setDate(2, Date.valueOf(i.getInspectionDate()));
        ps.setString(3, i.getInspector());
        ps.setInt(4, i.getConditionCategory());
        ps.setString(5, i.getNotes());
    }

    private Inspection map(ResultSet rs) throws SQLException {
        return new Inspection(
                rs.getLong("id"),
                rs.getLong("object_id"),
                rs.getDate("inspection_date").toString(),
                rs.getString("inspector"),
                rs.getInt("condition_category"),
                rs.getString("notes"));
    }
}
