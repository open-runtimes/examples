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
        
        Map<String, String> payload = req.getQuery();
		
        try {
            List<String> errorFields = validatePayload(payload);

            if (!errorFields.isEmpty()) {
                throw new NoSuchFieldException("Required fields are not present: " + String.join(",", errorFields));
            }

            String text = payload.get("text");
            byte[] qrCode = generateQR(text);

            Map<String, String> headers = new HashMap<>();
            headers.put("content-type", "image/png");

            return res.binary(qrCode, 200, headers);
        } catch (Exception e) {
                Map<String, Object> output = new HashMap<>();
                output.put("message", e.getMessage());
                output.put("success", false);
                return res.json(output, 400);
            }
        }

        // Checks whether all the requried arguments are present and ensure proper value
        // Return array of fields with an error during validation
        public List<String> validatePayload(Map<String, String> payload) {
            List<String> fields = new ArrayList<>();

            if (!payload.containsKey("text") || payload.get("text") == null || payload.get("text").toString().isEmpty()) {
                fields.add("text");
            }

            return fields;
        }

        // Returns QR code in png format
        public byte[] generateQR(String text) throws Exception {
            QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
            QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
            BufferedImage img = qr.toImage(10, 3); // get BufferedImage for qr

            // convert buffer to byte array
            final ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {
                ImageIO.write(img, "png", os);
                os.flush();
                return os.toByteArray();
            } catch (final IOException ioe) {
                throw new IOException("IO error occured.");
            } finally {
                os.close();
            }
        }
}
