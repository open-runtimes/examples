import Foundation
import AsyncHTTPClient
import NIO
import NIOFoundationCompat

class Tweeter : Messenger{
    var oauth_consumer_key:String
    var oauth_token:String
    var httpClient: HTTPClient

    init(oauth_consumer_key:String, oauth_token:String) {
        self.oauth_consumer_key = oauth_consumer_key
        self.oauth_token = oauth_token
        self.httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    }

    func sendMessage(messageRequest:Message) async -> Error? {         
        var tweet_text:String
        if messageRequest.recipient != "" {
            tweet_text = "@" + messageRequest.recipient + " " + messageRequest.content
        } else {
            tweet_text = messageRequest.content
        }

        var request = HTTPClientRequest(url: "https://api.twitter.com/2/tweets?")
        request.method = .POST
        request.headers.add(name: "Authorization", value: "no current value")
        let jsonText : [String:String] = ["text":"\(tweet_text)"]
        
        let response:HTTPClientResponse
        do {
            request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonText))))
            response = try await httpClient.execute(request, timeout: .seconds(30))
            try await httpClient.shutdown()
        } catch {
            return MessengerError.providerError(error: "Request did not recieve a response or  connection timeout")
        }
        
        
        if response.status != .ok {
            return MessengerError.validationError(error: "Unable to create dm, API Status Code: \(response.status)")
        }
        return nil //Returns no error if tweet was created
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










