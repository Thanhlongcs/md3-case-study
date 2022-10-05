package rikkei.academy.service.user;

import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.User;
import rikkei.academy.service.role.IRoleService;
import rikkei.academy.service.role.RoleServiceIMPL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceIMPL implements IUserService{
    private IRoleService roleService = new RoleServiceIMPL();
    private Connection connection = ConnectMySQL.getConnection();
    private final String CREATE_USER = "INSERT INTO users(name,username,email,password) values (?,?,?,?);";
    private final String INSERT_ROLE_ID_USER_ID = "INSERT INTO user_role(user_id,role_id) values (?,?);";
    private final String FIND_ALL_USERNAME = "SELECT username FROM users";
    private final String FIND_ALL_EMAIL = "SELECT email FROM users";
    private final String FIND_BY_ID_USER = "SELECT name,password FROM users WHERE id=?;";
    private final String FIND_ROLE_BY_USER = "SELECT role_id FROM user_role WHERE user_id=?;";
    private final String FIND_BY_USERNAME_PASSWORD = "SELECT * FROM users WHERE username=?AND password=?";
    private final String CHANGE_AVATAR ="UPDATE users SET avatar=? where id=?";
    private final String CHANGE_PASSWORD = "update users set password=? where id=?";

    String test = "update users set password = 11111 where id = 4";
    @Override
    public void save(User user) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,user.getPassword());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int user_id = 0; //LAY RA ID CUAR USER DE TAO VAO BANG CHUNG GIAN
            while (resultSet.next()){
                user_id = resultSet.getInt(1);
            }
            //TAO DU LIEU CHO BANG TRUNG GIAN
            PreparedStatement preparedStatement1 = connection.prepareStatement(INSERT_ROLE_ID_USER_ID);
            Set<Role> roles = user.getRoles();
            List<Role> roleList = new ArrayList<>(roles); // Convert tu Set -> List
            List<Integer> listRoleId = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                listRoleId.add(roleList.get(i).getId());
            }
            for (int i = 0; i < listRoleId.size(); i++) {
                preparedStatement1.setInt(1,user_id);
                preparedStatement1.setInt(2,listRoleId.get(i));
                preparedStatement1.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existedByUsername(String username) {
        List<String> listUsername = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERNAME);
           ResultSet resultSet =  preparedStatement.executeQuery();
           while (resultSet.next()){
               listUsername.add(resultSet.getString("username"));
           }
            for (int i = 0; i < listUsername.size(); i++) {
                if(username.equals(listUsername.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean existedByEmail(String email) {
        List<String> listEmail = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_EMAIL);
            ResultSet resultSet =  preparedStatement.executeQuery();
            while (resultSet.next()){
                listEmail.add(resultSet.getString("email"));
            }
            for (int i = 0; i < listEmail.size(); i++) {
                if(email.equals(listEmail.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public User findById(int id) {
        User user = null;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_USER);
            preparedStatement.setInt(1,id);
            PreparedStatement preparedStatement1 = connection.prepareStatement(FIND_ROLE_BY_USER);
            preparedStatement1.setInt(1,id);
            ResultSet resultSet = preparedStatement1.executeQuery();
            int id_role = 0;
            Set<Role> roles = new HashSet<>();
            while (resultSet.next()){
                id_role = resultSet.getInt("role_id");
                Role role = roleService.findById(id_role);
                roles.add(role);
            }
            ResultSet resultSet1 = preparedStatement.executeQuery();
            while (resultSet1.next()){
                String name = resultSet1.getString("name");
                String password = resultSet1.getString("password");
                user = new User(id,name,roles);
                user.setPassword(password);
            }
            connection.commit();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        User user = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USERNAME_PASSWORD);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                user = findById(id);
               return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void changeAvatar(String avatar, int id) {
        try{
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(CHANGE_AVATAR);
            preparedStatement.setString(1,avatar);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void editPassword(User user) {
        try{
            PreparedStatement ps = connection.prepareStatement(CHANGE_PASSWORD);
            ps.setString(1,user.getPassword());
            ps.setInt(2,user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
