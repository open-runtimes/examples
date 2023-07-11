//an enum of the implementations we provide, anything else is rejected
enum MessageType {
    case sms(String)
    case email(String)
    case twitter(String)
    case discord(String)
}

//error types which we want to define, handy for making sure all implementations provide the same errors
enum MessengerError: Error {
    case validationError(error: String) //the message request
    case providerError(error: String) //what if things appear in good order to us but discord is down? This error type signifies that there is a downstream error.
    case misconfigurationError(error: String)// something is wrong with config like the API token for discord being wrong etc
}

//this is our interface for users to interact with us, input to this example function must conform to this 
struct Message {
    var type MessageType
    var recipient String
    var content String
}

protocol Messenger {
    func sendMessage(messageRequest: Message) -> Error? //make sure this is synchronous and you handle error handling
}

func main(messageRequest: Message) {
}