package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.repositories.DiningTableRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.TABLE_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final DiningTableRepository tableRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public byte[] generateQRCodeImage(String text, int width, int height) throws IOException, WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    public byte[] generateQRCodeForTable(Long tableId) {
        log.info("Generation of QR codes for table identification: {}", tableId);

        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException(TABLE_NOT_FOUND_MESSAGE));

        String url = frontendUrl + "/table/" + table.getQrCode();

        try {
            byte[] qrImage = generateQRCodeImage(url, 300, 300);
            log.info("QR code successfully generated for desktop {}", table.getNumber());
            return qrImage;
        } catch (Exception e) {
            log.error("Error generating QR code for table {}: {}", tableId, e.getMessage());
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    public byte[] generateQRCodeFromString(String qrCode) {
        String url = frontendUrl + "/table/" + qrCode;

        try {
            return generateQRCodeImage(url, 300, 300);
        } catch (Exception e) {
            log.error("Error generando QR: {}", e.getMessage());
            throw new RuntimeException("Error generando código QR", e);
        }
    }
}
