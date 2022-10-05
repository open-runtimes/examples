import { PDFDocument } from "https://cdn.pika.dev/pdf-lib@^1.7.0";

export default async function (req: any, res: any) {
  const payload = JSON.parse(req.payload);
  const { issuer, customer, vat, currency, items } = payload;
  let err = "";
  if (!issuer || issuer === "") err = "issuer";
  if (!customer || customer === "") err = "customer";
  if (!vat || vat === "") err = "val";
  if (!currency || currency === "") err = "currency";
  if (!items || items.length === 0) err = "items";

  if (err != "") {
    res.send({ "success": false, "message": `Please provide a ${err}.` });
  } else {
    const pdfDoc = await PDFDocument.create();
    let page = pdfDoc.addPage([550, 750]);
    page.moveTo(110, 200);
    page.drawText("Invoice", { x: 50, y: 700, size: 32 });
    page.drawText(`Issuer: ${issuer}`, { x: 50, y: 600, size: 20 });
    page.drawText(`Customer: ${customer}`, { x: 50, y: 580, size: 20 });
    page.drawText(`Currency: ${currency}`, { x: 50, y: 560, size: 20 });
    page.drawText(`VAT: ${vat}%`, { x: 50, y: 540, size: 20 });
    let yCoordinate = 520;
    let totalPrice = 0;
    for (let index = 0; index < items.length; index++) {
      yCoordinate -= 20;
      if (yCoordinate <= 60) {
        yCoordinate = 700;
        page = pdfDoc.addPage([550, 750]);
      }
      page.drawText(
        `Items:${items[index].name}............Price:${items[index].price}`,
        {
          x: 50,
          y: yCoordinate,
          size: 20,
        },
      );
      totalPrice += items[index].price;
      if (index === items.length - 1) {
        page.drawText(`Total:${totalPrice}`, {
          x: 50,
          y: yCoordinate - 20,
          size: 20,
        });
      }
    }

    const pdfBytes = await pdfDoc.save();
    const string = btoa(pdfBytes);

    res.send({ success: true, invoice: string });
  }
}
