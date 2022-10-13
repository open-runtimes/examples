enum Status {
  case SUCCESS
  case ERROR
}

enum TokenType: String {
    case Bitly = "BITLY_API_TOKEN"
    case TinyUrl = "TINYURL_API_TOKEN"
}

enum ProviderType: String {
    case Bitly = "bitly"
    case TinyUrl = "tinyurl"
    case Invalid = "invalid"
}

struct ApiData: Decodable, Hashable {
    var tiny_url: String
}

struct ApiDataBitly: Decodable, Hashable {
    var link: String
}

struct ApiDataTinyUrl: Decodable, Hashable {
    var data: ApiData
}

struct ApiResponse {
    var link: String
    var status: Status
    var message: String
}

enum ApiError: Error {
    case runtimeError(String)
}
