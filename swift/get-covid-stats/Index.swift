import AsyncHTTPClient
import Foundation
import NIOCore
import NIOFoundationCompat

func main(req: RequestValue, res: RequestResponse) async throws -> RequestResponse {
    let httpClient = HTTPClient(eventLoopGroupProvider: .createNew)

    guard !req.payload.isEmpty,
        let data = req.payload.data(using: .utf8),
        let payload: [String: Any?] = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
        let countryId = payload["country"] as? String else {
        return res.json(data: ["error": "Invalid payload."])
    }

    let request = HTTPClientRequest(url: "https://api.covid19api.com/summary")
    let response = try await httpClient.execute(request, timeout: .seconds(30))
    let body = try await response.body.collect(upTo: 1024*1024) // 1MB

    if (response.status != .ok) {
        return res.json(data: ["error": "Failed to fetch data: \(body)"])
    } 

    let covidData = try! JSONDecoder().decode(CovidData.self, from: body)

    let resultCountry = covidData.countries.filter {
        [
            $0.countryCode.lowercased(),
            $0.country.lowercased(),
            $0.slug.lowercased()
        ].contains(countryId.lowercased())
    }.first

    // Variables that will be shown in output
    let country = resultCountry?.country ?? "The World"
    let confirmedCasesToday = resultCountry?.newConfirmed ?? covidData.global.newConfirmed
    let deathsToday = resultCountry?.newDeaths ?? covidData.global.newDeaths
    let recoveredToday = resultCountry?.newRecovered ?? covidData.global.newRecovered

    return res.json(data: [
        "country": country,
        "confirmedCasesToday": confirmedCasesToday,
        "deathsToday": deathsToday,
        "recoveredToday": recoveredToday
    ])
}