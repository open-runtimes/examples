const QRCode = require("qrcode");

module.exports = async (req, res) => {
  const text = req.body?.text || "Hello World";
  const qrCode = await QRCode.toDataURL(text);

  res.json({
    success: true,
    text: text,
    qrCode: qrCode,
  });
};
