package com.example.application.services;

import org.springframework.stereotype.Service;

import com.example.application.views.MainLayout;

import lombok.Getter;
import lombok.Setter;

@Service
public class GlobalAccessService {
	@Getter
	@Setter
	private MainLayout mainLayout;

	public GlobalAccessService() {}
}
