package com.example.application.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
    @Column(length = 10 * 1000 * 1000)
    private byte[] profilePicture;

	@Getter
	@Setter
	@ManyToMany
	private Set<Role> roles;

	@Getter
	@Setter
	@OneToMany(mappedBy = "owner")
	private List<Course> courses;
}
