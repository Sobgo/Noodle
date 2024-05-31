package com.example.application.data.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
public class PlatformUser extends AbstractEntity {

	@Getter
	@Setter
	private String nick;

	@ManyToMany
	private List<Role> roles;
}
