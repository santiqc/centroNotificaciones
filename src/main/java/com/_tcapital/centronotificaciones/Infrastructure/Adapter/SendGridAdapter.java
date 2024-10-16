package com._tcapital.centronotificaciones.Infrastructure.Adapter;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sendgrid.*;

import java.io.IOException;

@Component
public class SendGridAdapter {
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendEmail(String recipient, String subject, String body) {
        Email from = new Email("no-reply@yourdomain.com");
        Email to = new Email(recipient);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException("Error sending email", ex);
        }
    }
}
