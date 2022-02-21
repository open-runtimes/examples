require "twilio-ruby"
require "json"

def main(request, response) 

    # If there is no payload or environment, return an error.
    if request.payload.nil? || request.payload.empty? || request.env.nil? || request.env.empty?
        return response.json({
            code: 400,
            message: "Payload or environment variables missing."
        })
    end

    # Parse the payload to get the message to send and the receiver's mobile number.
    payload = JSON.parse(request.payload)
    message = payload["message"]
    receiver = payload["receiver"]

    # Parse the environment variables to get account keys and the sender's mobile number.
    account_sid = request.env["TWILIO_ACCOUNT_SID"]
    auth_token = request.env["TWILIO_AUTH_TOKEN"]
    sender = request.env["TWILIO_SENDER"]

    # If there is no message or receiver, return an error.
    if message.nil? || receiver.nil? || receiver.empty? || auth_token.nil? || auth_token.empty?
        return response.json({ 
            code: 400,
            message: "Payload or environment variables missing."
        })
    end

    # Create the Twilio client.
    client = Twilio::REST::Client.new(account_sid, auth_token)

    # Send the message.
    message = client.messages.create(
        body: message,
        from: sender,
        to: receiver
    )

    response.json({
        messageId: message.sid,
    })
end