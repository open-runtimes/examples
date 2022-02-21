import AsyncHTTPClient
import Foundation

func main(req: RequestValue, res: RequestResponse) async throws -> RequestResponse {

    guard !req.payload.isEmpty,
        let data = req.payload.data(using: .utf8),
        let payload: [String: Any?] = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
        let name = payload["name"] as? String,
        let email = payload["email"] as? String else {
        return res.json(data: ["error": "Invalid payload."])
    }

    guard let mailgunDomain = req.env["MAILGUN_DOMAIN"],
        let mailgunAPIKey = req.env["MAILGUN_API_KEY"] else {
        return res.json(data: ["error": "Missing environment variables."])
    }

    let message = "Welcome \(name)!"
    let targetURL = "https://api.mailgun.net/v3/\(mailgunDomain)/messages"
    let auth = "api:\(mailgunAPIKey)".data(using: .utf8)!.base64EncodedString()
    let params =  [
        "from" : "Excited User <hello@example.com>",
        "to" : email,
        "subject" : "Hello",
        "text" : message
    ]

    let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)

    do {
        var request = HTTPClientRequest(url: targetURL)
        request.method = .POST
        request.headers.add(name: "Content-Type", value: "multipart/form-data")
        request.headers.add(name: "Authorization", value: "Basic \(auth)")
        
        buildMultipart(&request, with: params)

        let response = try await httpClient.execute(request, timeout: .seconds(30))
        var body = try await response.body.collect(upTo: 1024*1024) //1MB
        let string = body.readString(length: body.readableBytes)
        if response.status == .ok {
            return res.json(data: [
                "code": 200,
                "message": string ?? "OK"
            ])
        } else {
            return res.json(data: [
                "code": 500,
                "message": string ?? "Unknown error"
            ])
        }
    } catch let error {
        return res.json(data: [
            "code": 500,
            "message": "Failed to send message: \(error)"
        ])
    }
}