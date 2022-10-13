import Foundation
import AsyncHTTPClient

class Network {
    func shortenUrl(provider: Provider, url: String) async throws -> ApiResponse {
        var apiResponse: ApiResponse = ApiResponse(link: "", status: .SUCCESS, message: "Entered URL is not a valid URL.")

        let body: String = provider.type == .Bitly ? "{\"long_url\": \"\(url)\"}" : provider.type == .TinyUrl ? "{\"url\": \"\(url)\"}" : "{}"
        let bytes = body.utf8
        let buffer = [UInt8](bytes)

        let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)

        var request = HTTPClientRequest(url: provider.endpoint)

        request.method = .POST
        request.headers.add(name: "Content-Type", value: "application/json")
        request.headers.add(name: "Authorization", value: "Bearer \(provider.token)")
        request.body = .bytes(buffer)

        do {
            let response = try await httpClient.execute(request, timeout: .seconds(30))

            let data = try await response.body.collect(upTo: 1024*1024)

            if (response.status == .ok) {
                if provider.type == .Bitly {
                    let shortenedLink: ApiDataBitly = try JSONDecoder().decode(ApiDataBitly.self, from: data)

                    apiResponse.link = shortenedLink.link
                    apiResponse.status = .SUCCESS
                } else if provider.type == .TinyUrl {
                    let shortenedLink: ApiDataTinyUrl = try JSONDecoder().decode(ApiDataTinyUrl.self, from: data)

                    apiResponse.link = shortenedLink.data.tiny_url
                    apiResponse.status = .SUCCESS
                }
            } else {
                throw ApiError.runtimeError("Error")
            }
        } catch {
            apiResponse.status = .ERROR
        }

        return apiResponse
    }
}
