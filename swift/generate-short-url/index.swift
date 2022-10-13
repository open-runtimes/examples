import AsyncHTTPClient
import Foundation

func main(req: RequestValue, res: RequestResponse) async throws -> RequestResponse {
    var status: Bool = false
    var isBitly: Bool = false
    var isTinyUrl: Bool = false

    var type: String = ""
    var url: String = ""
    var tokenKey: String = ""
    var token: String = ""
    var result: [String : Any?]

    var providerType: ProviderType = .Invalid
    let network = Network()

    result = [
        "status": status,
        "message": "Missing payload",
    ]

    if !req.payload.isEmpty,
        let data = req.payload.data(using: .utf8),
        let payload = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
            type = payload["provider"] as? String ?? ""
            url = payload["url"] as? String ?? ""
    } else {
        return res.json(data: result)
    }

    if type.isEmpty {
        result = [
            "status": status,
            "message": "Empty provider type",
        ]
        return res.json(data: result)
    }

    if url.isEmpty {
        result = [
            "status": status,
            "message": "Empty url to be shorten",
        ]
        return res.json(data: result)
    }

    isBitly = type.lowercased().elementsEqual("bitly")
    isTinyUrl = type.lowercased().elementsEqual("tinyurl")

    providerType = isBitly ? .Bitly : isTinyUrl ? .TinyUrl : .Invalid
    tokenKey = isBitly ? TokenType.Bitly.rawValue : isTinyUrl ? TokenType.TinyUrl.rawValue : ""
    token = req.env[tokenKey] ?? ""

    if providerType == .Invalid {
        result = [
            "status": status,
            "message": "Invalid provider type, supported types are 'bitly' and 'tinyurl'",
        ]
        return res.json(data: result)
    }

    if token.isEmpty {
        result = [
            "status": status,
            "message": "Missing token, token named \(tokenKey) can't be empty",
        ]
        return res.json(data: result)
    }

    do {
        let provider = Provider(type: providerType, token: token)

        let response = try await network.shortenUrl(provider: provider, url: url)

        status = response.status == .SUCCESS

        if status {
            result = [
                "status": status,
                "url": response.link,
            ]
        } else {
            result = [
                "status": status,
                "message": response.message,
            ]
        }

        return res.json(data: result)
    } catch {
        result = [
            "status": status,
            "message": "An unexpected network error came in generating url",
        ]
        return res.json(data: result)
    }
}
