package org.bibliodigit;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void testHibernateConnection() {
        DriverHibernate driver = new DriverHibernate();
        Session session = driver.getSession();

        assertNotNull(session, "La sesión no debe ser null");
        assertTrue(session.isConnected(), "La sesión debe estar conectada");

        session.close();
    }
}
