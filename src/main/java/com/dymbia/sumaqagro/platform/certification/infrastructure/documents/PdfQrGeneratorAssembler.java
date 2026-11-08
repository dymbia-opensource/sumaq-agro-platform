package com.dymbia.sumaqagro.platform.certification.infrastructure.documents;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Builds the quality certificate PDF, its public QR code and the SHA-256 hash that seals the content.
 */
public final class PdfQrGeneratorAssembler {

    public static final String PUBLIC_VERIFICATION_PATH = "/api/v1/public/traceability";

    private static final int QR_SIZE = 280;

    private PdfQrGeneratorAssembler() {
    }

    /**
     * Generates the certificate document for a graded harvest batch.
     *
     * @param batch             graded batch whose category and score are sealed
     * @param certificateNumber public certificate number
     * @param issuedByUserId    user who issues the certificate
     * @param issuedAt          issuance time
     * @param publicBaseUrl     public site origin, without a trailing path
     * @return the PDF bytes, the QR PNG bytes and the SHA-256 hash
     */
    public static GeneratedCertificate generate(HarvestBatch batch, String certificateNumber, Long issuedByUserId,
                                                Instant issuedAt, String publicBaseUrl) {
        Objects.requireNonNull(batch, "Harvest batch is required");
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new IllegalArgumentException("Certificate number is required");
        }
        if (issuedByUserId == null || issuedByUserId <= 0) {
            throw new IllegalArgumentException("Issuer id must be greater than zero");
        }
        Objects.requireNonNull(issuedAt, "Certificate issue time is required");
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            throw new IllegalArgumentException("Public base URL is required");
        }
        if (batch.getQualityCategory() == null || batch.getQualityScore() == null) {
            throw new IllegalStateException("A certificate requires an assigned quality score");
        }

        String number = certificateNumber.trim();
        String hash = sha256(canonicalContent(batch, number, issuedByUserId, issuedAt));
        String verificationUrl = verificationUrl(publicBaseUrl, number, hash);
        byte[] qrPng = qrPng(verificationUrl);
        byte[] pdf = pdf(batch, number, issuedByUserId, issuedAt, hash, qrPng);
        return new GeneratedCertificate(pdf, qrPng, hash);
    }

    private static String canonicalContent(HarvestBatch batch, String certificateNumber, Long issuedByUserId,
                                           Instant issuedAt) {
        return String.join("|",
                certificateNumber,
                batch.getBatchCode(),
                String.valueOf(batch.getId()),
                batch.getCropType().name(),
                batch.getQualityCategory().name(),
                batch.getQualityScore().toPlainString(),
                String.valueOf(issuedByUserId),
                issuedAt.toString());
    }

    private static String sha256(String content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static String verificationUrl(String publicBaseUrl, String certificateNumber, String hash) {
        String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
        return base + PUBLIC_VERIFICATION_PATH
                + "?number=" + URLEncoder.encode(certificateNumber, StandardCharsets.UTF_8)
                + "&hash=" + hash;
    }

    private static byte[] qrPng(String verificationUrl) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(verificationUrl, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < matrix.getWidth(); x++) {
                for (int y = 0; y < matrix.getHeight(); y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", output);
            return output.toByteArray();
        } catch (WriterException | IOException exception) {
            throw new IllegalStateException("The certificate QR code could not be generated", exception);
        }
    }

    private static byte[] pdf(HarvestBatch batch, String certificateNumber, Long issuedByUserId, Instant issuedAt,
                              String hash, byte[] qrPng) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDImageXObject qrImage = PDImageXObject.createFromByteArray(document, qrPng, "certificate-qr");
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                writeLine(content, "SumaqAgro", 50, 780, titleFont, 22);
                writeLine(content, "Certificado digital de calidad", 50, 750, titleFont, 16);
                writeLine(content, "Numero: " + certificateNumber, 50, 710, bodyFont, 12);
                writeLine(content, "Lote: " + batch.getBatchCode(), 50, 690, bodyFont, 12);
                writeLine(content, "Cultivo: " + batch.getCropType().name(), 50, 670, bodyFont, 12);
                writeLine(content, "Categoria: " + batch.getQualityCategory().name(), 50, 650, bodyFont, 12);
                writeLine(content, "Puntaje: " + batch.getQualityScore().toPlainString(), 50, 630, bodyFont, 12);
                writeLine(content, "Cosecha: " + batch.getHarvestedOn(), 50, 610, bodyFont, 12);
                writeLine(content, "Emitido por: " + issuedByUserId, 50, 590, bodyFont, 12);
                writeLine(content, "Fecha de emision: " + issuedAt, 50, 570, bodyFont, 12);
                writeLine(content, "SHA-256: " + hash, 50, 540, bodyFont, 9);
                content.drawImage(qrImage, 400, 640, 140, 140);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("The quality certificate PDF could not be generated", exception);
        }
    }

    private static void writeLine(PDPageContentStream content, String text, float x, float y, PDType1Font font,
                                  float size) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    /**
     * PDF, QR image and SHA-256 hash of one issued certificate.
     *
     * @param pdf            certificate PDF
     * @param qrPng          QR code PNG that encodes the public verification URL
     * @param sha256Hash     hash that seals the certificate content
     */
    public record GeneratedCertificate(byte[] pdf, byte[] qrPng, String sha256Hash) {
    }
}
