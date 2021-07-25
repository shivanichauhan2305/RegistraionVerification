package com.registration.verification.email.otp.registrationverificationemailotp.entities;


import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "emailOrMobileno"))
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Size(min=2, message="Name should have atleast 2 characters")
    private String name;

    private String address;

    private String emailOrMobileno;

    private String password;
    
    private Boolean enabled = false;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
    		name="users_roles",
    		joinColumns = @JoinColumn(
    					name = "user_id", referencedColumnName = "id"),
    		inverseJoinColumns = @JoinColumn(
    			     	name = "role_id", referencedColumnName = "id")) 
    
    private Collection<Role> roles;
    
    //private Boolean locked = false;
  //  private Boolean enabled = false;
    
    public User() {

    }
    
	public User(String name, String address, String emailOrMobileno, String password, Collection<Role> roles) {
		super();
		this.name = name;
		this.address = address;
		this.emailOrMobileno = emailOrMobileno;
		this.password = password;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Collection<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmailOrMobileno() {
		return emailOrMobileno;
	}

	public void setEmailOrMobileno(String emailOrMobileno) {
		this.emailOrMobileno = emailOrMobileno;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
