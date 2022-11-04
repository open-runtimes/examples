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

  console.log("Starting function");

  // initialize appwrite client
  if (
    !req.variables["APPWRITE_FUNCTION_ENDPOINT"] ||
    !req.variables["APPWRITE_FUNCTION_API_KEY"]
  ) {
    console.warn(
      "Environment variables are not set. Function cannot use Appwrite SDK."
    );
    res.json({ success: false, message: "Environment variables are not set." });
    return;
  } else {
    client
      .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"])
      .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"])
      .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"]);
  }
 
  // parse payload
  const payload = req.payload;
  const { bucketId } = JSON.parse(payload);
  if (!bucketId) {
    console.warn("Bucket ID is not set.");
    res.json({ success: false, message: "Bucket ID is not set." });
    return;
  }

  // get files
  let filesFound = true;
  let offset = 0;
  let files = [];

  console.log("Getting files from bucket: " + bucketId);
  while (filesFound) {
    try {
      // fetch files from appwrite
      const sto = await storage.listFiles(bucketId, [
        sdk.Query.limit(50),
        sdk.Query.offset(offset),
      ]);
      if (sto.files.length && sto.files.length > 0) {
        // some files were found, save them for deletion
        files = files.concat(sto.files);

        offset += sto.files.length;
      } else {
        // no more files were found
        filesFound = false;
      }
    } catch (err) {
      console.warn(err);
      res.json({ success: false, message: "Bucket not found." });
      return;
    }
  }

  console.log(`Found ${files.length} files.`);
  if (files.length === 0) {
    console.log("No files found.");
    res.json({ success: false, message: "No files found." });
    return;
  }

  // add all deleteFile() requests to a list so we can run them all at once later
  const deleteStack = [];
  for (const file of files) {
    deleteStack.push(storage.deleteFile(bucketId, file.$id));
  }

  const results = await Promise.allSettled(deleteStack);
  for (const result of results) {
    if (result.status === "rejected") {
      console.warn(result.reason);
      res.json({ success: false, message: "Failed to delete files." });
      return;
    }
  }

  res.json({
    success: true,
  });
};
