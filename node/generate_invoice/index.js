const PDFDOC = require("pdfkit")
const doc = new PDFDOC();
export default function generateInvoice(req,res){
    const {currency,items,issuer,customer,vat} = req.payload;
    console.log(issuer);
    if(!issuer || issuer ==="") return {status:false,message:"Please provide Issuer"}
    if(!customer || customer ==="") return {status:false,message:"Please provide Customer"}
    if(!vat) return {status:false,message:"Please provide Vat"}
    if(!currency || currency ==="") return {status:false,message:"Please provide Currency"}
    if(!items) return {status:false,message:"Please provide Items"}
    doc
    .fontSize(26).text("Invoice",{
        align:"center",
        underline:true,
        lineGap:10,

    } ,100, 100);

    doc.moveDown();
    doc.fontSize(15).text(`Issuer: ${issuer}`,{align:"left"});
    doc.moveDown();
    doc.text(`Customer: ${customer}`,{align:"left"});
    doc.moveDown();
    doc.text(`Currency: ${currency}`,{align:"left"});
    doc.moveDown();
    doc.text(`VAT: ${vat}%`,{align:"left"});
    let totalPrice=0;
    doc.moveDown();
    for (let index = 0; index < items.length; index++) {
        doc.text(
          `Items:${items[index].name}          Price:${items[index].price}`,
          {
            x: 50,
          },
        );
        totalPrice += items[index].price;
        if (index === items.length - 1) {
            doc.moveDown();
          doc.fontSize(20).text(`Total:${totalPrice}`, {
            x: 50,
          });
        }
        doc.moveDown();
      }
    doc.end();
    const output = doc.read();
    const string = output.toString("base64");
    res.send({status:true,invoice:string})
}