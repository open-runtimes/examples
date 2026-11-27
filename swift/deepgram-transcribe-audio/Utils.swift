import AsyncHTTPClient
import Foundation

func DictToString(json: [String: Any]) -> String {
  String(decoding: try! JSONSerialization.data(withJSONObject: json, options: []), as: UTF8.self)
}
extension String {
  func toData() -> Data {
    self.data(using: .utf8)!
  }
  func decodeJson() -> [String: Any] {
    self.toData().decodeJson()
  }
  func decodeTranscribeRequest() throws -> TranscribeRequest {
    let decoder = JSONDecoder()
    do {
      return try decoder.decode(TranscribeRequest.self, from: self.toData())
    } catch let DecodingError.dataCorrupted(ctx) {
      throw TranscribeRequestError.dataCorrupted(message: ctx.debugDescription)
    } catch let DecodingError.keyNotFound(key, _) {
      throw TranscribeRequestError.keyNotFound(key: key)
    } catch {
      throw TranscribeRequestError.invalidRequest
    }

  }
}
extension Data {
  func decodeJson() -> [String: Any] {
    return try! JSONSerialization.jsonObject(with: self, options: []) as! [String: Any]
  }
}
func TranscribeAudio(url: URL, apiKey: String) async -> [String: Any] {
  let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
  defer {
    // Shutdown is guaranteed to work if it's done precisely once (which is the case here).
    try! httpClient.syncShutdown()
  }
  var request = HTTPClientRequest(url: deepgramURL)
  request.method = .POST
  request.headers.add(name: "Authorization", value: "Token \(apiKey)")
  request.headers.add(name: "Content-Type", value: "application/json")
  request.body = .bytes([UInt8](DictToString(json: ["url": url.absoluteString]).utf8))
  var response: [String: Any] = [:]
  do {
    let DeepgramResponse = try await httpClient.execute(request, timeout: .seconds(30))
    var DeepgramResponseBody = try await DeepgramResponse.body.collect(upTo: 1024 * 1024) // 1MB
    // upTo is just to allocate memory limit to request to avoid DDOS nothing related to length of body in general
    // ref: swift-server/async-http-client/issues/363
    let TranscribedJson = DeepgramResponseBody.readData(length: DeepgramResponseBody.readableBytes)!
      .decodeJson()
    let statusCode = DeepgramResponse.status.code
    if statusCode == 200 {
      response = ["success": true, "deepgramData": TranscribedJson]
    } else if statusCode == 401 {
      response = ["success": false, "message": "Please provide a valid DEEPGRAM_API_KEY"]
    } else if statusCode == 400 {
      response = ["success": false, "message": "Please provide a valid audio URL"]
    } else {
      response = [
        "success": false,
        "message": "\(TranscribedJson["error"] ?? "encounter error from deepgram"), \(TranscribedJson["reason"] ?? "")",
      ]
    }
  } catch {
    response = ["success": false, "message": error.localizedDescription]
    print(error)
  }

  return response
}
