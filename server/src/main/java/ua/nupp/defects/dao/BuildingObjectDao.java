package ua.nupp.defects.dao;

import ua.nupp.defects.model.BuildingObject;
import ua.nupp.defects.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingObjectDao {

    public List<BuildingObject> findAll() throws SQLException {
        List<BuildingObject> list = new ArrayList<>();
        String sql = "SELECT id, name, address, object_type, build_year FROM building_object ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public BuildingObject findById(long id) throws SQLException {
        String sql = "SELECT id, name, address, object_type, build_year FROM building_object WHERE id = ?";
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

    public long insert(BuildingObject o) throws SQLException {
        String sql = "INSERT INTO building_object (name, address, object_type, build_year) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getName());
            ps.setString(2, o.getAddress());
            ps.setString(3, o.getObjectType());
            ps.setInt(4, o.getBuildYear());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public boolean update(BuildingObject o) throws SQLException {
        String sql = "UPDATE building_object SET name = ?, address = ?, object_type = ?, build_year = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getName());
            ps.setString(2, o.getAddress());
            ps.setString(3, o.getObjectType());
            ps.setInt(4, o.getBuildYear());
            ps.setLong(5, o.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM building_object WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private BuildingObject map(ResultSet rs) throws SQLException {
        return new BuildingObject(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("object_type"),
                rs.getInt("build_year"));
    }
}
