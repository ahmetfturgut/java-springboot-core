package com.javaspringboot.javaspringbootcore.app.user.entity;

import com.javaspringboot.javaspringbootcore.app.user.enums.UserRole;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Builder
@Data
@NoArgsConstructor()
@AllArgsConstructor()
@Accessors(chain = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;

    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state = UserState.NOT_VERIFIED;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.state.equals(UserState.EXPIRED_PASSWORD);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.state.equals(UserState.PASSIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.state.equals(UserState.EXPIRED_PASSWORD);
    }

    @Override
    public boolean isEnabled() {
        return this.state.equals(UserState.ACTIVE) || this.state.equals(UserState.NOT_VERIFIED);
    }
}