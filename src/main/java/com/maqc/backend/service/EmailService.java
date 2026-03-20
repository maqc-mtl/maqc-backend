package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public void sendContactEmail(ContactFormData form, Property property, User owner) throws MessagingException {
        String toEmail = owner.getEmail();
        String subject = "MAQC - Property Inquiry #" + property.getId() + " - " + form.getSubject();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("daiandre11301@gmail.com");
        helper.setSubject(subject);
        helper.setFrom("andre19880808@gmail.com");

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

    private String buildReceiptEmailTemplate(User user, String planType) {
        String userName = user.getFirstName() + " " + user.getLastName();
        String planPrice = getPlanPrice(planType);
        String purchaseDate = java.time.LocalDate.now().toString();
        String receiptNumber = "MAQC-" + (System.currentTimeMillis() % 1000000);

        String template = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background: linear-gradient(135deg, #1a1a6d 0%, #2563eb 100%);
                            color: white;
                            padding: 30px;
                            border-radius: 10px 10px 0 0;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 700;
                        }
                        .content {
                            background: #f8fafc;
                            padding: 30px;
                            border-radius: 0 0 10px 10px;
                            border: 1px solid #e2e8f0;
                            border-top: none;
                        }
                        .section {
                            margin-bottom: 25px;
                        }
                        .section-title {
                            font-size: 14px;
                            font-weight: 700;
                            color: #64748b;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            margin-bottom: 10px;
                        }
                        .info-grid {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 15px;
                        }
                        .info-item {
                            background: white;
                            padding: 12px;
                            border-radius: 8px;
                            border: 1px solid #e2e8f0;
                        }
                        .info-label {
                            font-size: 11px;
                            color: #94a3b8;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            margin-bottom: 4px;
                        }
                        .info-value {
                            font-size: 14px;
                            color: #1e293b;
                            font-weight: 600;
                        }
                        .total-row {
                            background: #f0f9ff;
                            padding: 15px;
                            border-radius: 8px;
                            border: 1px solid #bae6fd;
                            margin-top: 10px;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #e2e8f0;
                            font-size: 12px;
                            color: #94a3b8;
                        }
                        .welcome-box {
                            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                            color: white;
                            padding: 20px;
                            border-radius: 10px;
                            text-align: center;
                            margin-bottom: 20px;
                        }
                        .welcome-box h2 {
                            margin: 0 0 5px 0;
                            font-size: 20px;
                        }
                        .welcome-box p {
                            margin: 0;
                            opacity: 0.9;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🧾 Payment Receipt</h1>
                    </div>
                    <div class="content">
                        <div class="welcome-box">
                            <h2>Welcome to MAQC!</h2>
                            <p>Your membership is now active</p>
                        </div>

                        <div class="section">
                            <div class="section-title">Billing Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Customer</div>
                                    <div class="info-value">[[userName]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Email</div>
                                    <div class="info-value">[[userEmail]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Date</div>
                                    <div class="info-value">[[purchaseDate]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Receipt #</div>
                                    <div class="info-value">[[receiptNumber]]</div>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="section-title">Order Details</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Plan</div>
                                    <div class="info-value">[[planType]] Membership</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Amount</div>
                                    <div class="info-value">[[planPrice]]</div>
                                </div>
                            </div>
                        </div>

                        <div class="total-row">
                            <div class="flex justify-between items-center">
                                <span style="font-weight: 700; font-size: 16px;">Total Paid</span>
                                <span style="font-weight: 700; font-size: 24px; color: #059669;">[[planPrice]]</span>
                            </div>
                        </div>

                        <div class="section" style="margin-top: 30px;">
                            <div class="info-item" style="background: #fef3c7; border-color: #fbbf24;">
                                <div class="info-label" style="color: #92400e;">Important</div>
                                <div class="info-value" style="color: #92400e;">
                                    Your membership is now active. You can log in to your account and start using all the premium features!
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>This receipt was automatically generated by MAQC.ca</p>
                        <p>© 2026 MAQC.ca All rights reserved</p>
                        <p style="margin-top: 10px;">
                            <a href="http://localhost:3000" style="color: #1a1a6d;">Go to MAQC.ca</a>
                        </p>
                    </div>
                </body>
                </html>
                """;

        String result = template;
        result = result.replace("[[userName]]", userName);
        result = result.replace("[[userEmail]]", user.getEmail());
        result = result.replace("[[purchaseDate]]", purchaseDate);
        result = result.replace("[[receiptNumber]]", receiptNumber);
        result = result.replace("[[planType]]", planType);
        result = result.replace("[[planPrice]]", planPrice);
        return result;
    }

    private String getPlanPrice(String planType) {
        return switch (planType.toLowerCase()) {
            case "basic" -> "$0.00";
            case "premium" -> "$49.99";
            case "pro" -> "$99.99";
            default -> "$29.99";
        };
    }

    private String buildEmailTemplate(Map<String, Object> variables) {
        String template = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background: linear-gradient(135deg, #1a1a6d 0%, #2563eb 100%);
                            color: white;
                            padding: 30px;
                            border-radius: 10px 10px 0 0;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 700;
                        }
                        .content {
                            background: #f8fafc;
                            padding: 30px;
                            border-radius: 0 0 10px 10px;
                            border: 1px solid #e2e8f0;
                            border-top: none;
                        }
                        .section {
                            margin-bottom: 25px;
                        }
                        .section-title {
                            font-size: 14px;
                            font-weight: 700;
                            color: #64748b;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            margin-bottom: 10px;
                        }
                        .info-grid {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 15px;
                        }
                        .info-item {
                            background: white;
                            padding: 12px;
                            border-radius: 8px;
                            border: 1px solid #e2e8f0;
                        }
                        .info-label {
                            font-size: 11px;
                            color: #94a3b8;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            margin-bottom: 4px;
                        }
                        .info-value {
                            font-size: 14px;
                            color: #1e293b;
                            font-weight: 600;
                        }
                        .message-box {
                            background: white;
                            padding: 20px;
                            border-radius: 8px;
                            border: 1px solid #e2e8f0;
                            margin-top: 10px;
                        }
                        .message-text {
                            font-size: 14px;
                            line-height: 1.7;
                            color: #475569;
                        }
                        .button {
                            display: inline-block;
                            background: #cdcdf3;
                            color: white;
                            padding: 12px 24px;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 600;
                            font-size: 14px;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #e2e8f0;
                            font-size: 12px;
                            color: #94a3b8;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🏠 New Property Inquiry</h1>
                    </div>
                    <div class="content">
                        <div class="section">
                            <div class="section-title">Property Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Property</div>
                                    <div class="info-value">[[propertyTitle]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Address</div>
                                    <div class="info-value">[[propertyAddress]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Listing ID</div>
                                    <div class="info-value">#[[propertyId]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">View Property</div>
                                    <div class="info-value">
                                        <a href="[[propertyUrl]]" class="button" style="padding: 6px 12px; font-size: 12px;">View on MAQC</a>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="section-title">Interested Buyer</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Name</div>
                                    <div class="info-value">[[buyerName]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Email</div>
                                    <div class="info-value">[[buyerEmail]]</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Phone</div>
                                    <div class="info-value">[[buyerPhone]]</div>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="section-title">Message</div>
                            <div class="message-box">
                                <div class="message-text">[[message]]</div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="info-item" style="background: #fef3c7; border-color: #fbbf24;">
                                <div class="info-label" style="color: #92400e;">Important</div>
                                <div class="info-value" style="color: #92400e;">
                                    Please respond to this inquiry within 24-48 hours for the best customer experience.
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>This email was sent from MAQC.ca - Quebec's Professional FSBO Real Estate Platform</p>
                        <p>© 2026 MAQC.ca All rights reserved</p>
                    </div>
                </body>
                </html>
                """;

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "[[" + entry.getKey() + "]]";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(key, value);
        }
        return result;
    }

    public static class ContactFormData {
        private String subject;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String message;

        public ContactFormData(String subject, String firstName, String lastName, String email, String phone,
                String message) {
            this.subject = subject;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.message = message;
        }

        public String getSubject() {
            return subject;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getMessage() {
            return message;
        }
    }
}