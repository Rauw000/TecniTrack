package com.tecnitrack.domain;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UsuarioPrincipal extends User {

    private final Long versionSesion;

    public UsuarioPrincipal(
            String correo,
            String contrasenaHash,
            boolean activo,
            Collection<? extends GrantedAuthority> authorities,
            Long versionSesion) {

        super(correo, contrasenaHash, activo,
                true, true, true, authorities);

        this.versionSesion = versionSesion;
    }

    public Long getVersionSesion() {
        return versionSesion;
    }
}
