require 'net/http'
require 'addressable/uri'
require 'json'
require 'base64'
require 'open-uri'

def main(request, response)
    website_url = nil
    begin
        payload = JSON.parse(request.payload)
        website_url = payload["url"]
    rescue Exception => e
        return response.json({"message":"Payload is invalid."})
    end

    begin
        uri = Addressable::URI.parse("https://shot.screenshotapi.net/screenshot")
        api_key = request.variables['SCREENSHOT_API_KEY']
        uri.query_values = {
          'token'  => api_key,
          'url' => website_url,
          'output' => 'JSON',
          'full_page': true,
        }
        uri = URI(uri)

        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        req =  Net::HTTP::Get.new(uri)
        res = http.request(req)

        screenshot_url = JSON.parse(res.body)['screenshot']
        image =  URI.open(screenshot_url)
        result = Base64.strict_encode64(image.read)

        return response.json({"success":true,"screenshot": result})
    rescue StandardError => e
        return response.json({"success":false,"message":e})
    end
    
end