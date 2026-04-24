package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BrevoEmailService extends BaseEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email:no-reply@maqc.ca}")
    private String senderEmail;

    @Value("${brevo.sender.name:MAQC}")
    private String senderName;

    private TransactionalEmailsApi apiInstance;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing BrevoEmailService with API Key: {}...",
                    apiKey != null && apiKey.length() > 5 ? apiKey.substring(0, 5) + "***" : "null");

            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");

            if (apiKeyAuth == null) {
                log.error("Could not find 'api-key' authentication in Brevo ApiClient configuration");
                return;
            }

            apiKeyAuth.setApiKey(apiKey);
            apiInstance = new TransactionalEmailsApi(defaultClient);
            log.info("BrevoEmailService initialized successfully");
        } catch (Exception e) {
            log.error("Error during BrevoEmailService initialization: {}", e.getMessage(), e);
        }
    }

    public void sendContactEmail(ContactFormData form, Property property, User owner) throws Exception {
        System.out.println("Brevo: Contact email sent to " + owner.getEmail() + " for property " + property.getId());
        String toEmail = owner.getEmail();
        String subject = "MAQC - Property Inquiry #" + property.getId() + " - " + form.getSubject();

        // Build variables for template
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
        sendBrevoEmail(toEmail, subject, htmlContent);
        log.info("Brevo: Contact email sent to {} for property {}", toEmail, property.getId());
    }

    public void sendReceiptEmail(User user, String planType) throws Exception {
        String toEmail = user.getEmail();
        String subject = "MAQC - Receipt for Your " + planType + " Membership Purchase";

        String htmlContent = buildReceiptEmailTemplate(user, planType);
        sendBrevoEmail(toEmail, subject, htmlContent);
        log.info("Brevo: Receipt email sent to {} for plan {}", toEmail, planType);
    }

    public void sendPasswordResetEmail(User user) throws Exception {
        String toEmail = user.getEmail();
        String subject = "MAQC - Password Reset Request";

        String resetUrl = frontendUrl + "/reset-password?token=" + user.getResetToken();
        String htmlContent = buildPasswordResetEmailTemplate(user, resetUrl);
        sendBrevoEmail(toEmail, subject, htmlContent);
        log.info("Brevo: Password reset email sent to {}", toEmail);
    }

    private void sendBrevoEmail(String toEmail, String subject, String htmlContent) throws Exception {
        if (apiInstance == null) {
            log.error("Brevo apiInstance is null. Initialization might have failed.");
            throw new Exception("Brevo Email Service is not properly initialized");
        }

        try {
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(senderEmail);
            sender.setName(senderName);
            sendSmtpEmail.setSender(sender);

            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(toEmail);
            sendSmtpEmail.setTo(Collections.singletonList(to));

            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setHtmlContent(htmlContent);

            log.debug("Sending Brevo email to {}", toEmail);
            apiInstance.sendTransacEmail(sendSmtpEmail);
            log.info("Brevo email sent successfully to {}", toEmail);
        } catch (ApiException e) {
            String errorBody = e.getResponseBody();
            log.error("Brevo API Error ({}): {}", e.getCode(), errorBody);
            throw new Exception("Brevo API error (" + e.getCode() + "): " +
                    (errorBody != null ? errorBody : e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error sending Brevo email: {}", e.getMessage(), e);
            throw new Exception(
                    "Unexpected email error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }
}
