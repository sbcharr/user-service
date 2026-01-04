package com.github.sbcharr.user_service.models;

public enum RoleName {
    ADMIN("ADMIN"),      // Full platform access
    MERCHANT("MERCHANT"), // Manage products/orders
    BUYER("BUYER"),      // Standard shopping
    GUEST("GUEST");      // Browse only (no auth)

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // For DB enum mapping convenience
    public static RoleName fromValue(String value) {
        for (RoleName role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
