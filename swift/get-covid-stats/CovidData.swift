struct CovidData : Codable {
    let global: Global
    let countries: [Country]

    enum CodingKeys : String, CodingKey {
        case global = "Global"
        case countries = "Countries"
    }
}

struct Global : Codable {
    let newConfirmed: Int
    let newDeaths: Int
    let newRecovered: Int

    enum CodingKeys: String, CodingKey {
        case newConfirmed = "NewConfirmed"
        case newDeaths = "NewDeaths"
        case newRecovered = "NewRecovered"
    }
}

struct Country : Codable {
    let country: String
    let countryCode: String
    let slug: String
    let newConfirmed: Int
    let newDeaths: Int
    let newRecovered: Int

    enum CodingKeys: String, CodingKey {
        case country = "Country"
        case countryCode = "CountryCode"
        case slug = "Slug"
        case newConfirmed = "NewConfirmed"
        case newDeaths = "NewDeaths"
        case newRecovered = "NewRecovered"
    }
}
