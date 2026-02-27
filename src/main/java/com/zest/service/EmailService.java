package com.zest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${mail.from}")
    private String fromEmail;

    public boolean sendEmail(String toEmail, String subject, String content) {
        log.info("Attempting to send email. API Key present: {}, From email: {}", 
                 apiKey != null && !apiKey.isEmpty(), fromEmail);
        
        try {
            // Create the mail object using the constructor that accepts all parameters
            Email from = new Email(fromEmail);
            Email to = new Email(toEmail);
            Content emailContent = new Content("text/html", content);
            
            Mail mail = new Mail(from, subject, to, emailContent);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
             log.info("SendGrid response status: {}", response.getStatusCode());
            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (Exception e) {
            log.error("Error sending email to: {}. Exception type: {}, Message: {}", toEmail, e.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
    }

    public boolean sendRegistrationConfirmation(String toEmail, String userName) {
        String subject = "Welcome to Zest - Registration Confirmation";
        String content = "<html>" +
                "<body>" +
                "<h2>Welcome to Zest, " + userName + "!</h2>" +
                "<p>Your registration was successful.</p>" +
                "<p>You can now log in to your account and start exploring our event management platform.</p>" +
                "<p>If you have any questions, feel free to contact our support team.</p>" +
                "<br>" +
                "<p>Best regards,<br>The Zest Team</p>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendEventApprovalNotification(String toEmail, String eventName, String eventId) {
        String subject = "Event Approval Notification - " + eventName;
        String content = "<html>" +
                "<body>" +
                "<h2>Congratulations! Your event has been approved</h2>" +
                "<p>Dear Organizer,</p>" +
                "<p>We're excited to inform you that your event <strong>" + eventName + "</strong> has been approved by our admin team.</p>" +
                "<p>Event ID: " + eventId + "</p>" +
                "<p>Your event is now live and visible to users on our platform.</p>" +
                "<br>" +
                "<p>Best regards,<br>The Zest Team</p>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendEventRejectionNotification(String toEmail, String eventName, String eventId) {
        String subject = "Event Rejection Notification - " + eventName;
        String content = "<html>" +
                "<body>" +
                "<h2>Event Rejection Notification</h2>" +
                "<p>Dear Organizer,</p>" +
                "<p>We regret to inform you that your event <strong>" + eventName + "</strong> has been rejected by our admin team.</p>" +
                "<p>Event ID: " + eventId + "</p>" +
                "<p>If you believe this decision was made in error, please contact our support team for clarification.</p>" +
                "<br>" +
                "<p>Best regards,<br>The Zest Team</p>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }
}