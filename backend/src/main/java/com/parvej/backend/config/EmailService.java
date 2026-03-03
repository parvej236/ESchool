package com.parvej.backend.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Amra Pari | Password Reset Instructions");

            String content = """
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8;padding:20px 0;font-family:Arial,sans-serif;">
                      <tr>
                        <td align="center">
                          <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;padding:40px;">
                    
                            <tr>
                              <td align="center" style="padding-bottom:20px;">
                                <h2 style="margin:0;color:#1f2937;">Amra Pari</h2>
                              </td>
                            </tr>
                    
                            <tr>
                              <td style="color:#374151;font-size:16px;line-height:1.6;">
                                <p>Hello,</p>
                    
                                <p>
                                  We received a request to reset your password. 
                                  Click the button below to set a new password.
                                </p>
                    
                                <p style="text-align:center;margin:30px 0;">
                                  <a href="%s"
                                     style="background-color:#2563eb;
                                            color:#ffffff;
                                            padding:14px 28px;
                                            text-decoration:none;
                                            border-radius:6px;
                                            font-weight:bold;
                                            display:inline-block;">
                                     Reset Password
                                  </a>
                                </p>
                    
                                <p style="font-size:14px;color:#6b7280;">
                                  This link will expire in <strong>15 minutes</strong>.
                                </p>
                    
                                <p style="font-size:14px;color:#6b7280;">
                                  If you did not request this password reset, please ignore this email.
                                </p>
                    
                                <hr style="margin:30px 0;border:none;border-top:1px solid #e5e7eb;">
                    
                                <p style="font-size:12px;color:#9ca3af;text-align:center;">
                                  © 2026 Amra Pari. All rights reserved.<br>
                                  This is an automated email. Please do not reply.
                                </p>
                    
                              </td>
                            </tr>
                    
                          </table>
                        </td>
                      </tr>
                    </table>
                    """.formatted(resetLink);

            helper.setText(content, true); // true = HTML

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send reset email");
        }
    }
}
