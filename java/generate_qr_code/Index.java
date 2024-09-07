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
            
            String base64QR = generateQR((String) payload.get("text"));

            Map<String, Object> map = new HashMap<>();
            map.put("message", base64QR);
            map.put("success", true);
            return res.json(map, 200);
        } catch (NoSuchFieldException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            map.put("success", false);
            return res.json(map, 400);
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

    // Returns QR code in Base64 string format for the input text
    public String generateQR(String text) {

        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
        BufferedImage img = qr.toImage(10, 3); // get  BufferedImage for qr

        // convert image to base 64 string
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

    }
}
