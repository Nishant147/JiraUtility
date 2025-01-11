package com.iris.software.jiracompliance.outbound.email;

import com.iris.software.jiracompliance.application.ports.outbound.EmailNotification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class EmailNotificationImpl implements EmailNotification {
    private JavaMailSender emailSender;


    @Override
    public void sendMail(String from, Set<String> to, Set<String> cc, String subject, String htmlBody,
                         String attachmentFilename, InputStreamSource inputStreamSource) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to.toArray(new String[0]));
            helper.setCc(cc.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(attachmentFilename, inputStreamSource);
            emailSender.send(message);
        } catch (MessagingException e){
            log.error("Error sending email", e);
        }
    }

    @Override
    public void sendMail(String from, Set<String> to, Set<String> cc, String subject, String htmlBody) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to.toArray(new String[0]));
            helper.setCc(cc.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(message);
        } catch (MessagingException e){
            log.error("Error sending email", e);
        }
    }
}
