require "json"
require 'open-uri'
require "base64"
require "tinify"
require "kraken-io"

class NotImageException < StandardError; end
class ApiError < StandardError; end

invalidPayload = {
  success: false, message: "Invalid Payload"
}
notImage = {
  success: false, message: "Input file is not an image."
}
invalidApi = {
  success: false, message: "Invalid API."
}

def tinypng(req, image) 
    # Get api key
    if !req.env['TINYPNG_API']
      raise ApiError
    end

    Tinify.key = req.env['TINYPNG_API']
    buffer = Base64.decode64(image)
    result = Tinify.from_buffer(buffer).to_buffer
    
    Base64.encode64(result)
end

def krakenio(req, image) 
    if !req.env['KRAKENIO_KEY'] or !req.env['KRAKENIO_SECRET']
        raise ApiError
    end

    buffer = Base64.decode64(image)
    File.open('/file.png', 'wb') do |f|
      f.write(buffer)
    end

    kraken = Kraken::API.new(
        :api_key => req.env['KRAKENIO_KEY'],
        :api_secret => req.env['KRAKENIO_SECRET']
    )

    data = kraken.upload('/file.png')

    if data.success
        img = URI.open(data.kraked_url)
        Base64.encode64(img.read)
    else
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
        return res.json(invalidPayload)
    end

    if provider.nil? or provider.empty? or image.nil? or image.empty?
        return res.json(invalidPayload)
    end

    if provider == 'tinypng' 
        begin
            base64 = tinypng(req, image)
            return res.json({
                success: true,
                image: base64
            })
        rescue Tinify::ClientError
            return res.json(notImage)
        rescue Tinify::AccountError
            return res.json(invalidApi)    
        rescue ApiError
            return res.json(invalidApi)    
        end
    end

    if provider == 'krakenio' 
        begin
            base64 = krakenio(req, image)
            return res.json({
                success: true,
                image: base64
            })
        rescue NotImageException
            return res.json(notImage)
        rescue ApiError
            return res.json(invalidApi)    
        end
    end

    res.json({
        success: false, message: "Unsupported Providers."
    })
end
