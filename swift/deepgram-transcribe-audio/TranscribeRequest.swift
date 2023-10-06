import Foundation

struct Payload: Codable{
    var fileUrl: URL
}

struct Variables: Codable{
    var DEEPGRAM_API_KEY: String
}
struct TranscribeRequest: Codable {
    var payload: Payload
    var variables: Variables
}

enum TranscribeRequestError: Error{
    case emptyApiKey
    case dataCorrupted(message:String)
    case keyNotFound(key: CodingKey)
    case invalidRequest
}