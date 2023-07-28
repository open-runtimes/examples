import AsyncHTTPClient
import Foundation
import NIO 
import NIOFoundationCompat 

class SMSMessenger: Messenger{
    private let authToken: String
    private let accountSid: String
    private let twilioNumber: String
    private let httpClient: HTTPClient

    init(_ env_vars: [String: String], httpClient: HTTPClient) throws{
        guard let accountSid = env_vars["TWILIO_ACCOUNT_SID"],
            let authToken = env_vars["TWILIO_AUTH_TOKEN"],
            let twilioNumber =  env_vars["TWILIO_SENDER"] else {
            throw MessengerError.misconfigurationError(error: "Missing environment variables.")
        }
        self.authToken = authToken
        self.accountSid = accountSid
        self.twilioNumber = twilioNumber
        self.httpClient = httpClient

    }
    /* 
    Creates an HTTPClientRequest to send an SMS message via Twilio API.
    */
    private func createRequest(message: Message) async throws-> HTTPClientRequest { 
        let targetURL: String = "https://api.twilio.com/2010-04-01/Accounts/\(accountSid)/Messages" 
        let credentials: String = "\(accountSid):\(authToken)"
        var request = HTTPClientRequest(url: targetURL)
        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/x-www-form-urlencoded")
        request.headers.add(name:"Authorization", value: "Basic \(Data(credentials.utf8).base64EncodedString())") // credentials
        let bodyString: String = "From=\(twilioNumber)&Body=\(message.content)&To=\(message.recipient)"
        let bodyData = Data(bodyString.utf8)
        request.body = .bytes(ByteBuffer(bytes: bodyData))
        return request 
    }
    /* 
    Sends an SMS message using the Twilio API and handles potential errors during the process.
    */
    public func sendMessage(messageRequest: Message) async -> Error?{
        
        do{
            let request =  try await createRequest(message: messageRequest)
            let response: HTTPClientResponse = try await httpClient.execute(request, timeout: .seconds(30))
            //messege created successfully 
            switch response.status.code{
            case 201: 
                return nil
            case 400:
                return MessengerError.misconfigurationError(error: "Bad request in request construction")
            case 401:
                return MessengerError.validationError(error: "Unauthorized credentials, Ensure credentials are correct")
            case 404:
                return MessengerError.providerError(error: "Message couldnt be found")
            case 500,503,429: 
                return MessengerError.providerError(error: "Connection timed out, try again later")
            default: 
                return MessengerError.providerError(error: "Error: \(response.status)")
            }
        }
        catch{
            return MessengerError.providerError(error: "Connection error: \(error)")
        }
    }
}
