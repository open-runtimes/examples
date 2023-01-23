const sdk = require('node-appwrite');

module.exports = async function (req, res) {
  const client = new sdk.Client();
  const databases = new sdk.Databases(client);

  if(!req.variables["APPWRITE_FUNCTION_ENDPOINT"] || !req.variables["APPWRITE_FUNCTION_API_KEY"] || !req.variables["APPWRITE_FUNCTION_PROJECT_ID"]) {
      res.json({success: false, message: "Variables missing."});
      return;
  }

  client
    .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"])
    .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"])
    .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"]);

  try{
    const payload = JSON.parse(req.payload ?? '{}');
    if(!payload.databaseId || !payload.collectionId) {
      res.json({success: false, message: "Invalid payload."});
      return;
    }

    let sum = 0;
    let done = false;

    while(!done) {
      const response = await databases.listDocuments(payload.databaseId, payload.collectionId);
      const documents = response.documents;

      for(const document of documents) {
        await databases.deleteDocument(payload.databaseId, payload.collectionId, document.$id);
        sum++;
      }

      if(documents.length === 0) {
        done = true;
      }
    }

    res.send({success: true, sum: sum});
  } catch(e) {
    res.json({success: false, message: "Unexpected error: " + e});
  }

};
