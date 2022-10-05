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
    !req.variables["APPWRITE_FUNCTION_ENDPOINT"] ||
    !req.variables["APPWRITE_FUNCTION_API_KEY"]
  ) {
    console.warn(
      "Environment variables are not set. Function cannot use Appwrite SDK."
    );
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
    return;
  }

  // get files
  let filesFound = true;
  let offset = 0;
  let files = {
    total: 0,
    files: [],
  };

  while (filesFound) {
    try {
      // fetch files from appwrite
      const sto = await storage.listFiles(bucketId, [
        sdk.Query.limit(50),
        sdk.Query.limit(0),
      ]);
      if (sto.total && sto.total > 0) {
        // some files were found, save them for deletion
        files.total += sto.total;
        files.files += sto.files;

        offset += files.total;
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

  if (!files.total || files.total === 0) {
    console.log("No files found.");
    return;
  }

  // add all deleteFile() requests to a list so we can run them all at once later
  const deleteStack = [];
  for (const file of files.files) {
    deleteStack.push(await storage.deleteFile(bucketId, file.$id));
  }

  const result = await Promise.allSettled(deleteStack);
  //TODO: iterate over result to make sure every promise resolved correctly

  res.json({
    success: true,
  });
};
