package io.openruntimes.java;

import io.nayuki.qrcodegen.QrCode;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;


public class Index {

    public RuntimeOutput main(RuntimeContext context) throws Exception {

        RuntimeRequest req = context.getReq();
        RuntimeResponse res = context.getRes();
        
        Map<String, Object> payload = (Map<String, Object>) req.getBody();
		
	try {
            // validate payload
            List<String> errorFields = validatePayload(payload);
            if (!errorFields.isEmpty()) {
                throw new NoSuchFieldException("Required fields are not present: " + String.join(",", errorFields));
            }

            byte[] qrCode = generateQR((String) payload.get("text"));
            Map<String, String> resHeaders = new HashMap<>();
            resHeaders.put("content-type", "image/png");
            return res.binary(qrCode, 200, resHeaders);
        } catch (Exception e) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", e.getMessage());
            responseData.put("success", false);
            return res.json(responseData, 400);
        }

    }

    // Checks whether all the requried arguments are present and validates it's values
    public List<String> validatePayload(Map<String, Object> payload) {
        List<String> fields = new ArrayList<>();

        if (!payload.containsKey("text") || payload.get("text") == null || payload.get("text").toString().isEmpty()) {
            fields.add("text");
        }

        return fields;
    }

    // Returns QR code(byte[])  in png format for the input text
    public byte[] generateQR(String text) throws Exception{

        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
        BufferedImage img = qr.toImage(10, 3); // get  BufferedImage for qr

        // convert image to base 64 string
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
            os.flush();
            return os.toByteArray();
        } catch (final IOException ioe) {
            throw new IOException("IO error occured.");
        } finally{
	   os.close();
	}

    }
}
