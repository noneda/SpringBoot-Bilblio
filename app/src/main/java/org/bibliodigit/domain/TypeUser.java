package org.bibliodigit.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "type_users")
public class TypeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String type;  

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "typeUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> users = new ArrayList;
    
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }
}
