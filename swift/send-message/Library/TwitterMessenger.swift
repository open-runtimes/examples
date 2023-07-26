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

    init(_ env_vars: [String: String]) throws {
    guard let oauth_consumer_key = env_vars["TWITTER_API_KEY"],
        let oauth_consumer_secret = env_vars["TWITTER_API_KEY_SECRET"],
        let oauth_token = env_vars["TWITTER_ACCESS_TOKEN"],
        let oauth_token_secret = env_vars["TWITTER_ACCESS_TOKEN_SECRET"] else {
        throw MessengerError.misconfigurationError(error: "Missing environment variables.")
    }
    self.oauth_consumer_key = oauth_consumer_key
    self.oauth_consumer_secret = oauth_consumer_secret
    self.oauth_token = oauth_token
    self.oauth_token_secret = oauth_token_secret
    httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    }

    func sendMessage(messageRequest:Message) async -> Error? {        
        /*
            - If recipient is specified, recipient is tagged and tweet becomes a reply
            - If no recipient specified, tweet is standard tweet
        */
        var tweetText:String
        if messageRequest.recipient != "" {
            tweetText = "@" + messageRequest.recipient + " " + messageRequest.content
        } else {
            tweetText = messageRequest.content
        }
        let jsonText : [String:String] = ["text":"\(tweetText)"]
        
        let requestURL = "https://api.twitter.com/2/tweets"
        var request = HTTPClientRequest(url: requestURL)
        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/json")
        request.headers.add(name: "Authorization", value: createAuthHeader())

        do {
            request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonText))))
            let response = try await httpClient.execute(request, timeout: .seconds(30))
            try await httpClient.shutdown()
            
            if response.status.code != 201 {
                var errorMessage:String = ""
                do {
                    let bodyResponse: ByteBuffer = try await response.body.collect(upTo: 1024*1024)
                    let jsonResponse = try JSONSerialization.jsonObject(with: bodyResponse.getData(at:0, length: bodyResponse.readableBytes)!, options: [])      
                    if let jsonResponseDict = jsonResponse as? [String: Any],
                    let detail = jsonResponseDict["detail"] {
                        errorMessage = detail as! String
                    }
                    return MessengerError.validationError(error: "Unable to post tweet, API Status Code: \(response.status), API Error Message: \(errorMessage)")
                } catch {
                    return error
                }   
            }
        } catch {
            return MessengerError.providerError(error: "Request did not recieve a response or  connection timeout")
        }
        return nil //Returns no error if tweet was created
    }

    private func createAuthHeader() -> String {
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