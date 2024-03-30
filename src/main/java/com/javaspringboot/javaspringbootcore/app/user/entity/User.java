package com.javaspringboot.javaspringbootcore.app.user.entity;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor()
@AllArgsConstructor()
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String surname;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state = UserState.NOT_VERIFIED;

    @Column(nullable = false)
    private String password;

    private String salt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;
}