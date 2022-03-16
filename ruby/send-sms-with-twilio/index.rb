require "twilio-ruby"
require "json"

def main(req, res) 
    # Input validation
    message = nil
    receiver = nil
    begin
        payload = JSON.parse(req.payload)
        message = payload["message"]
        receiver = payload["receiver"]
    rescue Exception => err
        puts err
        raise 'Payload is invalid.'
    end

    if message.nil? or message.empty? or receiver.nil? or receiver.empty?
        raise 'Payload is invalid.'
    end

    # Make sure we have envirnment variables required to execute
    if !req.env['TWILIO_ACCOUNT_SID'] or !req.env['TWILIO_AUTH_TOKEN'] or !req.env['TWILIO_SENDER']
        raise 'Please provide all required environment variables.'
    end

    # Parse the environment variables to get account keys and the sender's mobile number.
    account_sid = req.env["TWILIO_ACCOUNT_SID"]
    auth_token = req.env["TWILIO_AUTH_TOKEN"]
    sender = req.env["TWILIO_SENDER"]

    # Create the Twilio client.
    client = Twilio::REST::Client.new(account_sid, auth_token)

    # Send the message.
    message = client.messages.create(
        body: message,
        from: sender,
        to: receiver
    )

    # Return a response
    res.json({
        messageId: message.sid,
    })
end