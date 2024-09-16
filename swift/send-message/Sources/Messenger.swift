import Foundation
import AsyncHTTPClient
//an enum of the implementations we provide, anything else is rejected
enum MessageType: String {
    case sms = "sms"
    case email = "email"
    case twitter = "twitter"
    case discord = "discord"
    case none
}

//error types which we want to define, handy for making sure all implementations provide the same errors
enum MessengerError: Error {
    case validationError(error: String) //the message request
    case providerError(error: String) //what if things appear in good order to us but discord is down? This error type signifies that there is a downstream error.
    case misconfigurationError(error: String)// something is wrong with config like the API token for discord being wrong etc
}

//this is our interface for users to interact with us, input to this example function must conform to this 
struct Message {
    var type: MessageType
    var recipient: String
    var content: String
    var subject: String?
}

protocol Messenger {
     //make sure this is synchronous and you handle error handling
    func sendMessage(messageRequest: Message) async -> Error?
}

func main(req: RequestValue, res: RequestResponse) async throws -> RequestResponse {

    guard !req.payload.isEmpty,
        let data = req.payload.data(using: .utf8),
        let payload: [String: Any?] = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
        let typeString = payload["type"] as? String,      
        let recipient = payload["recipient"] as? String,
        let content = payload["content"] as? String else {      
        return res.json(data: ["success": false, "message" :"Misconfigurtion Error: Invalid payload."])
    }

    let type:MessageType = MessageType(rawValue: typeString) ?? .none
    let messenger: Messenger
    let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)

    //initialize messenger with environment variables and httpClient
    do{
        switch type{
            case .sms:
                messenger = try SMSMessenger(req.variables, httpClient: httpClient)
            case .email:
                messenger = try EmailMessenger(req.variables, httpClient: httpClient)
            case .twitter:
                messenger = try TwitterMessenger(req.variables, httpClient: httpClient)
            case .discord:
                messenger = try DiscordMessenger(req.variables, httpClient: httpClient)
            default: 
                return res.json(data: ["success": false, "message" :"Misconfigurtion Error: Invalid Type"])
        }
    } catch{
        return res.json(data: ["success": false, "message" :"Misconfigurtion Error: Missing environment variables."])
    }

    let subject = payload["subject"] as? String 
    let messageRequest = Message(type: type, recipient: recipient, content: content, subject: subject)

    let result = await messenger.sendMessage(messageRequest: messageRequest)
    try await httpClient.shutdown()
    
    if result == nil{
        return res.json(data: ["success": true])
    } else {
        let messengerError = result as? MessengerError
        switch messengerError{
            case let .validationError(error):
                return res.json(data: ["success": false, "message" :"Validation Error: \(error)"])
            case let .providerError(error):
                return res.json(data: ["success": false, "message" :"Provider Error: \(error)"])
            case let .misconfigurationError(error):
                return res.json(data: ["success": false, "message" :"Misconfigurtion Error: \(error)"])
            default:
                return res.json(data: ["success": false, "message" :"Unknown Error"])
        }
    }
}