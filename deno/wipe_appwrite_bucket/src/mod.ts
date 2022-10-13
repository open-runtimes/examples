import { sdk } from "./deps.ts";

export default async function (req: any, res: any) {
  if (
    !req.variables["APPWRITE_FUNCTION_ENDPOINT"] ||
    !req.variables["APPWRITE_FUNCTION_PROJECT_ID"] ||
    !req.variables["APPWRITE_FUNCTION_API_KEY"] ||
    !req.payload
  ) {
    return res.json({
      success: false,
      message: "Missing required environment variables or payload.",
    });
  }
  let bucketId = "";
  try {
    const payload = JSON.parse(req.payload);
    bucketId = payload.bucketId;
  } catch (_err) {
    return res.json({
      success: false,
      message: "Payload is invalid.",
    });
  }
  const client = new sdk.Client();
  const storage = new sdk.Storage(client);

  client
    .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"])
    .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"])
    .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"]);

  try {
    const limit = 1;
    const totalFiles = (await storage.listFiles(bucketId, [sdk.Query.limit(limit)])).total;
    for (let i = 0; i < totalFiles; i += limit) {
      const files = (await storage.listFiles(bucketId, [sdk.Query.limit(limit)])).files;
      for (const file of files) {
        await storage.deleteFile(bucketId, file.$id);
      }
    }
    return res.json({
      success: true,
    });
  } catch (_err) {
    return res.json({
      success: false,
      message: "Bucket not found.",
    });
  }
}
