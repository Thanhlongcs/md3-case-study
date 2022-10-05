package rikkei.academy.controller;

import rikkei.academy.model.Role;
import rikkei.academy.model.RoleName;
import rikkei.academy.model.User;
import rikkei.academy.service.role.IRoleService;
import rikkei.academy.service.role.RoleServiceIMPL;
import rikkei.academy.service.user.IUserService;
import rikkei.academy.service.user.UserServiceIMPL;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(value = "/users")
public class UserController extends HttpServlet {

    private IRoleService roleService = new RoleServiceIMPL();
    private IUserService userService = new UserServiceIMPL();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "register":
                showFormRegister(request, response);
                break;
            case "login":
                showFormLogin(request, response);
                break;
            case "logout":
                logOut(request, response);
                break;
            case "change_avatar":
                showUploadAvatar(request, response);
                break;
            case "change_password":
                showChangePassword(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "register":
                actionRegister(request, response);
                break;
            case "login":
                actionLogin(request, response);
                break;
            case "change_avatar":
                actionUploadAvatar(request, response);
                break;
            case "change_password":
                actionChangePassword(request, response);
                break;
        }
    }

    public void destroy() {
    }

    public void showFormRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/register.jsp");
        dispatcher.forward(request, response);
    }

    public void actionRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = "admin";
        Set<String> strRole = new HashSet<>();
        Set<Role> roles = new HashSet<>();
        strRole.add(role);
        strRole.forEach(role1 -> {
            switch (role1) {
                case "admin":
                    Role adminRole = roleService.findByName(RoleName.ADMIN);
                    roles.add(adminRole);
                    break;
                case "pm":
                    Role pmRole = roleService.findByName(RoleName.PM);
                    roles.add(pmRole);
                    break;
                default:
                    Role userRole = roleService.findByName(RoleName.USER);
                    roles.add(userRole);
            }
        });
        System.out.println("roles set ---> " + roles);
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        if (userService.existedByUsername(username)) {
            request.setAttribute("message", "The username existed! Please try again!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/register.jsp");
            dispatcher.forward(request, response);
//            return;
        }
        String email = request.getParameter("email");
        if (userService.existedByEmail(email)) {
            request.setAttribute("message", "The email existed! Please try again!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/register.jsp");
            dispatcher.forward(request, response);
//            return;
        }
        String password = request.getParameter("password");
        String confirm_pass = request.getParameter("repeat_pass");
        if (!password.equals(confirm_pass)) {
            request.setAttribute("message", "The password do not match!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/register.jsp");
            dispatcher.forward(request, response);
            return;
        }
        User user = new User(name, username, email, password, roles);
        userService.save(user);
        request.setAttribute("success", "Create user success!!");
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/register.jsp");
        dispatcher.forward(request, response);
    }

    //LOGIN
    public void showFormLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/form-login/login.jsp");
        dispatcher.forward(request, response);
    }

    public void actionLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userService.findByUsernameAndPassword(username, password);
        String pageJSP = "";
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            System.out.println("get userlogin ---> " + session.getAttribute("user"));
            pageJSP = "WEB-INF/profile/profile.jsp";
        } else {
            pageJSP = "WEB-INF/form-login/login.jsp";
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(pageJSP);
        dispatcher.forward(request, response);
    }

    //LOG OUT
    public void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
            response.sendRedirect("index.jsp");
        }
    }

    public void showUploadAvatar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/upload/upload_avatar.jsp");
        dispatcher.forward(request, response);
    }

    public void actionUploadAvatar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String avatar = request.getParameter("avatar");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        int id = user.getId();
        userService.changeAvatar(avatar, id);
        request.setAttribute("avatar", avatar);
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/profile/profile.jsp");
        dispatcher.forward(request, response);
    }

    public void showChangePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/profile/change-password.jsp");
        dispatcher.forward(request, response);
    }

    private void actionChangePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User userLogin = (User) request.getSession().getAttribute("user");
        String oldPass = request.getParameter("current_password");
        String newPass = request.getParameter("new_password");
        String confirmPass = request.getParameter("confirm_password");
        if (!newPass.equals(confirmPass)) {
            request.setAttribute("message", "repeat password do not match");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/profile/change-password.jsp");
            dispatcher.forward(request, response);
            return;
        }
        if (!userLogin.getPassword().equals(oldPass)) {
            String message = "old password not match";
            request.setAttribute("message", message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/profile/change-password.jsp");
            dispatcher.forward(request, response);
            return;

        }
        userLogin.setPassword(newPass);
        System.out.println(userLogin);
        userService.editPassword(userLogin);
        request.getSession().setAttribute("userLogin", userLogin);
        request.setAttribute("success", "Change password success");
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/profile/profile.jsp");
        dispatcher.forward(request, response);
    }
}