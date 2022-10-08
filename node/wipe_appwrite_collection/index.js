const sdk = require('node-appwrite');

module.exports = async function (req, res) {

  // Init SDK
  const client = new sdk.Client();

  const databases = new sdk.Databases(client);

  if(!req.variables["APPWRITE_FUNCTION_ENDPOINT"] || !req.variables["APPWRITE_FUNCTION_API_KEY"] || !req.variables["APPWRITE_FUNCTION_PROJECT_ID"]){
      res.json({success: false, message: "Project details not found."});
      return;
  }

  client
    .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"])
    .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"])
    .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"])
  ;

  try{
    const payload = JSON.parse(req.payload);
    if(!payload.databaseId || !payload.collectionId){
      res.json({success: false, message: "Problem with payload."});
    }
    const documentListPayload = await databases.listDocuments(payload.databaseId, payload.collectionId);
    const documentIdList = documentListPayload?.documents?.map(doc => doc['$id']) ?? [];
    if(!documentIdList.length){
      res.json({success: false, message: "Collection is empty."});
    }
    documentIdList.forEach(async (docId) => {
        await databases.deleteDocument(payload.databaseId, payload.collectionId, docId);
    });
    res.send({success: true});
  } catch(e) {
    res.json({success: false, message: "Collection not found."});
  }

};
