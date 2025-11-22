package org.bibliodigit.domain;

public enum LoanStatus {
    AVAILABLE,   // Libro disponible en inventario
    ACTIVE,      // Préstamo activo
    RETURNED,    // Devuelto a tiempo
    OVERDUE      // Devuelto con retraso (o aún activo con retraso)
}
