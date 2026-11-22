package com.dymbia.sumaqagro.platform.shared.application.outboundservices.email;

/**
 * Outbound port to send notification emails, shared by every bounded context.
 * <p>
 * The contexts only know this contract; the infrastructure decides the provider (Brevo) or
 * disables the delivery when notifications are turned off.
 * </p>
 *
 * @since 1.1
 */
public interface EmailSender {

    /**
     * Sends one HTML email.
     *
     * @param recipientEmail the destination address
     * @param subject        the subject
     * @param htmlContent    the body, already escaped by the caller
     * @throws DeliveryFailed when the provider rejects the email or cannot be reached
     */
    void send(String recipientEmail, String subject, String htmlContent);

    /**
     * Tells whether emails really leave the application.
     *
     * @return {@code false} when notifications are turned off
     */
    boolean isEnabled();

    final class DeliveryFailed extends RuntimeException {
        public DeliveryFailed(String reason) {
            super(reason);
        }
    }
}
