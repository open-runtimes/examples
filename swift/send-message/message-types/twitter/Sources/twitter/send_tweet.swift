import Foundation
import AsyncHTTPClient
import NIO
import NIOFoundationCompat
import OhhAuth

class Tweeter : Messenger{
    var oauth_consumer_key:String
    var oauth_consumer_secret:String
    var oauth_token:String
    var oauth_token_secret:String
    var httpClient: HTTPClient

    init(oauth_consumer_key:String, oauth_consumer_secret:String, oauth_token:String, oauth_token_secret:String) {
        self.oauth_consumer_key = oauth_consumer_key
        self.oauth_consumer_secret = oauth_consumer_secret
        self.oauth_token = oauth_token
        self.oauth_token_secret = oauth_token_secret
        self.httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    }

    func sendMessage(messageRequest:Message) async -> Error? {         
        var tweetText:String
        if messageRequest.recipient != "" {
            tweetText = "@" + messageRequest.recipient + " " + messageRequest.content
        } else {
            tweetText = messageRequest.content
        }
        let jsonText : [String:String] = ["text":"\(tweetText)"]

        var request = HTTPClientRequest(url: "https://api.twitter.com/2/tweets?")
        request.method = .POST
        request.headers.add(name: "Authorization", value: createAuthorizationHeader(text:tweetText))
        let response:HTTPClientResponse

        do {
            request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonText))))
            response = try await httpClient.execute(request, timeout: .seconds(30))
            print(response)
            try await httpClient.shutdown()
        } catch {
            return MessengerError.providerError(error: "Request did not recieve a response or  connection timeout")
        }
        
        if response.status != .ok {
            return MessengerError.validationError(error: "Unable to post tweet, API Status Code: \(response.status)")
        }
        return nil //Returns no error if tweet was created
    } 

    private func createAuthorizationHeader(text:String) -> String {
        let cc = (key: self.oauth_consumer_key, secret: self.oauth_consumer_secret)
        let uc = (key: self.oauth_token, secret: self.oauth_token_secret)
        let paras = ["text": text]
        let requestURL = URL(string: "https://api.twitter.com/2/tweets?")!

        let oauth_signature = OhhAuth.calculateSignature(url: requestURL, method: "HMAC-SHA1", parameter: paras, consumerCredentials: cc, userCredentials: uc)
        return oauth_signature
    }
}

    //public func createHeader() -> String {
    //     var header:[String, String] = "OAuth oauth_consumer_key":\(oauth_consumer_key) ,
    //                         oauth_token=\(oauth_token) , 
    //                         oauth_signature_method=\(oauth_signature_method) ,
    //                         oauth_timestamp=\(oauth_timestamp) ,
    //                         oauth_nonce = \(oauth_nonce) ,
    //                         oauth_version = \(oauth_version) ,
    //                         oauth_signature = \()"
        
    //     var testString:String = "this is a test \"string inside a string\" "
    //     return header
    // }










