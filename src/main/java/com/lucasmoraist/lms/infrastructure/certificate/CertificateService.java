package com.lucasmoraist.lms.infrastructure.certificate;

import com.lucasmoraist.lms.domain.exceptions.CertificateException;
import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.domain.gateway.CertificateGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService implements CertificateGateway {

    private static final String TEMPLATE_PATH = "certificate/template.png";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final BucketGateway bucketGateway;

    @Override
    public byte[] generatePdf(String studentName, String courseTitle, LocalDateTime issuedAt) {
        try (PDDocument document = new PDDocument()) {
            BufferedImage template = loadTemplate();
            float width = template.getWidth();
            float height = template.getHeight();

            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            PDImageXObject background = LosslessFactory.createFromImage(document, template);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(background, 0, 0, width, height);

                PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                drawCenteredText(contentStream, titleFont, 28, width, height * 0.62f, studentName);
                drawCenteredText(contentStream, bodyFont, 18, width, height * 0.48f,
                        "concluiu com sucesso o curso");
                drawCenteredText(contentStream, titleFont, 22, width, height * 0.38f, courseTitle);
                drawCenteredText(contentStream, bodyFont, 14, width, height * 0.22f,
                        "Emitido em " + issuedAt.format(DATE_FORMATTER));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            log.error("Failed to generate certificate PDF for student {} and course {}", studentName, courseTitle, ex);
            throw new CertificateException("Failed to generate certificate PDF", ex);
        }
    }

    @Override
    public void uploadPdf(String storageKey, byte[] pdfContent) {
        bucketGateway.uploadBytes(storageKey, pdfContent, "application/pdf");
    }

    private BufferedImage loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                return ImageIO.read(inputStream);
            }
        }

        log.warn("Certificate template not found at {}, using generated fallback", TEMPLATE_PATH);
        return createFallbackTemplate();
    }

    private BufferedImage createFallbackTemplate() {
        int width = 842;
        int height = 595;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(0, 51, 102));
        graphics.setStroke(new BasicStroke(4));
        graphics.drawRect(30, 30, width - 60, height - 60);
        graphics.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 36));
        graphics.drawString("Certificado de Conclusao", 220, 120);
        graphics.dispose();
        return image;
    }

    private void drawCenteredText(PDPageContentStream contentStream, PDType1Font font, float fontSize,
                                  float pageWidth, float y, String text) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (pageWidth - textWidth) / 2;

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

}
