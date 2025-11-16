package org.bibliodigit;

import org.hibernate.Session;

public class App {
    public static void main(String[] args) {
        DriverHibernate driver = new DriverHibernate();
        Session session = driver.getSession();

        session.beginTransaction();

        // Solo ejemplo: un print
        System.out.println("Conectado a la BD desde Docker correctamente");

        session.getTransaction().commit();
        session.close();
    }
}
 