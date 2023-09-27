package com.mywebapp.controller;

import com.mywebapp.model.Users;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestConnectionJPA {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistence");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            List<Users> users = em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
            em.getTransaction().commit();
            System.out.println("Conexión a la base de datos exitosa.");
            System.out.println(users);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}