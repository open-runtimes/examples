require "uri"
require "net/http"
require "openssl"
require "json"


def main(req, res)
  fileUrl = req.payload["fileUrl"]
  deepgramApiKey = req.variables["DEEPGRAM_API_KEY"]

  url = URI("https://api.deepgram.com/v1/listen?model=video")
  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = true

  request = Net::HTTP::Post.new(url)
  request["content-type"] = "application/json"
  request["Authorization"] = "Token #{deepgramApiKey}"
  request.body = JSON.generate({url: fileUrl})

  begin
    response = http.request(request)
  rescue Exception => err
    return res.json({
      success: false,
      message: err
    })
  end

 response_body = response.read_body
 if response.code=="200"
  parsed_response = JSON.parse(response_body)
  res.json({
    success: true,
    deepgramData: parsed_response["results"]
  })
 elsif valid_json?(response_body)
  res.json({
    success: false,
    message: JSON.parse(response_body)
  })
 else
  res.json({
    success: false,
    message: response_body
  })
 end
end

def valid_json?(json)
  JSON.parse(json)
  true
rescue JSON::ParserError, TypeError => e
  false
end