const PDFDOC = require("pdfkit")
const doc = new PDFDOC();
const data  = {
    "currency":"EUR",
    "items":[{"name":"Web development","price":15}],
    "issuer":"Some Issuer",
    "customer":"Some Customer",
    // "vat":21
}
const generateInvoice = (data) => {
    if(!data.issuer || data.issuer ==="") return {status:false,message:"Please provide Issuer"}
    if(!data.customer || data.customer ==="") return {status:false,message:"Please provide Customer"}
    if(!data.vat) return {status:false,message:"Please provide Vat"}
    if(!data.currency || data.currency ==="") return {status:false,message:"Please provide Currency"}
    if(!data.items) return {status:false,message:"Please provide Items"}
    doc
    .fontSize(26).text("Invoice",{
        align:"center",
        underline:true,
        lineGap:10,

    } ,100, 100);

    doc.moveDown();
    doc.fontSize(15).text(`Issuer: ${data.issuer}`,{align:"left"});
    doc.moveDown();
    doc.text(`Customer: ${data.customer}`,{align:"left"});
    doc.moveDown();
    doc.text(`Currency: ${data.currency}`,{align:"left"});
    doc.moveDown();
    doc.text(`VAT: ${data.vat}%`,{align:"left"});
    doc.moveDown();
    doc.text(`Items:${data.items[0].name} ,Price:${data.items[0].price}`,{align:"left"});
    doc.end()
    const output = doc.read();
    const string = output.toString("base64");
    return {status:true,invoice:string}
}