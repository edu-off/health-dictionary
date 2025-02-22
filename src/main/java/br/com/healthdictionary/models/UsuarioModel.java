package br.com.healthdictionary.models;

import br.com.healthdictionary.enums.UsuarioRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USUARIO"));

        if (this.role == UsuarioRole.ADMIN)
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (this.role == UsuarioRole.MEDICO)
            return List.of(new SimpleGrantedAuthority("ROLE_MEDICO"));

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
