package com.iris.software.jiracompliance.application.ports.outbound;

import org.springframework.core.io.InputStreamSource;

import java.util.Set;

public interface EmailNotification {
    void sendMail(
            String from,
            Set<String> to,
            Set<String> cc,
            String subject,
            String htmlBody,
            String attachmentFilename,
            InputStreamSource inputStreamSource);

    void sendMail(
            String from,
            Set<String> to,
            Set<String> cc,
            String subject,
            String htmlBody);
}
