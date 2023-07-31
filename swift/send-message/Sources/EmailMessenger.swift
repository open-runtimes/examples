import AsyncHTTPClient
import Foundation
import NIO
import NIOFoundationCompat

class EmailMessenger: Messenger{

    private let mailgunAPIKey: String
    private let mailgunDomain: String
    private let fromEmailAddress: String
    private let httpClient: HTTPClient

    init(_ env_vars: [String: String], httpClient: HTTPClient) throws{
        guard let mailgunAPIKey = env_vars["MAILGUN_API_KEY"],
            let mailgunDomain = env_vars["MAILGUN_DOMAIN"] else {
            throw MessengerError.misconfigurationError(error: "Missing environment variables.")
        }
        self.mailgunAPIKey = mailgunAPIKey
        self.mailgunDomain = mailgunDomain
        
        //sender of the message's email and optionally their name. Can be a non-existent email as long as it is formatted correctly. 
        //for example “Bob <bob@example.com>”  or "me@samples.mailgun.org" or "5555@5555.5555".
        fromEmailAddress = env_vars["MAILGUN_FROM_EMAIL_ADDRESS"] ?? "me@samples.mailgun.org"
        self.httpClient = httpClient
    }
    
    public func sendMessage(messageRequest: Message) async -> Error? {     
        let targetURL:String = "https://api.mailgun.net/v3/\(mailgunDomain)/messages"
        let auth:String = "api:\(mailgunAPIKey)".data(using: .utf8)!.base64EncodedString()
        var request = HTTPClientRequest(url: targetURL)
        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/x-www-form-urlencoded")
        request.headers.add(name: "Authorization", value: "Basic \(auth)")

        let bodyString: String = "from=\(fromEmailAddress)&to=\(messageRequest.recipient)&subject=\(messageRequest.subject ?? "")&text=\(messageRequest.content)"
        request.body = .bytes(ByteBuffer(bytes: Data(bodyString.utf8)))
        do{
            let response = try await httpClient.execute(request, timeout: .seconds(30))
            if response.status == .ok {       
                return nil
            } else {        
                let responseBody: ByteBuffer = try await response.body.collect(upTo: 1024 * 1024) // 1 MB
                //Some errors, normally ones with authorization, return strings and other errors return json. This do catch handles both cases
                do{
                    let responseBodyJson = try JSONSerialization.jsonObject(with: responseBody.getData(at: 0, length: responseBody.readableBytes)!, options: [])   
                    return MessengerError.validationError(error: "Unable to send email, API Status Code: \(response.status), API Error Message: \((responseBodyJson as! [String: Any])["message"] ?? "none")")
                }catch{
                    let responseBodyString = String(data: Data(buffer: responseBody), encoding: .utf8) ?? "Failed to convert data to UTF-8 string"
                    return MessengerError.validationError(error: "Unable to send email, API Status Code: \(response.status), API Error Message: \(responseBodyString)")
                }
            }
        }catch{
            return MessengerError.providerError(error: "Request url not responding or connection timed out")
        }    
    }
}