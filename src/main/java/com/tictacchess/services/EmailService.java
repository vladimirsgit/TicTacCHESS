package com.tictacchess.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
   @Autowired
    private JavaMailSender javaMailSender;

   public void sendEmail(String to, String subject, String htmlContent) throws MessagingException{
       MimeMessage mimeMessage =javaMailSender.createMimeMessage();
       MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

       mimeMessageHelper.setFrom("tictacchessspring@gmail.com");
       mimeMessageHelper.setTo(to);
       mimeMessageHelper.setSubject(subject);
       mimeMessageHelper.setText(htmlContent, true);

       javaMailSender.send(mimeMessage);

   }
}
