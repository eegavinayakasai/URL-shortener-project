package com.evs.UrlShortenerProject.service;



import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService
{
    @Value("${api_key}")
    private String API_KEY;

    @Value("${mail.from}")
    private String FROM_EMAIL;

    public void  sendOtpToEmail(String toEmail, String otp)
    {
        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);
        String subject = "OTP for Email verification";
        Content content = new Content("text/plain", "Your OTP is: " + otp + "\nExpires in 5 minutes");
        Mail mail = new Mail(from,subject,to,content);
        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try
        {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            com.sendgrid.Response response = sg.api(request);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to send email");
        }
    }
}
