import Foundation
import AsyncHTTPClient
import NIO
import NIOFoundationCompat

class Tweeter {
    var oauth_consumer_key:String
    var oauth_token:String
    var httpClient: HTTPClient

    init(oauth_consumer_key:String, oauth_token:String) {
        self.oauth_consumer_key = oauth_consumer_key
        self.oauth_token = oauth_token
        self.httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    }

    public func sendMessage(receiver:String = "", message:String = "") async throws -> Error? {         
        var tweet_text:String
        if receiver != "" {
            tweet_text = "@" + receiver + " " + message
        } else {
            tweet_text = message
        }

        var request = HTTPClientRequest(url: "https://api.twitter.com/2/tweets?")
        let jsonText : [String:String] = ["text":"\(tweet_text)"]

        request.method = .POST
        request.headers.add(name: "Authorization", value: "no current value")
        request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonText))))
        let response = try await httpClient.execute(request, timeout: .seconds(30))
        try await httpClient.shutdown()
        
        if response.status != .ok {
            print("Tweet was unable to be created")
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










