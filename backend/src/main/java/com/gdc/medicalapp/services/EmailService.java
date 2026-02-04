package com.gdc.medicalapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@medicalapp.com}")
    private String fromEmail;

    @Value("${app.name:MedicalApp}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía el email de recuperación de contraseña
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink, int expirationMinutes) {
        String subject = "Restablecer contraseña - " + appName;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" style="width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; margin-top: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">%s</h1>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin-top: 0;">Hola %s,</h2>
                            
                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en %s.
                            </p>
                            
                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                Haz clic en el siguiente botón para crear una nueva contraseña:
                            </p>
                            
                            <!-- Button -->
                            <table role="presentation" style="margin: 30px auto;">
                                <tr>
                                    <td style="border-radius: 8px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);">
                                        <a href="%s" target="_blank" style="display: inline-block; padding: 16px 36px; font-size: 16px; color: #ffffff; text-decoration: none; font-weight: bold;">
                                            Restablecer Contraseña
                                        </a>
                                    </td>
                                </tr>
                            </table>
                            
                            <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                Este enlace expirará en <strong>%d minutos</strong>.
                            </p>
                            
                            <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                Si no solicitaste restablecer tu contraseña, puedes ignorar este correo de forma segura.
                            </p>
                            
                            <hr style="border: none; border-top: 1px solid #eeeeee; margin: 30px 0;">
                            
                            <p style="color: #999999; font-size: 12px;">
                                Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:
                            </p>
                            <p style="color: #667eea; font-size: 12px; word-break: break-all;">
                                %s
                            </p>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px 30px; text-align: center;">
                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                © 2026 %s. Todos los derechos reservados.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(appName, userName != null ? userName : "Usuario", appName, resetLink, expirationMinutes, resetLink, appName);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * Envía confirmación de cambio de contraseña
     */
    @Async
    public void sendPasswordChangedConfirmation(String toEmail, String userName) {
        String subject = "Contraseña actualizada - " + appName;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" style="width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; margin-top: 20px;">
                    <tr>
                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0;">%s</h1>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333;">Hola %s,</h2>
                            <p style="color: #666666; font-size: 16px;">
                                Tu contraseña ha sido actualizada exitosamente.
                            </p>
                            <p style="color: #666666; font-size: 16px;">
                                Si no realizaste este cambio, por favor contacta con nuestro equipo de soporte inmediatamente.
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                            <p style="color: #999999; font-size: 12px;">© 2026 %s</p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(appName, userName != null ? userName : "Usuario", appName);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * Envía email a usuarios OAuth que intentan recuperar contraseña
     */
    @Async
    public void sendOAuthUserPasswordResetEmail(String toEmail, String authProvider) {
        String subject = "Recuperación de contraseña - " + appName;

        String providerName = switch (authProvider) {
            case "GOOGLE" -> "Google";
            case "APPLE" -> "Apple";
            default -> authProvider;
        };

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" style="width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; margin-top: 20px;">
                    <tr>
                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0;">%s</h1>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333;">Información importante</h2>
                            <p style="color: #666666; font-size: 16px;">
                                Tu cuenta está vinculada a <strong>%s</strong> y no tiene una contraseña local configurada.
                            </p>
                            <p style="color: #666666; font-size: 16px;">
                                Para acceder a tu cuenta, utiliza el botón "Iniciar sesión con %s" en nuestra página de login.
                            </p>
                            <p style="color: #999999; font-size: 14px;">
                                Si deseas configurar una contraseña local además de tu inicio de sesión con %s, 
                                por favor contacta con nuestro equipo de soporte.
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                            <p style="color: #999999; font-size: 12px;">© 2026 %s</p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(appName, providerName, providerName, providerName, appName);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error pero no lanzar excepción para no revelar información
            System.err.println("Error enviando email a " + to + ": " + e.getMessage());
        }
    }
}
