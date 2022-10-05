package rikkei.academy.service.role;

import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.RoleName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleServiceIMPL implements IRoleService{
    private Connection connection = ConnectMySQL.getConnection();
    private final String FIND_BY_NAME = "SELECT * FROM roles WHERE name=?;";
    private final String FIND_BY_ID = "SELECT  * FROM roles WHERE id=?;";
    @Override
    public Role findByName(RoleName name) {
        Role role = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME);
            preparedStatement.setString(1, String.valueOf(name));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
               int id= resultSet.getInt("id");
                role = new Role(id,name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return role;
    }

    @Override
    public Role findById(int id) {
        Role role = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                RoleName name = RoleName.valueOf(resultSet.getString("name"));
                role = new Role(id,name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return role;
    }

}
