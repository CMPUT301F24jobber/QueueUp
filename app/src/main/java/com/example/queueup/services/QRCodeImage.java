package com.example.queueup.services;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class QRCodeImage {

    private final static int QR_CODE_DIMENSION = 500;

    /**
     * Generates a QR code image from the input text.
     *
     * @param qrCodeText
     * @return
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
            e.printStackTrace();
            return null;
        }
    }
}
