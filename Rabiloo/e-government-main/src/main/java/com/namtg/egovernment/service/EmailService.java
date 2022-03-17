package com.namtg.egovernment.service;

import com.namtg.egovernment.dto.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendMail(EmailTemplate email) {
        new Thread(() -> {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            try {
                helper.setTo(email.getReceiver());
                helper.setSubject(email.getSubject());
                helper.setText(email.getContent());

                emailSender.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMailNotifyConclusionPost(String emailReceiver, List<String> listTitlePost) {
        EmailTemplate email = new EmailTemplate();
        email.setReceiver(emailReceiver);
        email.setSubject("Thông báo thời gian đưa ra kết luận bài viết");
        email.setContent("Sắp đến thời gian đưa ra kết luận cho bài viết " + listTitlePost.toString());
        sendMail(email);
    }
}
