package com.code10.xml.service;

import com.code10.xml.model.XmlWrapper;
import com.code10.xml.util.XPathUtil;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.mail.internet.MimeMessage;
import javax.xml.xpath.XPathConstants;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendReviewSubmittedMail(XmlWrapper paper, String emailAddress, String review, InputStream pdf) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Your paper has been reviewed");
            message.setText(String.format("Your paper \"%s\" has been reviewed. \n\n %s", title, review), true);
            message.addAttachment("review.html", new ByteArrayResource(review.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("review.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendCoverLetterMail(String emailAddress, String letter, InputStream pdf) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Cover letter submitted");
            message.setText(letter, true);
            message.addAttachment("letter.html", new ByteArrayResource(letter.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("letter.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendEvaluationMail(String emailAddress, String username, String title, String evaluation, InputStream pdf) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Evaluation submitted");
            message.setText(String.format("Evaluation for \"%s\" has been submitted by user \"%s\". \n\n %s", title, username, evaluation), true);
            message.addAttachment("evaluationForm.html", new ByteArrayResource(evaluation.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("evaluationForm.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperPublishedMail(XmlWrapper paper, String emailAddress) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Publishing System: Your paper has been published");
        message.setFrom(senderEmail);
        message.setTo(emailAddress);
        message.setText(String.format("Your paper \"%s\" has been published.", title));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperRejectedMail(XmlWrapper paper, String emailAddress) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Publishing System: Your paper has been rejected");
        message.setFrom(senderEmail);
        message.setTo(emailAddress);
        message.setText(String.format("Your paper \"%s\" has been rejected.", title));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperAssignedMail(XmlWrapper paper, String emailAddress, String link) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Publishing System: Paper review assigned");
        message.setFrom(senderEmail);
        message.setTo(emailAddress);
        message.setText(String.format("Review of paper \"%s\" has been assigned to you. \n\n %s", title, link));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendReviewerAcceptedMail(XmlWrapper paper, String emailAddress, String username) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Publishing System: Paper review accepted");
        message.setFrom(senderEmail);
        message.setTo(emailAddress);
        message.setText(String.format("Review of paper \"%s\" has been accepted by \"%s\".", title, username));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendReviewerDeclinedMail(XmlWrapper paper, String emailAddress, String username) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Publishing System: Paper review declined");
        message.setFrom(senderEmail);
        message.setTo(emailAddress);
        message.setText(String.format("Review of paper \"%s\" has been declined by \"%s\".", title, username));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperSubmittedMail(XmlWrapper paper, String emailAddress, String html, InputStream pdf) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Paper has been submitted");
            message.setText(String.format("Paper \"%s\" has been submitted. \n\n %s", title, html), true);
            message.addAttachment("paper.html", new ByteArrayResource(html.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("paper.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperRevisedMail(XmlWrapper paper, String emailAddress, String html, InputStream pdf) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Paper revision has been submitted");
            message.setText(String.format("Revision for paper \"%s\" has been submitted. \n\n %s", title, html), true);
            message.addAttachment("revision.html", new ByteArrayResource(html.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("revision.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Async
    public void sendPaperReviewedMail(XmlWrapper paper, String username, String emailAddress, String html, InputStream pdf) {
        final String title = ((Node) XPathUtil.evaluate("/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(emailAddress);
            message.setFrom(senderEmail);
            message.setSubject("Publishing System: Paper review has been submitted");
            message.setText(String.format("Paper review for \"%s\" has been submitted by user \"%s\". \n\n %s", title, username, html), true);
            message.addAttachment("revision.html", new ByteArrayResource(html.getBytes(StandardCharsets.UTF_8)));
            message.addAttachment("revision.pdf", new ByteArrayResource(IOUtils.toByteArray(pdf)));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
