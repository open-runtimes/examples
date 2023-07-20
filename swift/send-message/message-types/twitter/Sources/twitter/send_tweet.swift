import Foundation
import AsyncHTTPClient
import NIO
import NIOFoundationCompat
import Crypto

class TwitterMessenger : Messenger{
    private let oauth_consumer_key:String
    private let oauth_consumer_secret:String
    private let oauth_token:String
    private let oauth_token_secret:String
    private let httpClient: HTTPClient

    init() {
        self.oauth_consumer_key = ProcessInfo.processInfo.environment["TWITTER_API_KEY"]
        self.oauth_consumer_secret = ProcessInfo.processInfo.environment["TWITTER_API_KEY_SECRET"]
        self.oauth_token = ProcessInfo.processInfo.environment["TWITTER_ACCESS_TOKEN"]
        self.oauth_token_secret = ProcessInfo.environment["TWITTER_ACCESS_TOKEN_SECRET"]
        self.httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    }

    func sendMessage(messageRequest:Message) async -> Error? {        
        var tweetText:String
        if messageRequest.recipient != "" {
            tweetText = "@" + messageRequest.recipient + " " + messageRequest.content
        } else {
            tweetText = messageRequest.content
        }
        
        var request = HTTPClientRequest(url: "https://api.twitter.com/2/tweets")
        let jsonText : [String:String] = ["text":"\(tweetText)"]

        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/json")
        request.headers.add(name: "Authorization", value: createHeader())
        
        do {
            request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonText))))
            let response = try await httpClient.execute(request, timeout: .seconds(30))
            try await httpClient.shutdown()
        } catch {
            return MessengerError.providerError(error: "Request did not recieve a response or  connection timeout")
        }
        
        if response.headers["location"] == [] {
            return MessengerError.validationError(error: "Unable to post tweet, API Status Code: \(response.status)")
        }
        return nil //Returns no error if tweet was created
    } 

    private func createHeader() -> String {
        let nonce = (Data(ChaChaPoly.Nonce()).base64EncodedString()).filter{$0.isLetter}
        let timestamp = Int(Date().timeIntervalSince1970)
        let customAllowedSet =  NSCharacterSet(charactersIn:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~") as CharacterSet
        var baseString = "POST&https%3A%2F%2Fapi.twitter.com%2F2%2Ftweets&"
        let parameters = """
        oauth_consumer_key=\(oauth_consumer_key)&oauth_nonce=\(nonce)&oauth_signature_method=HMAC-SHA1&\
        oauth_timestamp=\(timestamp)&oauth_token=\(oauth_token)&oauth_version=1.0
        """
        baseString.append(contentsOf: parameters.addingPercentEncoding(withAllowedCharacters: customAllowedSet)!)
        let dataBaseString = baseString.data(using: .utf8)!
        let signingKey = (oauth_consumer_secret + "&" + oauth_token_secret).data(using: .utf8)!
        
        let hmacHash = HMAC<Insecure.SHA1>.authenticationCode(for: dataBaseString, using: SymmetricKey(data: signingKey))
        let signature = Data(hmacHash).base64EncodedString().addingPercentEncoding(withAllowedCharacters: customAllowedSet)!

        return """
        OAuth oauth_consumer_key=\"\(oauth_consumer_key)\",oauth_token=\"\(oauth_token)\",oauth_signature_method=\"HMAC-SHA1\",\
        oauth_timestamp=\"\(timestamp)\",oauth_nonce=\"\(nonce)\",\
        oauth_version=\"1.0\",oauth_signature=\"\(signature)\"
        """
    }
}