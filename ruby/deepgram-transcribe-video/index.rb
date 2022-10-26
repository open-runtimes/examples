require "uri"
require "net/http"
require "openssl"
require "json"


def main(req, res)
  fileUrl = nil
  begin
    payload = JSON.parse(req.payload)
    fileUrl = payload["fileUrl"]
  rescue Exception => err
    res.json({
      success: false,
      message: "Payload is invalid."
    })
  end
  
  url = URI("https://api.deepgram.com/v1/listen?model=video")
  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = true
  http.verify_mode = OpenSSL::SSL::VERIFY_PEER

  request = Net::HTTP::Post.new(url)
  request["content-type"] = "application/json"
  request["Authorization"] = "Token #{req.env["DEEPGRAM_API_KEY"]}"
  request.body = JSON.generate({url: fileUrl})

  begin
    response = http.request(request)
  rescue Exception => err
    res.json({
      success: false,
      message: err
    })
  end

 if response.code=="200"
  parsed_response = JSON.parse(response.read_body)
  res.json({
    success: true,
    deepgramData: parsed_response["results"]
  })
 else
  res.json({
    success: false,
    message: response.body
  })
 end
end