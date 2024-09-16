import AsyncHTTPClient 
import Foundation 
import NIO 
import NIOFoundationCompat

let createDMURL = "https://discord.com/api/v10/users/@me/channels"

class DiscordMessenger: Messenger{
    private let discordBotToken: String
    private let discordGuildID: String
    private let httpClient: HTTPClient

    init(_ env_vars: [String: String], httpClient: HTTPClient) throws {
        guard let discordBotToken = env_vars["DISCORD_BOT_TOKEN"],
            let discordGuildID = env_vars["DISCORD_GUILD_ID"] else {
            throw MessengerError.misconfigurationError(error: "Missing environment variables.")
        }
        self.discordBotToken = discordBotToken
        self.discordGuildID = discordGuildID
        self.httpClient = httpClient
    }

    private func getIDFromUsername(json: [[String:Any]], username: String) throws -> String{
        for user  in json{
            let currentUsername = ((user["user"]) as! [String: Any])["username"] as! String
            
            if currentUsername == username{
                return ((user["user"]) as! [String: Any])["id"] as! String
            }      
        }
        throw MessengerError.misconfigurationError(error: "No users with username \(username) found in server")
    }

    //Returns a recipient id given their username
    private func getRecipientID(username: String) async throws -> String{
        let targetURL = "https://discord.com/api/v10/guilds/\(discordGuildID)/members/search?query=\(username)&limit=1000"
        var request = HTTPClientRequest(url: targetURL)

        request.method = .GET
        request.headers.add(name: "Authorization", value: discordBotToken) 
        
        let response:HTTPClientResponse
        do{
            response = try await httpClient.execute(request, timeout: .seconds(30))
        }catch{
            throw MessengerError.providerError(error: "Request url not responding or connection timed out")
        }

        let responseBody: ByteBuffer = try await response.body.collect(upTo: 1024 * 1024) // 1 MB
        let responseBodyJson = try JSONSerialization.jsonObject(with: responseBody.getData(at: 0, length: responseBody.readableBytes)!, options: [])             

        //If no user is returned we still get a status 200. searchUsersByUsername() will throw is misconfiguration error if there are no users that match the username
        if response.status != .ok {
            throw MessengerError.validationError(error: "Unable to get recipient id, API Status Code: \(response.status), API Error Message: \((responseBodyJson as! [String: Any])["message"] ?? "none")")
        }

        return try getIDFromUsername(json: responseBodyJson as! Array<[String: Any]>, username: username)
    }

    //Returns dm channel ID. If there isn't already a dm channel with a user one is made.
    private func createDM(recipient_id: String) async throws -> String{
        var request = HTTPClientRequest(url: createDMURL)
        let jsonRecipientID: [String: String] = ["recipient_id": "\(recipient_id)"]

        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/json")
        request.headers.add(name: "Authorization", value: discordBotToken)
        request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonRecipientID))))
        
        let response:HTTPClientResponse
        do{
            response = try await httpClient.execute(request, timeout: .seconds(30))
        }catch{
            throw MessengerError.providerError(error: "Request url not responding or connection timed out")
        }

        let responseBody: ByteBuffer = try await response.body.collect(upTo: 1024 * 1024) // 1 MB
        let responseBodyJson = try JSONSerialization.jsonObject(with: responseBody.getData(at: 0, length: responseBody.readableBytes)!, options: []) as! [String: Any]

        if response.status != .ok {
            throw MessengerError.validationError(error: "Unable to create dm, API Status Code: \(response.status), API Error Message: \(responseBodyJson["message"] ?? "none")")
        }

        return responseBodyJson["id"] as! String   
    }
    
    //Sends a message given a Message with members recipient and message. 
    //Returns an Error type if there is an error, otherwise returns nil
    public func sendMessage(messageRequest: Message) async -> Error?{
        let dmChannelID:String
        do{
            dmChannelID = try await createDM(recipient_id: getRecipientID(username: messageRequest.recipient))
        }catch{
            return error
        }

        let targetURL = "https://discord.com/api/v10/channels/\(dmChannelID)/messages"
        var request = HTTPClientRequest(url: targetURL)
        let jsonMessage: [String: String] = ["content": "\(messageRequest.content)"]

        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/json")
        request.headers.add(name: "Authorization", value: discordBotToken)

        do{
            request.body = .bytes(ByteBuffer(data: (try JSONSerialization.data(withJSONObject: jsonMessage))))
            let response = try await httpClient.execute(request, timeout: .seconds(30))
            if response.status == .ok {
                //if everything appears to have worked, return nil
                return nil
            } 
            let responseBody: ByteBuffer = try await response.body.collect(upTo: 1024 * 1024) // 1 MB
            let responseBodyJson = try JSONSerialization.jsonObject(with: responseBody.getData(at: 0, length: responseBody.readableBytes)!, options: []) as! [String: Any]
            return MessengerError.validationError(error: "Unable to send dm, API Status Code: \(response.status), API Error Message: \(responseBodyJson["message"] ?? "none")")
        }catch{
            return MessengerError.providerError(error: "Request url not responding or connection timed out")
        }
        
    }

}