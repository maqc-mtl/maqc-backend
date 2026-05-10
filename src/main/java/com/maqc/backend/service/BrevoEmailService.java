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

    public void sendReservationEmail(String toEmail, String proName, String proType,
            String clientName, String clientEmail, String clientPhone,
            String date, String time, String notes) throws Exception {
        String subject = "MAQC - New Reservation Request from " + clientName;
        System.out.println("sendReservationEmail  start");
        String htmlContent = buildReservationEmailTemplate(proName, proType, clientName,
                clientEmail, clientPhone, date, time, notes);
        System.out.println("sendReservationEmail");
        sendBrevoEmail(toEmail, subject, htmlContent);
        log.info("Brevo: Reservation email sent to {} for {}", toEmail, proType);
    }

    private String buildReservationEmailTemplate(String proName, String proType,
            String clientName, String clientEmail,
            String clientPhone, String date, String time, String notes) {
        String proTypeDisplay = proType.equals("notary") ? "Notary"
                : proType.equals("inspector") ? "Home Inspector" : "Real Estate Agent";

        String notesSection = "";
        if (notes != null && !notes.isEmpty()) {
            notesSection = """
                    <div class="notes-box">
                        <div class="info-label" style="margin-bottom: 8px;">Notes</div>
                        <div class="info-value" style="color: #475569;">[[notes]]</div>
                    </div>
                    """;
        } else {
            notesSection = """
                    <div class="notes-box">
                        <div class="info-label" style="margin-bottom: 8px;">Notes</div>
                        <div class="info-value" style="color: #475569;">No additional notes provided.</div>
                    </div>
                    """;
        }

        String template = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            line-height: 1.5;
                            color: #1e293b;
                            background-color: #f8fafc;
                            margin: 0;
                            padding: 0;
                            -webkit-font-smoothing: antialiased;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 20px;
                            overflow: hidden;
                            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background-color: #1a1a6d;
                            background: linear-gradient(135deg, #1a1a6d 0%, #2563eb 100%);
                            padding: 40px 40px;
                            text-align: center;
                            color: #ffffff;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 800;
                            letter-spacing: -0.025em;
                        }
                        .content {
                            padding: 40px;
                        }
                        .greeting {
                            font-size: 18px;
                            font-weight: 700;
                            margin-bottom: 24px;
                            color: #0f172a;
                        }
                        .section-title {
                            font-size: 11px;
                            font-weight: 800;
                            color: #94a3b8;
                            text-transform: uppercase;
                            letter-spacing: 0.1em;
                            margin-bottom: 16px;
                            border-bottom: 1px solid #e2e8f0;
                            padding-bottom: 8px;
                        }
                        .info-grid {
                            display: table;
                            width: 100%;
                            table-layout: fixed;
                            margin-bottom: 24px;
                        }
                        .info-item {
                            display: table-cell;
                            padding-bottom: 20px;
                        }
                        .info-label {
                            font-size: 11px;
                            font-weight: 700;
                            color: #64748b;
                            text-transform: uppercase;
                            margin-bottom: 4px;
                        }
                        .info-value {
                            font-size: 14px;
                            font-weight: 600;
                            color: #1e293b;
                        }
                        .notes-box {
                            background-color: #f8fafc;
                            border: 1px solid #e2e8f0;
                            border-radius: 12px;
                            padding: 16px;
                            margin-top: 16px;
                        }
                        .footer {
                            padding: 30px 40px;
                            text-align: center;
                            background-color: #f8fafc;
                            border-top: 1px solid #e2e8f0;
                        }
                        .footer p {
                            font-size: 12px;
                            color: #94a3b8;
                            margin: 4px 0;
                        }
                        @media only screen and (max-width: 480px) {
                            .content { padding: 24px; }
                            .info-item { display: block; width: 100%; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>New Reservation Request</h1>
                            <p>MAQC - Quebec's Real Estate Platform</p>
                        </div>
                        <div class="content">
                            <div class="greeting">Hello [[proName]],</div>

                            <p style="margin-bottom: 24px;">You have received a new reservation request from a client. Please find the details below:</p>

                            <div class="section-title">Professional Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Name</div>
                                    <div class="info-value">[[proName]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Type</div>
                                    <div class="info-value">[[proTypeDisplay]]</div>
                                </div>
                            </div>

                            <div class="section-title">Reservation Details</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Date</div>
                                    <div class="info-value">[[date]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Time</div>
                                    <div class="info-value">[[time]]</div>
                                </div>
                            </div>

                            <div class="section-title">Client Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Name</div>
                                    <div class="info-value">[[clientName]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Email</div>
                                    <div class="info-value">[[clientEmail]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Phone</div>
                                    <div class="info-value">[[clientPhone]]</div>
                                </div>
                            </div>

                            [[notesSection]]
                        </div>
                        <div class="footer">
                            <p>This is an automated message from MAQC platform.</p>
                            <p>Please contact the client directly to confirm the appointment.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        return template.replace("[[proName]]", proName)
                .replace("[[proTypeDisplay]]", proTypeDisplay)
                .replace("[[date]]", date)
                .replace("[[time]]", time)
                .replace("[[clientName]]", clientName)
                .replace("[[clientEmail]]", clientEmail)
                .replace("[[clientPhone]]", clientPhone != null ? clientPhone : "Not provided")
                .replace("[[notesSection]]", notesSection)
                .replace("[[notes]]", notes);
    }

    private void sendBrevoEmail(String toEmail, String subject, String htmlContent) throws Exception {
        if (apiInstance == null) {
            log.error("Brevo apiInstance is null. Initialization might have failed.");
            throw new Exception("Brevo Email Service is not properly initialized");
        }
        System.out.println("sendBrevoEmail....");
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
