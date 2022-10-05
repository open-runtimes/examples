const sdk = require("node-appwrite");

/*
  'req' variable has:
    'headers' - object with request headers
    'payload' - request body data as a string
    'variables' - object with function variables

  'res' variable has:
    'send(text, status)' - function to return text response. Status code defaults to 200
    'json(obj, status)' - function to return JSON response. Status code defaults to 200

  If an error is thrown, a response with code 500 will be returned.
*/

module.exports = async function (req, res) {
  const client = new sdk.Client();

  const storage = new sdk.Storage(client);

  // initialize appwrite client
  if (
    !req.variables['APPWRITE_FUNCTION_ENDPOINT'] ||
    !req.variables['APPWRITE_FUNCTION_API_KEY']
  ) {
    console.warn("Environment variables are not set. Function cannot use Appwrite SDK.");
    return;
  } else {
    client
      .setEndpoint(req.variables['APPWRITE_FUNCTION_ENDPOINT'])
      .setProject(req.variables['APPWRITE_FUNCTION_PROJECT_ID'])
      .setKey(req.variables['APPWRITE_FUNCTION_API_KEY']);
  }

  // parse payload
  const payload = req.payload;
  const {bucketId} = JSON.parse(payload);
  if (!bucketId) {
    console.warn("Bucket ID is not set.");
    return;
  }

  // get files
  let files;
  try {
    files = await storage.listFiles(bucketId);
  } catch (err) {
    console.warn(err);
    res.json({success: false, message: "Bucket not found."});
    return;
  }
  if (!files.total || files.total === 0) {
    console.log("No files found.");
    return;
  }

  // delete files
  for (const file of files.files) {
    try {
      await storage.deleteFile(bucketId, file.$id);
    } catch (err) {
      console.warn(err);
      res.json({success: false, message: `Could not delete file ${file.$id}.`});
      return;
    }
  }

  res.json({
    success: true,
  });
};
