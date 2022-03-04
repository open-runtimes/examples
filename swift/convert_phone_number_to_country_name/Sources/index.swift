//
//  File.swift
//  
//
//  Created by Matej Bačo on 04/03/2022.
//

import Foundation
import Appwrite
import AppwriteModels

enum ExampleError : Error {
  case missing(String)
  case invalid(String)
}

/*
Globaly-scoped cache used by function. Example value:
[
    {
        "code": "+1",
        "countryCode": "US",
        "countryName": "United States"
    }
]
*/

struct PhonePrefix {
  let code: String
  let countryCode: String
  let countryName: String
}

var phonePrefixList:PhoneList?;



func main(req: RequestValue, res: RequestResponse) async throws -> RequestResponse {
  var phoneNumber = ""

  guard let payload = try JSONSerialization.jsonObject(with: req.payload.data(using: .utf8)!, options: []) as? [String:Any] else {
    throw ExampleError.invalid("Payload is invalid")
  }

  phoneNumber = payload["phoneNumber"] as? String ?? ""

  guard phoneNumber.hasPrefix("+") else {
    throw ExampleError.invalid("Invalid phone number")
  }

  guard let endpoint = String(utf8String: getenv("APPWRITE_FUNCTION_ENDPOINT")),
        let projectId = String(utf8String: getenv("APPWRITE_FUNCTION_PROJECT_ID")),
        let apiKey = String(utf8String: getenv("APPWRITE_FUNCTION_API_KEY")) else {
    throw ExampleError.missing("Please provide all required environment variables")
  }

  if(phonePrefixList == nil) {
    let group = DispatchGroup()

    let client = 
      Client()
      .setEndpoint(endpoint)
      .setProject(projectId)
      .setKey(apiKey)

    let locale = Locale(client)
    
    group.enter()
    locale.getCountriesPhones() { result in 
      defer { group.leave() }

      switch result {
        case .failure:
          break
        case .success(let list):
          phonePrefixList = list
      }
    }

    group.wait()
  }

  if(phonePrefixList == nil) {
    throw ExampleError.invalid("Unable to fetch phone prefix list")
  }

  guard let phonePrefix = phonePrefixList!.phones.first(where: { prefix -> Bool in 
    return phoneNumber.hasPrefix(prefix.code)
  }) else {
    throw ExampleError.invalid("Invalid phone number")
  }

  return res.json(data: [
    "phoneNumber": phoneNumber,
    "phonePrefix": phonePrefix.code,
    "countryCode": phonePrefix.countryCode,
    "countryName": phonePrefix.countryName,
  ])
}