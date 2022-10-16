const { fetch } = require("undici");
const Ajv = require("ajv");

const ajv = new Ajv({ allErrors: true });
require("ajv-errors")(ajv);

ajv.addFormat("provider", {
  type: "string",
  validate: (x) => x === "openstreets",
});

const schema = {
  type: "object",
  properties: {
    provider: { type: "string", format: "provider" },
    lat: { type: "number", minimum: -90, maximum: 90 },
    lng: { type: "number", minimum: -180, maximum: 180 },
  },
  required: ["provider", "lat", "lng"],
  errorMessage: {
    properties: {
      provider: "Provider must be an openstreets",
      lat: "Latitude must be a valid number between -90 and 90.",
      lng: "Longitude must be a valid number between -180 and 180.",
    },
  },
};

module.exports = async function (req, res) {
  const validate = ajv.compile(schema);

  const payload = JSON.parse(req.payload);
  const valid = validate(payload);

  if (!valid) {
    return res.json({
      success: false,
      message: validate.errors.map((error) => error.message).join(", "),
    });
  }

  const { lat, lng } = payload;

  const z = 16; // zoom level
  const x = Math.floor(((lng + 180) / 360) * Math.pow(2, z));
  const y = Math.floor(
    ((1 -
      Math.log(
        Math.tan((lat * Math.PI) / 180) + 1 / Math.cos((lat * Math.PI) / 180)
      ) /
        Math.PI) /
      2) *
      Math.pow(2, z)
  );

  const result = await fetch(
    `https://tile.openstreetmap.org/${z}/${x}/${y}.png`
  );

  if (result.status != 200) {
    return res.json({ success: false, message: "Openstreetmap server error" });
  }

  const image = await result.arrayBuffer();

  res.json({
    success: true,
    image: Buffer.from(image).toString("base64"),
  });
};
