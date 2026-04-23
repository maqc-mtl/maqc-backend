package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService extends BaseEmailService {

    private final JavaMailSender mailSender;

    public void sendContactEmail(ContactFormData form, Property property, User owner) throws MessagingException {
        String toEmail = owner.getEmail();
        String subject = "MAQC - Property Inquiry #" + property.getId() + " - " + form.getSubject();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom("infomaqc@gmail.com");

        // Build the email content
        Map<String, Object> variables = new HashMap<>();
        variables.put("ownerName", owner.getFirstName() + " " + owner.getLastName());
        variables.put("buyerName", form.getFirstName() + " " + form.getLastName());
        variables.put("buyerEmail", form.getEmail());
        variables.put("buyerPhone", form.getPhone() != null ? form.getPhone() : "Not provided");
        variables.put("propertyTitle", property.getTitle());
        variables.put("propertyAddress", property.getAddress());
        variables.put("propertyId", property.getId());
        variables.put("message", form.getMessage());
        variables.put("propertyUrl", frontendUrl + "/properties/" + property.getId());

        String htmlContent = buildEmailTemplate(variables);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Contact email sent to {} for property {}", toEmail, property.getId());
    }

    public void sendReceiptEmail(User user, String planType) throws MessagingException {
        String toEmail = user.getEmail();
        String subject = "MAQC - Receipt for Your " + planType + " Membership Purchase";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom("infomaqc@gmail.com");

        String htmlContent = buildReceiptEmailTemplate(user, planType);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Receipt email sent to {} for plan {}", toEmail, planType);
    }

    public void sendPasswordResetEmail(User user) throws MessagingException {
        String toEmail = user.getEmail();
        String subject = "MAQC - Password Reset Request";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom("infomaqc@gmail.com");

        String resetUrl = frontendUrl + "/reset-password?token=" + user.getResetToken();

        String htmlContent = buildPasswordResetEmailTemplate(user, resetUrl);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Password reset email sent to {}", toEmail);
    }
}