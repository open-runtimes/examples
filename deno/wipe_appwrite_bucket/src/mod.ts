import { sdk } from "./deps.ts";

export default async function(req: any, res: any) {
  if(
    !req.variables['APPWRITE_FUNCTION_ENDPOINT'] || 
    !req.variables['APPWRITE_FUNCTION_PROJECT_ID'] || 
    !req.variables['APPWRITE_FUNCTION_API_KEY'] ||
    !req.payload
  ) {
    return res.json({
      success: false,
      message: "Missing required environment variables or payload.",
    });
  }
  let bucketId = '';
  try {
    const payload = JSON.parse(req.payload);
    bucketId = payload.bucketId;
  } catch(_err) {
    return res.json({
      success: false,
      message: "Payload is invalid.",
    });
  }
  const client = new sdk.Client();
  const storage = new sdk.Storage(client);
  
  client
    .setEndpoint(req.variables['APPWRITE_FUNCTION_ENDPOINT'])
    .setProject(req.variables['APPWRITE_FUNCTION_PROJECT_ID'])
    .setKey(req.variables['APPWRITE_FUNCTION_API_KEY'])
  ;

  try {
    const limit = 25;
    let offset = 0;
    let listFiles;
    do {
      listFiles = await storage.listFiles(bucketId, [sdk.Query.limit(limit), sdk.Query.offset(offset)]);
      for(const file of listFiles.files) {
        await storage.deleteFile(bucketId, file.$id);
      }
      offset += limit;
    } while(offset < listFiles.total);
    return res.json({
      success: true,
    });
  } catch(_err) {
    return res.json({
      success: false,
      message: "Bucket not found.",
    });
  }
}