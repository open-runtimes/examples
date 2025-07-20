const QRCode = require("qrcode");

module.exports = async (req, res) => {
  try {
    // Debug toàn bộ request object
    console.log("=== FULL REQUEST DEBUG ===");
    console.log("req keys:", Object.keys(req));
    console.log("req.body:", req.body);
    console.log("req.payload:", req.payload);
    console.log("req.variables:", req.variables);
    console.log("req.headers:", req.headers);
    console.log("req method:", req.method);

    // Try multiple ways to get text
    const text = req.body?.text || req.payload?.text || req.variables?.text || req.data?.text || "Hello World";

    console.log("Final text used:", text);

    const qrCode = await QRCode.toDataURL(text);

    return res.json({
      success: true,
      text: text,
      qrCode: qrCode,
      debug: {
        body: req.body,
        payload: req.payload,
        variables: req.variables,
      },
    });
  } catch (err) {
    console.error("Error:", err);
    return res.json({
      success: false,
      error: err.message,
    });
  }
};
