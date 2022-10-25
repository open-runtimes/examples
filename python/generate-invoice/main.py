import json
import base64
from fpdf import FPDF

class PDF(FPDF):
    pass

def error_message(res, message):
    return res.json({"status": True, "message": message})

def main(req, res):
    currency = None
    items = None
    issuer = None
    customer = None
    vat = None
    try:
        payload = json.load(req.payload)
        currency = payload["currency"]
        items = payload["items"]
        issuer = payload["issuer"]
        customer = payload["customer"]
        vat = payload["vat"]
    except Exception as err:
        print(err)
        raise Exception("Payload is invalid.")

    if not currency:
        error_message(res, "Please provide currency")
    elif not items:
        error_message(res, "Please provide items")
    elif not issuer:
        error_message(res, "Please provide issuer")
    elif not customer:
        error_message(res, "Please provide customer")
    elif not vat:
        error_message(res, "Please provide vat")

    pdf = PDF()
    pdf = PDF(orientation = 'P', unit = 'mm', format='A4')
    pdf.add_page()
    pdf.set_xy(0.0,0.0)
    pdf.set_font('Arial', 'B', 16)
    pdf.set_text_color(220, 50, 50)
    pdf.cell(w=210.0, h=40.0, align='C', txt="Invoice", border=0)

    pdf.set_xy(10.0,80.0)
    pdf.set_text_color(0, 0, 0)
    pdf.set_font('Arial', '', 12)
    pdf.multi_cell(0,10, "Issuer: {}".format(issuer))
    pdf.multi_cell(0,10, "Customer: {}".format(customer))
    pdf.multi_cell(0,10, "Currency: {}".format(currency))
    pdf.multi_cell(0,10, "VAT: {}".format(vat))

    total_price = 0

    for item in items:
        pdf.multi_cell(0,10, "Items{}:\tPrice: {}".format(item["name"], item["price"]))
        total_price += item["price"]

    pdf.multi_cell(0,10, "Total Price: {}".format(total_price))
    pdf.output("invoice.pdf")
    encoded_string = None
    with open("invoice.pdf", "rb") as pdf_file:
        encoded_string = base64.b64encode(pdf_file.read())

    return res.json({"status": True, "invoice": encoded_string})



