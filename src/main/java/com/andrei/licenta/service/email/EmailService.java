package com.andrei.licenta.service.email;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

import static com.andrei.licenta.constants.EmailConstants.*;


@Service
public class EmailService {


    @Async
    public void sendEmail(String firstName, String mail, String link) throws MessagingException {
        Message message = createEmail(firstName, mail, link);
        Transport.send(message);

    }

    @Async
    public void sendEmail(String mail, String mailMessage) throws MessagingException {
        Message message = createAnuntEmail(mail,mailMessage);
        Transport.send(message);
    }

    @Async
    public void sendForgotPasswordMail(String mail, String firstName,String link) throws MessagingException {
        Message message = createForgotPassword(mail,firstName,link);
        Transport.send(message);
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GRID_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(DEBUG, true);
        properties.put("mail.smtp.socketFactory.port", DEFAULT_PORT);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(API_KEY,
                        PASSWORD);
            }
        };

        return Session.getInstance(properties, auth);
    }

    private Message createEmail(String firstName, String mail, String link) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        MimeBodyPart bodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        String html = "Salut," + firstName + " apasa pe acest <a href = " + link + ">Link</a> pentru a confirma mailul";

        //de la cine
        message.setFrom(new InternetAddress(FROM_EMAIL));
        //catre cine
        bodyPart.setText(html, "UTF-8", "html");
        multipart.addBodyPart(bodyPart);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setContent(multipart);
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Message createAnuntEmail(String mail, String mailMessaage) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        MimeBodyPart bodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();

        //de la cine
        message.setFrom(new InternetAddress(FROM_EMAIL));
        //catre cine
        bodyPart.setText(mailMessaage, "UTF-8", "html");
        multipart.addBodyPart(bodyPart);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT_Anunt);
        message.setContent(multipart);
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }


    private Message createForgotPassword(String mail,String firstName, String link) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        MimeBodyPart bodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();

        String html = "Salut," + firstName + " apasa pe acest <a href = " + link + ">Link</a> pentru a schimba parola";
        //de la cine
        message.setFrom(new InternetAddress(FROM_EMAIL));
        //catre cine
        bodyPart.setText(html, "UTF-8", "html");
        multipart.addBodyPart(bodyPart);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject("Parola uitata");
        message.setContent(multipart);
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }
}
