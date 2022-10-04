require "httparty"
require "json"

def main(req, res)
    begin
        payload = JSON.parse(req.payload)
        url = payload["url"]
        method = payload["method"]
        headers = payload["headers"]
        body = payload["body"]
    rescue Exception => err
        puts err
        raise 'Payload is invalid.'
    end

    if url.nil? or url.empty? or method.nil? or method.empty?
        raise 'Payload is invalid.'
    end

    begin
        response = HTTParty.send(method.downcase, url, headers: headers, body: body)

        if response.success?
            res.json({
                "success": response.success?,
                "response": {
                    "headers": response.headers,
                    "code": response.code,
                    "body": response.body
                }
            })
        else
            res.json({
                "success": false,
                "message": response.message
            })
        end
    rescue Exception => err
        puts err
        raise 'Request could not be made.'
    end
end
