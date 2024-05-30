package com.example.application.data.entity;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractEntity {

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
	@SequenceGenerator(name = "id_generator", initialValue = 1)
	private Long id;

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractEntity that)) return false;
		if (getId() == null) return super.equals(that);
		return getId().equals(that.getId());
	}
}
