package br.com.healthdictionary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UsuarioRole {

    ADMIN("ADMIN"),
    USUARIO("USUARIO"),
    MEDICO("MEDICO");

    private final String role;

    public static UsuarioRole get(String role) {
        for (UsuarioRole roleEnum : UsuarioRole.values()) {
            if (roleEnum.getRole().equals(role)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("role inválido");
    }

}
