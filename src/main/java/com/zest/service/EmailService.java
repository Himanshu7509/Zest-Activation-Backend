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
                "<head><style>" +
                "body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f5f7fa;margin:0;padding:20px;}" +
                ".container{max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;}" +
                ".header h1{margin:0;font-size:28px;font-weight:600;}" +
                ".header p{margin:10px 0 0;font-size:16px;opacity:0.9;}" +
                ".content{padding:30px;color:#333333;}" +
                ".content h2{color:#667eea;margin-top:0;font-size:24px;}" +
                ".content p{line-height:1.6;margin:15px 0;}" +
                ".highlight{background-color:#f0f4ff;border-left:4px solid #667eea;padding:15px;margin:20px 0;border-radius:0 8px 8px 0;}" +
                ".btn{display:inline-block;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;text-decoration:none;padding:12px 25px;border-radius:25px;font-weight:600;margin:20px 0;text-align:center;}" +
                ".features{margin:25px 0;}" +
                ".feature{display:flex;align-items:center;margin:15px 0;padding:10px;background-color:#f8f9ff;border-radius:8px;}" +
                ".feature-icon{font-size:20px;margin-right:15px;color:#667eea;}" +
                ".footer{background-color:#2c3e50;color:#ecf0f1;padding:20px;text-align:center;font-size:14px;}" +
                ".social{margin-top:15px;}" +
                ".social a{color:#667eea;text-decoration:none;margin:0 10px;}" +
                "</style></head>" +
                "<body><div class='container'>" +
                "<div class='header'><h1>🎉 Welcome to Zest!</h1><p>Your Event Management Journey Starts Now</p></div>" +
                "<div class='content'><h2>Hello " + userName + "!</h2>" +
                "<p>We're thrilled to have you join our community of event creators and attendees. Your account has been successfully created and is ready to use.</p>" +
                "<div class='highlight'><strong>🚀 Get Started:</strong> Log in to your account and explore our powerful event management platform designed to make your events unforgettable.</div>" +
                "<div class='features'><h3>✨ What You Can Do:</h3>" +
                "<div class='feature'><span class='feature-icon'>📅</span><span>Create and manage amazing events</span></div>" +
                "<div class='feature'><span class='feature-icon'>👥</span><span>Connect with event organizers and attendees</span></div>" +
                "<div class='feature'><span class='feature-icon'>🎫</span><span>Book tickets for exciting events</span></div>" +
                "<div class='feature'><span class='feature-icon'>📊</span><span>Track your event performance</span></div>" +
                "</div>" +
                "<p>Need help getting started? Our support team is here for you 24/7. Simply reply to this email or visit our help center.</p>" +
                "<div style='text-align:center;'><a href='#' class='btn'>Go to Your Dashboard</a></div>" +
                "</div>" +
                "<div class='footer'><p>© 2026 Zest Event Management. All rights reserved.</p>" +
                "<div class='social'><a href='#'>📱 Twitter</a> | <a href='#'>📘 Facebook</a> | <a href='#'>📸 Instagram</a></div>" +
                "<p style='margin-top:15px;font-size:12px;opacity:0.8;'>This email was sent to " + toEmail + ". You received this because you created an account with Zest.</p>" +
                "</div></div></body></html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendEventApprovalNotification(String toEmail, String eventName, String eventId) {
        String subject = "🎉 Event Approved - " + eventName;
        String content = "<html>" +
                "<head><style>" +
                "body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f5f7fa;margin:0;padding:20px;}" +
                ".container{max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;}" +
                ".header{background:linear-gradient(135deg,#4CAF50 0%,#2E7D32 100%);color:white;padding:30px;text-align:center;}" +
                ".header h1{margin:0;font-size:28px;font-weight:600;}" +
                ".content{padding:30px;color:#333333;}" +
                ".content h2{color:#4CAF50;margin-top:0;font-size:24px;}" +
                ".highlight{background-color:#e8f5e8;border-left:4px solid #4CAF50;padding:20px;margin:20px 0;border-radius:0 8px 8px 0;}" +
                ".event-details{background-color:#f9f9f9;padding:20px;border-radius:8px;margin:20px 0;}" +
                ".btn{display:inline-block;background:linear-gradient(135deg,#4CAF50 0%,#2E7D32 100%);color:white;text-decoration:none;padding:12px 25px;border-radius:25px;font-weight:600;margin:20px 0;text-align:center;}" +
                ".footer{background-color:#2c3e50;color:#ecf0f1;padding:20px;text-align:center;font-size:14px;}" +
                "</style></head>" +
                "<body><div class='container'>" +
                "<div class='header'><h1>🎉 Congratulations!</h1><p>Your Event Has Been Approved</p></div>" +
                "<div class='content'><h2>Great News, Event Organizer!</h2>" +
                "<p>We're thrilled to inform you that your event <strong>" + eventName + "</strong> has been approved by our admin team.</p>" +
                "<div class='highlight'>" +
                "  <strong>🚀 Your Event is Now Live!</strong><br>" +
                "  Your event is now visible to thousands of potential attendees on our platform." +
                "</div>" +
                "<div class='event-details'>" +
                "  <strong>Event Details:</strong><br>" +
                "  Event Name: " + eventName + "<br>" +
                "  Event ID: " + eventId + "<br>" +
                "  Status:✅ Approved & Live" +
                "</div>" +
                "<p>Start promoting your event and get ready to create an amazing experience for your attendees!</p>" +
                "<div style='text-align:center;'><a href='#' class='btn'>View Your Event Dashboard</a></div>" +
                "</div>" +
                "<div class='footer'><p>© 2026 Zest Event Management. All rights reserved.</p>" +
                "<p style='margin-top:15px;font-size:12px;opacity:0.8;'>This email was sent to " + toEmail + " regarding your event approval.</p>" +
                "</div></div></body></html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendEventRejectionNotification(String toEmail, String eventName, String eventId) {
        String subject = "📝 Event Review - " + eventName;
        String content = "<html>" +
                "<head><style>" +
                "body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f5f7fa;margin:0;padding:20px;}" +
                ".container{max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);overflow:hidden;}" +
                ".header{background:linear-gradient(135deg,#FF9800 0%,#F57C00 100%);color:white;padding:30px;text-align:center;}" +
                ".header h1{margin:0;font-size:28px;font-weight:600;}" +
                ".content{padding:30px;color:#333333;}" +
                ".content h2{color:#FF9800;margin-top:0;font-size:24px;}" +
                ".highlight{background-color:#fff8e1;border-left:4px solid #FF9800;padding:20px;margin:20px 0;border-radius:0 8px 8px 0;}" +
                ".event-details{background-color:#f9f9f9;padding:20px;border-radius:8px;margin:20px 0;}" +
                ".support-box{background-color:#e3f2fd;border:1px solid #2196F3;border-radius:8px;padding:20px;margin:20px 0;}" +
                ".btn{display:inline-block;background:linear-gradient(135deg,#2196F3 0%,#1976D2 100%);color:white;text-decoration:none;padding:12px 25px;border-radius:25px;font-weight:600;margin:10px 5px;text-align:center;}" +
                ".footer{background-color:#2c3e50;color:#ecf0f1;padding:20px;text-align:center;font-size:14px;}" +
                "</style></head>" +
                "<body><div class='container'>" +
                "<div class='header'><h1>📝 Event Review Complete</h1><p>Regarding Your Event Submission</p></div>" +
                "<div class='content'><h2>Thank You for Your Submission</h2>" +
                "<p>We appreciate your effort in submitting your event <strong>" + eventName + "</strong> to our platform.</p>" +
                "<div class='highlight'>" +
                "  <strong>📋 Current Status:</strong><br>" +
                "  Your event submission has been reviewed by our team. While we were unable to approve this particular submission, we encourage you to review our guidelines and consider resubmitting." +
                "</div>" +
                "<div class='event-details'>" +
                "  <strong>Event Details:</strong><br>" +
                "  Event Name: " + eventName + "<br>" +
                "  Event ID: " + eventId + "<br>" +
                "  Status: Review" +
                "</div>" +
                "<div class='support-box'>" +
                "  <strong>💡 Need Help?</strong><br>" +
                "  Our support team is here to help you improve your event submission. We'd be happy to provide specific feedback on how to meet our platform standards." +
                "</div>" +
                "<div style='text-align:center;'>" +
                "  <a href='#' class='btn'>Contact Support</a>" +
                "  <a href='#' class='btn'>Review Guidelines</a>" +
                "</div>" +
                "<p>We value your contribution to our event community and look forward to working with you to create amazing events together.</p>" +
                "</div>" +
                "<div class='footer'><p>© 2026 Zest Event Management. All rights reserved.</p>" +
                "<p style='margin-top:15px;font-size:12px;opacity:0.8;'>This email was sent to " + toEmail + " regarding your event submission.</p>" +
                "</div></div></body></html>";

        return sendEmail(toEmail, subject, content);
    }
}