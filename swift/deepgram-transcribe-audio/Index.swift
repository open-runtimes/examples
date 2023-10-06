import Foundation

func main(context: RuntimeContext) async throws -> RuntimeOutput {
  var transcribeRequest: TranscribeRequest
  var response: [String: Any] = ["success": false]
  do {
    transcribeRequest = try context.req.bodyRaw.decodeTranscribeRequest()
    guard !transcribeRequest.variables.DEEPGRAM_API_KEY.isEmpty else {
      throw TranscribeRequestError.emptyApiKey
    }
    let transcribedAudioResponse = await TranscribeAudio(
      url: transcribeRequest.payload.fileUrl, apiKey: transcribeRequest.variables.DEEPGRAM_API_KEY)

    response = transcribedAudioResponse
  } catch let TranscribeRequestError.dataCorrupted(message) {
    response["error"] = message
  } catch let TranscribeRequestError.keyNotFound(key: key) {
    response["error"] =
      "Key \(key.stringValue) not found in the request body a proper request body for this function looks something like \(exampleRequest)"
  } catch TranscribeRequestError.emptyApiKey {
    response["error"] = "Please provide valid apikey"
  } catch {
    print(error.localizedDescription)
    response["error"] =
      "Please provide valid Request body. A proper request body for this function looks something like \(exampleRequest)"
  }
  return try context.res.json(response)
}
