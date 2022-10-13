import Foundation

struct Provider {
    let endpoint: String
    let token: String
    let type: ProviderType

    init(type: ProviderType, token: String) {
        self.token = token
        self.type = type

        if self.type == .Bitly {
          self.endpoint = "https://api-ssl.bitly.com/v4/shorten"
        } else if self.type == .TinyUrl {
          self.endpoint = "https://api.tinyurl.com/create"
        } else {
          self.endpoint = "invalid"
        }
    }
}
