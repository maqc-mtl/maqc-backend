package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public abstract class BaseEmailService {

    @Value("${app.frontend-url}")
    protected String frontendUrl;

    protected String buildPasswordResetEmailTemplate(User user, String resetUrl) {
        String userName = user.getFirstName() + " " + user.getLastName();

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
                            text-transform: uppercase;
                        }
                        .header p {
                            margin: 10px 0 0;
                            font-size: 14px;
                            opacity: 0.9;
                            font-weight: 500;
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
                        .button-container {
                            margin-top: 40px;
                            text-align: center;
                        }
                        .button {
                            display: inline-block;
                            background-color: #1a1a6d;
                            color: #ffffff !important;
                            padding: 16px 32px;
                            border-radius: 12px;
                            font-size: 14px;
                            font-weight: 700;
                            text-decoration: none;
                            text-transform: uppercase;
                            letter-spacing: 0.05em;
                            box-shadow: 0 4px 6px -1px rgba(26, 26, 109, 0.2);
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
                        .footer a {
                            color: #1a1a6d;
                            text-decoration: none;
                            font-weight: 600;
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
                            <h1>Password Reset</h1>
                            <p>MAQC - Quebec's Real Estate Platform</p>
                        </div>
                        <div class="content">
                            <div class="greeting">Hi [[userName]],</div>
                            <p style="margin-bottom: 24px; color: #64748b; font-size: 14px;">We received a request to reset your password. Click the button below to create a new password. This link will expire in 24 hours.</p>

                            <div class="button-container">
                                <a href="[[resetUrl]]" class="button">Reset Password</a>
                            </div>

                            <p style="margin-top: 24px; font-size: 12px; color: #94a3b8;">If you didn't request this password reset, please ignore this email or contact support if you have concerns.</p>
                        </div>
                        <div class="footer">
                            <p>Questions? Contact our support team at <a href="mailto:support@maqc.ca">support@maqc.ca</a></p>
                            <p>&copy; 2026 MAQC.ca - Quebec's Real Estate Platform</p>
                            <p><a href="[[frontendUrl]]">www.maqc.ca</a></p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        return template.replace("[[userName]]", userName)
                .replace("[[resetUrl]]", resetUrl)
                .replace("[[frontendUrl]]", frontendUrl);
    }

    protected String buildReceiptEmailTemplate(User user, String planType) {
        String userName = user.getFirstName() + " " + user.getLastName();
        int planPriceInt = getPlanPrice(planType);
        BigDecimal planPrice = new BigDecimal(planPriceInt);

        BigDecimal gstRate = new BigDecimal("0.05");
        BigDecimal qstRate = new BigDecimal("0.09975");

        BigDecimal gst = planPrice.multiply(gstRate);
        BigDecimal qst = planPrice.multiply(qstRate);

        BigDecimal total = planPrice.add(gst).add(qst);

        // Round to 2 decimal places
        gst = gst.setScale(2, RoundingMode.HALF_UP);
        qst = qst.setScale(2, RoundingMode.HALF_UP);
        total = total.setScale(2, RoundingMode.HALF_UP);
        String purchaseDate = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")
                .format(java.time.LocalDate.now());
        String receiptNumber = "MAQC-" + (System.currentTimeMillis() % 10000000);

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
                            text-transform: uppercase;
                        }
                        .header p {
                            margin: 10px 0 0;
                            font-size: 14px;
                            opacity: 0.9;
                            font-weight: 500;
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
                        .summary-box {
                            background-color: #f1f5f9;
                            border-radius: 16px;
                            padding: 24px;
                            margin-bottom: 32px;
                            text-align: center;
                        }
                        .summary-label {
                            font-size: 12px;
                            font-weight: 800;
                            color: #64748b;
                            text-transform: uppercase;
                            letter-spacing: 0.1em;
                            margin-bottom: 8px;
                        }
                        .summary-value {
                            font-size: 32px;
                            font-weight: 800;
                            color: #1a1a6d;
                            letter-spacing: -0.02em;
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
                        .grid {
                            display: table;
                            width: 100%;
                            table-layout: fixed;
                            margin-bottom: 24px;
                        }
                        .grid-item {
                            display: table-cell;
                            padding-bottom: 20px;
                        }
                        .label {
                            font-size: 11px;
                            font-weight: 700;
                            color: #64748b;
                            text-transform: uppercase;
                            margin-bottom: 4px;
                        }
                        .value {
                            font-size: 14px;
                            font-weight: 600;
                            color: #1e293b;
                        }
                        .details-table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 8px;
                        }
                        .details-table td {
                            padding: 12px 0;
                            border-bottom: 1px solid #f1f5f9;
                        }
                        .details-table .item-name {
                            font-size: 14px;
                            font-weight: 600;
                            color: #1e293b;
                        }
                        .details-table .item-price {
                            text-align: right;
                            font-size: 14px;
                            font-weight: 700;
                            color: #1e293b;
                        }
                        .button-container {
                            margin-top: 40px;
                            text-align: center;
                        }
                        .button {
                            display: inline-block;
                            background-color: #1a1a6d;
                            color: #ffffff !important;
                            padding: 16px 32px;
                            border-radius: 12px;
                            font-size: 14px;
                            font-weight: 700;
                            text-decoration: none;
                            text-transform: uppercase;
                            letter-spacing: 0.05em;
                            box-shadow: 0 4px 6px -1px rgba(26, 26, 109, 0.2);
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
                        .footer a {
                            color: #1a1a6d;
                            text-decoration: none;
                            font-weight: 600;
                        }
                        @media only screen and (max-width: 480px) {
                            .content { padding: 24px; }
                            .grid-item { display: block; width: 100%; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>MAQC RECEIPT</h1>
                            <p>Transaction ID: [[receiptNumber]]</p>
                        </div>
                        <div class="content">
                            <div class="greeting">Hi [[userName]],</div>
                            <p style="margin-bottom: 24px; color: #64748b; font-size: 14px;">Thank you for your purchase! Your membership is now active and you can enjoy full access to our platform.</p>

                            <div class="summary-box">
                                <div class="summary-label">Total Amount Paid</div>
                                <div class="summary-value">$[[total]]</div>
                            </div>

                            <div class="section-title">Order Information</div>
                            <table class="details-table">
                                <tr>
                                    <td class="item-name">[[planType]] Membership</td>
                                    <td class="item-price">$[[planPrice]]</td>
                                </tr>
                                <tr>
                                    <td class="item-name">GST (5%)</td>
                                    <td class="item-price">$[[gst]]</td>
                                </tr>
                                <tr>
                                    <td class="item-name">QST (9.975%)</td>
                                    <td class="item-price">$[[qst]]</td>
                                </tr>
                                <tr>
                                    <td style="font-weight: 700; padding-top: 20px;">Total</td>
                                    <td style="text-align: right; font-weight: 800; font-size: 18px; color: #1a1a6d; padding-top: 20px;">$[[total]]</td>
                                </tr>
                            </table>

                            <div class="section-title" style="margin-top: 32px;">Billing Details</div>
                            <div class="grid">
                                <div class="grid-item">
                                    <div class="label">Customer</div>
                                    <div class="value">[[userName]]</div>
                                </div>
                                <div class="grid-item">
                                    <div class="label">Payment Date</div>
                                    <div class="value">[[purchaseDate]]</div>
                                </div>
                            </div>
                            <div class="grid">
                                <div class="grid-item">
                                    <div class="label">Email</div>
                                    <div class="value">[[userEmail]]</div>
                                </div>
                                <div class="grid-item">
                                    <div class="label">Status</div>
                                    <div class="value" style="color: #059669;">Success</div>
                                </div>
                            </div>

                            <div class="button-container">
                                <a href="[[frontendUrl]]" class="button">Go to My Dashboard</a>
                            </div>
                        </div>
                        <div class="footer">
                            <p>Questions? Contact our support team at <a href="mailto:support@maqc.ca">support@maqc.ca</a></p>
                            <p>&copy; 2026 MAQC.ca - Quebec's Real Estate Platform</p>
                            <p><a href="[[frontendUrl]]">www.maqc.ca</a></p>
                        </div>
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
        result = result.replace("[[planPrice]]", String.valueOf(planPrice));
        result = result.replace("[[total]]", String.valueOf(total));
        result = result.replace("[[gst]]", String.valueOf(gst));
        result = result.replace("[[qst]]", String.valueOf(qst));
        result = result.replace("[[frontendUrl]]", frontendUrl);
        return result;
    }

    protected int getPlanPrice(String planType) {
        return switch (planType.toLowerCase()) {
            case "basic" -> 99;
            case "plus" -> 199;
            case "pro" -> 399;
            default -> 0;
        };
    }

    protected String buildEmailTemplate(Map<String, Object> variables) {
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
                            text-transform: uppercase;
                        }
                        .header p {
                            margin: 10px 0 0;
                            font-size: 14px;
                            opacity: 0.9;
                            font-weight: 500;
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
                            margin-bottom: 32px;
                        }
                        .info-item {
                            display: table-cell;
                            padding: 0 10px 20px 0;
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
                        .message-box {
                            background-color: #f1f5f9;
                            border-radius: 16px;
                            padding: 24px;
                            margin-bottom: 32px;
                        }
                        .message-text {
                            font-size: 15px;
                            line-height: 1.6;
                            color: #334155;
                            white-space: pre-wrap;
                        }
                        .button-container {
                            margin-top: 40px;
                            text-align: center;
                        }
                        .button {
                            display: inline-block;
                            background-color: #2563eb;
                            color: #ffffff !important;
                            padding: 16px 32px;
                            border-radius: 12px;
                            font-size: 14px;
                            font-weight: 700;
                            text-decoration: none;
                            text-transform: uppercase;
                            letter-spacing: 0.05em;
                            box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);
                        }
                        .important-note {
                            background-color: #fffbeb;
                            border: 1px solid #fde68a;
                            border-radius: 12px;
                            padding: 16px;
                            margin-top: 32px;
                            display: flex;
                            align-items: center;
                        }
                        .important-note-text {
                            font-size: 13px;
                            color: #92400e;
                            font-weight: 500;
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
                        .footer a {
                            color: #1a1a6d;
                            text-decoration: none;
                            font-weight: 600;
                        }
                        @media only screen and (max-width: 480px) {
                            .content { padding: 24px; }
                            .info-item { display: block; width: 100%; padding-right: 0; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>PROPERTY INQUIRY</h1>
                            <p>Listing ID: #[[propertyId]]</p>
                        </div>
                        <div class="content">
                            <div class="greeting">Hi [[ownerName]],</div>
                            <p style="margin-bottom: 32px; color: #64748b; font-size: 14px;">You have received a new inquiry for your property listing. Here are the details from the interested buyer:</p>

                            <div class="section-title">Property Details</div>
                            <div class="info-grid">
                                <div class="info-item" style="width: 60%;">
                                    <div class="info-label">Title</div>
                                    <div class="info-value">[[propertyTitle]]</div>
                                </div>
                                <div class="info-item" style="width: 40%;">
                                    <div class="info-label">Address</div>
                                    <div class="info-value">[[propertyAddress]]</div>
                                </div>
                            </div>

                            <div class="section-title">Buyer Information</div>
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

                            <div class="section-title">Message</div>
                                <div class="message-box">
                                <div class="message-text">[[message]]</div>
                            </div>

                            <div class="button-container">
                                <a href="[[propertyUrl]]" class="button">View Listing on MAQC</a>
                            </div>

                            <div class="important-note">
                                <div class="important-note-text">
                                    <strong>Recommendation:</strong> Please respond to this inquiry within 24-48 hours via the provided email or phone for the best results.
                                </div>
                            </div>
                        </div>
                        <div class="footer">
                            <p>This email was sent from MAQC.ca - Quebec's FSBO Platform</p>
                            <p>&copy; 2026 MAQC.ca All rights reserved</p>
                            <p><a href="[[frontendUrl]]">www.maqc.ca</a></p>
                        </div>
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
        result = result.replace("[[frontendUrl]]", frontendUrl);
        return result;
    }

    @Getter
    public static class ContactFormData {
        private String subject;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String message;

        public ContactFormData() {
        }

        public ContactFormData(String subject, String firstName, String lastName, String email, String phone,
                String message) {
            this.subject = subject;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.message = message;
        }
    }
}
