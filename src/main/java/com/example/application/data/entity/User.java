package com.example.application.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class User extends AbstractEntity {

	@Getter
	@Setter
	@Column(unique = true)
	private String username;

	@Getter
	@Setter
	@JsonIgnore
	private String hashedPassword;

	@Getter
	@Setter
	@Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	@ManyToMany(mappedBy = "users")
	private Set<Role> roles;
}
