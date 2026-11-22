package com.dymbia.sumaqagro.platform.shared.application.outboundservices.messaging;

/**
 * Outbound port to send short notification messages to a phone (WhatsApp or SMS), shared by every bounded context.
 * <p>
 * The contexts only know this contract; the infrastructure decides the provider (Twilio) or disables the delivery
 * when there are no credentials.
 * </p>
 *
 * @since 1.1
 */
public interface MessageSender {

    /**
     * Sends one text message.
     *
     * @param phoneNumber the destination phone, as the producer wrote it in the profile
     * @param body        the text of the message
     * @throws DeliveryFailed when the phone is not valid, or the provider rejects the message or cannot be reached
     */
    void send(String phoneNumber, String body);

    /**
     * Tells whether messages really leave the application.
     *
     * @return {@code false} when messaging is turned off
     */
    boolean isEnabled();

    final class DeliveryFailed extends RuntimeException {
        public DeliveryFailed(String reason) {
            super(reason);
        }
    }
}
