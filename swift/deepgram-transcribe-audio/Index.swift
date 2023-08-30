import AsyncHTTPClient
import Foundation

func main(context: RuntimeContext) async throws -> RuntimeOutput{

    //Settingup necessary variables
    let bodyString = context.req.bodyRaw
    let bodyDict = try JSONSerialization.jsonObject(with: bodyString.data(using: .utf8)! ,options: []) as! [String:Any]
    let variables = bodyDict["variables"] as! [String:Any]
    let payload = bodyDict["payload"] as! [String:Any]
    let fileUrl = payload["fileUrl"] as! String
    let apikey = variables["DEEPGRAM_API_KEY"] as! String
    let jsonString = "{\"url\":\"\(fileUrl)\"}"

    let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)
    var request = HTTPClientRequest(url: "https://api.deepgram.com/v1/listen")
    request.method = .POST
    request.headers.add(name: "Authorization", value: "Token \(apikey)")
    request.headers.add(name: "Content-Type", value: "application/json")

    request.body = .bytes([UInt8](jsonString.utf8))
    
    let response = try await httpClient.execute(request, timeout: .seconds(30))
    var body = try await response.body.collect(upTo: 1024*1024) // 1MB 
    let string = body.readString(length: body.readableBytes)!
    let json = string.data(using: .utf8)!
    let jsonDict = try JSONSerialization.jsonObject(with: json,options: [])
    if(response.status.code == 200){
        return try context.res.json([
            "success":true,
            "deepgramData": jsonDict
        ])
    }else{
        return try context.res.json([
            "success":false,
            "error": jsonDict
        ])
    }
   
}