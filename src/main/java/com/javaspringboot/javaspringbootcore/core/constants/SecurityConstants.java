package com.javaspringboot.javaspringbootcore.core.constants;

import java.util.Set;

public class SecurityConstants {

    public static final Set<String> PERMITTED_PATHS = Set.of(
            "/users/createUser",
            "/users/login",
            "/users/verifySingUp",
            "/users/verifySingIn"
    );

}