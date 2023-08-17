package xyz.krsh.insecuresite.rest.entities;

import javax.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String role;

    public User() {
    }

    public String getId() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleAdmin() {
        this.role = "admin";
    }

}
