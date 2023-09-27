package com.mywebapp.controller;

import com.mywebapp.model.Users;
import com.mywebapp.persistence.UserJpaController;
import com.mywebapp.persistence.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.registry.infomodel.User;

@WebServlet("/UserServlet")
public class UsersController extends HttpServlet {

    //Two methods to Post and Get data depending the action HTTP
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "list":
                listUsers(request, response);
                break;
            default:
                listUsers(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addUser(request, response);
                break;
            case "edit":
                editUser(request, response);
                break;
            case "delete":
                deleteUser(request, response);
                break;
            default:
                listUsers(request, response);
        }
    }

    //METHODS
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Declaring variables in an instance
        UserJpaController userJPA = new UserJpaController();

        try {
            //Now we make the list by getting the method findUserEntities inside userJpaController
            List<Users> userList = userJPA.findUserEntities();

            //Add attributtes 
            request.setAttribute("userList", userList);

            //If everything is correct, the page will be redirected to userList.jsp.
            request.getRequestDispatcher("/userList.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            //If something is bad, the page will be redirected to error.jsp.
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }

    }

    private void addUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Variables with paramether to obtain
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String email = request.getParameter("email");
        int age = Integer.parseInt(request.getParameter("age"));

        //Declaring variables in an instance
        UserJpaController userJPA = new UserJpaController();

        try {
            //Declaring variables in an instance to create a new user in our model of getters and setters
            Users newUser = new Users();

            //Data obtained in the forms for a new user
            newUser.setFirst_name(first_name);
            newUser.setLast_name(last_name);
            newUser.setEmail(email);
            newUser.setAge(age);

            //add data in the method create in userJpaController
            userJPA.create(newUser);

            //If everything is correct, the page will be redirected to list but as new path that will send a message to the alert.
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&successMessage=User+added+successfully");
        } catch (Exception e) {
            e.printStackTrace();
            //If something is bad, the page will be redirected to list but as new path that will send a message to the alert.
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=Error+adding+user");
        }
    }

    private void editUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Variables with paramether to obtain
        int userId = Integer.parseInt(request.getParameter("userId"));
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String email = request.getParameter("email");
        int age = Integer.parseInt(request.getParameter("age"));

        //Declaring variables in an instance
        UserJpaController userJPA = new UserJpaController();
        try {
            //Find user by method in userJpaController
            Users existingUser = userJPA.findUser(userId);

            //Validation if the user already exists
            if (existingUser != null) {

                //Data obtained in the forms to edit
                existingUser.setFirst_name(firstName);
                existingUser.setLast_name(lastName);
                existingUser.setEmail(email);
                existingUser.setAge(age);

                //add data in the method edit in userJpaController
                userJPA.edit(existingUser);

                //If something is bad or good, the page will be redirected to the list but as a new path depending on the Exception that will send a message to the alert.
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&successMessage=User+edited+successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=User+not+found");
            }
        } catch (NonexistentEntityException nee) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=User+not+found");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=Error+updating+user");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Required id to delete user
        String userIdObtained = request.getParameter("userId");

        //Validation if the id has been obtained
        if (userIdObtained != null) {
            try {
                //Change the id to integer cause the id is obtain in string
                int userId = Integer.parseInt(userIdObtained);

                //Declaring variables in an instance
                UserJpaController userJPA = new UserJpaController();

                try {
                    //User delete by ID
                    userJPA.destroy(userId);

                    //If something is bad or good, the page will be redirected to the list but as a new path depending on the Exception that will send a message to the alert.
                    response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&successMessage=User+deleted+successfully");
                } catch (NonexistentEntityException nee) {
                    response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=User+not+found+or+already+deleted");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=Invalid+ID");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=list&errorMessage=Invalid+ID");
        }
    }
}
