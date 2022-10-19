package pl.devims.service;

public interface EmailService {

    void sendMail(String to, String subject, String text, boolean isHtml);
}
