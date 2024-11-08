package com.example.queueup.services;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * QRCodeImage is a utility class that provides functionality for generating
 * QR code images based on a given text input.
 */
public class QRCodeImage {

    // Default dimensions for the QR code
    private final static int QR_CODE_DIMENSION = 500;

    /**
     * Generates a QR code image as a Bitmap from a given text input.
     *
     * @param qrCodeText The text to encode into the QR code.
     * @return A Bitmap representation of the generated QR code, or null if an error occurs.
     */
    public static Bitmap generateQrCodeImage(String qrCodeText) {
        try {
            // Create a BitMatrix for the QR code using the input text
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrCodeText, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION);
            // Use BarcodeEncoder to create a Bitmap from the BitMatrix
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();  // Log the exception if an error occurs
            return null;
        }
    }
}
