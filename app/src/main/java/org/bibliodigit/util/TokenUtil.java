package org.bibliodigit.util;

import java.util.UUID;

public class TokenUtil {

    public static String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }
}
