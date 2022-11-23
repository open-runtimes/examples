require "json"
require "base64"
require "tinify"
require "kraken-io"

class NotImageException < StandardError; end
class ApiError < StandardError; end
class NoApiError < StandardError; end

$invalidPayload = {
  success: false, message: "Invalid Payload"
}
$notImage = {
  success: false, message: "Input file is not an image."
}
$invalidApi = {
  success: false, message: "Invalid API."
}
$noApi = {
  success: false, message: "no API key was provided."
}

def tinypng(req, image) 
    # Get api key
    if !req.variables['TINYPNG_API']
      raise NoApiError
    end

    Tinify.key = req.variables['TINYPNG_API']
    buffer = Base64.decode64(image)
    result = Tinify.from_buffer(buffer).to_buffer
    
    Base64.encode64(result)
end

def krakenio(req, image) 
    if !req.variables['KRAKENIO_KEY'] or !req.variables['KRAKENIO_SECRET']
        raise NoApiError
    end

    buffer = Base64.decode64(image)
    dir = Dir.mktmpdir("kraken_temp")

    File.open("#{dir}/file.png", 'wb') do |f|
      f.write(buffer)
    end

    kraken = Kraken::API.new(
        :api_key => req.variables['KRAKENIO_KEY'],
        :api_secret => req.variables['KRAKENIO_SECRET']
    )

    data = kraken.upload("#{dir}/file.png")

    File.delete("#{dir}/file.png") if File.exist?("#{dir}/file.png")

    if data.success
        img = URI.open(data.kraked_url)
        Base64.encode64(img.read)
    else
        if data.message.include? 'Unknown API Key'
          raise ApiError
        end
        raise NotImageException
    end
end

def main(req, res)
    # Input validation
    provider = nil
    image = nil
    begin
        payload = JSON.parse(req.payload)
        provider = payload["provider"]
        image = payload["image"]
    rescue Exception => err
        return res.json($invalidPayload)
    end

    if provider.nil? or provider.empty? or image.nil? or image.empty?
        return res.json($invalidPayload)
    end

    if provider == 'tinypng' 
        begin
            base64 = tinypng(req, image)
            return res.json({
                success: true,
                image: base64.gsub("\n",'')
            })
        rescue Tinify::ClientError
            return res.json($notImage)
        rescue Tinify::AccountError
            return res.json($invalidApi)    
        rescue NoApiError
            return res.json($noApi)    
        end
    end

    if provider == 'krakenio' 
        begin
            base64 = krakenio(req, image)
            return res.json({
                success: true,
                image: base64.gsub("\n",'')
            })
        rescue ApiError
            return res.json($invalidApi)
        rescue NotImageException
            return res.json($notImage)
        rescue NoApiError
            return res.json($noApi)    
        end
    end

    res.json({
        success: false, message: "Unsupported Providers."
    })
end