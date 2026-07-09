package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Async
    public void sendOtpByEmail(String toEmail, String otp){
        String subject = "LabTrack - Email Verification OTP";
        String body = """
                 <div style="font-family: Arial, Helvetica, sans-serif; max-width: 600px; margin: 0 auto; padding: 30px; border: 1px solid #e5e7eb; border-radius: 10px; background-color: #ffffff;">

                <h2 style="color: #2563eb; text-align: center; margin-bottom: 10px;">
                LabTrack
                </h2>

                <p>Dear User,</p>

                <p>
                Welcome to <strong>LabTrack</strong>!
                </p>

                <p>
                Thank you for creating your account. To activate your account and verify your
        email address, please use the One-Time Password (OTP) below:
            </p>

                <div style="
        margin: 30px 0;
        padding: 18px;
        text-align: center;
        background-color: #f8fafc;
        border: 2px dashed #2563eb;
        border-radius: 8px;
        font-size: 32px;
        font-weight: bold;
        letter-spacing: 8px;
        color: #1e3a8a;">
                %s
                </div>

                <p>
                This OTP is valid for <strong>10 minutes</strong>.
            </p>

                <p>
                For your security, do not share this OTP with anyone.
                The LabTrack team will never ask for your OTP or password.
            </p>

                <p>
                If you did not create a LabTrack account, you can safely ignore this email.
                </p>

                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">

                <p style="font-size: 13px; color: #6b7280;">
                This is an automated email. Please do not reply to this message.
                </p>

                <p style="font-size: 13px; color: #6b7280;">
                Regards,<br>
                <strong>Team LabTrack</strong>
                </p>

                </div>
                """.formatted(otp);
                sendHtmlEmail(toEmail, subject, body);
            }



            private void sendHtmlEmail(String toEmail, String subject, String body) {
                try{
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(toEmail);
                    helper.setSubject(subject);
                    helper.setText(body,true);
                    mailSender.send(message);
                    logger.info("Email sent to: {}", toEmail);
                }catch (MessagingException e) {
                    logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
                    throw new RuntimeException("Failed to send email", e);
                }
            }

        }
