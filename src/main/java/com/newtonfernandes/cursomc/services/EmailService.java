package com.newtonfernandes.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.newtonfernandes.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
