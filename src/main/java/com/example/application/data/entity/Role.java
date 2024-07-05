package com.example.application.data.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Role extends AbstractEntity {

	@Getter
	@Setter
	@NotEmpty
	private String name;

	@Getter
	@Setter
	@ManyToMany(mappedBy = "roles")
	private Set<User> users;
}
