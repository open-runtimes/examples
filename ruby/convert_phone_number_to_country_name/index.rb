require 'appwrite'
require 'json'

=begin
Globaly-scoped cache used by function. Example value:
[
    {
        "code": "+1",
        "countryCode": "US",
        "countryName": "United States"
    }
]
=end
$phone_prefix_list = nil

def main(req, res)
    # Input validation
    phone_number = nil
    begin
        payload = JSON.parse(req.payload)
        phone_number = (payload['phoneNumber'] || '').delete(' ')
    rescue Exception => err
        puts err
        raise 'Payload is invalid.'
    end

    if phone_number.nil? or phone_number.empty? or !phone_number.start_with?('+')
        raise 'Invalid phone number.'
    end

    # Make sure we have envirnment variables required to execute
    if !req.env['APPWRITE_FUNCTION_ENDPOINT'] or !req.env['APPWRITE_FUNCTION_PROJECT_ID'] or !req.env['APPWRITE_FUNCTION_API_KEY']
        raise 'Please provide all required environment variables.'
    end

    # If we don't have cached list of phone number prefixes (first execution only)
    if $phone_prefix_list.nil?
        # Init Appwrite SDK
        client = Appwrite::Client.new
        locale = Appwrite::Locale.new(client)

        client
            .set_endpoint(req.env['APPWRITE_FUNCTION_ENDPOINT'])
            .set_project(req.env['APPWRITE_FUNCTION_PROJECTID'])
            .set_key(req.env['APPWRITE_FUNCTION_API_KEY'])

        # Fetch and store phone number prefixes
        server_response = locale.get_countries_phones()
        $phone_prefix_list = server_response.phones
    end

    # Get phone prefix
    phone_prefix = $phone_prefix_list.filter_map { |prefix| prefix if phone_number.start_with?(prefix.code) }

    if phone_prefix.empty?
        raise 'Invalid phone number.'
    end

    # Return phone number prefix
    return res.json({
        :phone_number => phone_number,
        :phonePrefix => phone_prefix[0].code,
        :countryCode => phone_prefix[0].country_code,
        :countryName => phone_prefix[0].country_name,
    })
end