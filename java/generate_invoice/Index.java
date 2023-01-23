import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) {
    String payloadString = req.getPayload();
    Map<String, Object> payload = gson.fromJson(payloadString, Map.class);
    Map<String, Object> responseData = new HashMap<>();

    try {
        String currency = Objects.toString(payload.get("currency"), "");
        List<Map<String, Object>> items = (List<Map<String, Object>>)(List<?>) payload.get("items");
        String issuer = Objects.toString(payload.get("issuer"), "");
        String customer = Objects.toString(payload.get("customer"), "");
        Number vat = (Number) payload.get("vat");

        List<String> errorFields = validatePayload(currency, items, issuer, customer, vat);
        if (!errorFields.isEmpty()) {
            throw new NoSuchFieldException("Please provide " + String.join(", ", errorFields));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        doc.open();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 26, Font.BOLD | Font.UNDERLINE);
        Paragraph invoice = new Paragraph("Invoice", font);
        invoice.setAlignment(Element.ALIGN_CENTER);
        doc.add(invoice);

        font.setSize(12);
        font.setStyle(Font.NORMAL);
        doc.add(new Paragraph(50));
        doc.add(new Paragraph("Issuer: " + issuer, font));
        doc.add(new Paragraph("Customer: " + customer, font));
        doc.add(new Paragraph("Currency: " + currency, font));
        doc.add(new Paragraph("VAT: " + vat, font));

        Chunk glue = new Chunk(new VerticalPositionMark());
        Chunk rightChunk = new Chunk(glue);

        font.setStyle(Font.BOLD);
        Paragraph itemHeader = new Paragraph(50, "Item", font);
        itemHeader.add(rightChunk);
        itemHeader.add("Price");
        doc.add(itemHeader);

        font.setStyle(Font.NORMAL);
        double total = 0;
        for (Map<String, Object> item : items) {
            String name = item.get("name").toString();
            double price = (double) item.get("price");

            total += price;
            Paragraph itemParagraph = new Paragraph(name, font);
            itemParagraph.add(rightChunk);
            itemParagraph.add(String.valueOf(price));
            doc.add(itemParagraph);
        }

        font.setStyle(Font.UNDERLINE | Font.BOLD);
        font.setSize(15);
        Paragraph totalParagraph = new Paragraph(20, "Total:", font);
        totalParagraph.add(rightChunk);
        totalParagraph.add(String.valueOf(total));
        doc.add(totalParagraph);
        doc.close();
        writer.close();

        String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());

        responseData.put("success", true);
        responseData.put("invoice", base64String);

        return res.json(responseData);
    } catch (DocumentException | NoSuchFieldException exception) {
        responseData.put("success", false);
        responseData.put("message", exception.getMessage());
        return res.json(responseData);
    }
}

private List<String> validatePayload(String currency, List<Map<String, Object>> items, String issuer, String customer, Number vat) {

    List<String> errors = new ArrayList<>();

    if (currency.isEmpty()) {
        errors.add("currency");
    }

    if (items == null || items.isEmpty()) {
        errors.add("items");
    }

    if (issuer.isEmpty()) {
        errors.add("issuer");
    }

    if (customer.isEmpty()) {
        errors.add("customer");
    }

    if (vat == null) {
        errors.add("vat");
    }

    return errors;
}