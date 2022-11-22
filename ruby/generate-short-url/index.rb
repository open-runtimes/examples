require 'json'
require 'uri'
require 'net/http'

VALID_PROVIDERS = %w[bitly tinyurl].freeze
API_URLS = {
  'bitly' => 'https://api-ssl.bitly.com/v4/shorten',
  'tinyurl' => 'https://api.tinyurl.com/create'
}.freeze

def main(req, res)
  payload = req.payload
  env = req.variables
  # Input validation
  validate_params(payload)
  # Make sure we have environment variables required to execute
  validate_environment_variables(env)
  # Build request params
  params = make_request_params(payload)
  token = get_provider_token(payload['provider'], env)
  uri = URI(params['url'])
  # Make request to generate short url
  make_request(uri, params, token, payload, res)
end

def make_request_params(payload)
  provider = payload['provider']

  return { 'url' => API_URLS[provider], 'body' => { 'long_url' => payload['url'] } } if provider == 'bitly'
  return { 'url' => API_URLS[provider], 'body' => { 'url' => payload['url'] } } if provider == 'tinyurl'
end

def parse_response(http_response, provider)
  body = JSON.parse(http_response.body)

  return { 'success' => false, 'errors' => body['errors'] } if http_response.code != '200'
  return { 'success' => true, 'url' => body['link'] } if provider == 'bitly'
  return { 'success' => true, 'url' => body['data']['tiny_url'] } if provider == 'tinyurl'
end

def validate_params(payload)
  error_message = 'Payload is invalid, you should specify a valid provider and url.'

  is_invalid_url = payload['url'].nil? || payload['url'].empty?
  is_invalid_provider = payload['provider'].nil? || payload['provider'].empty?
  provider_not_supported = !VALID_PROVIDERS.include?(payload['provider'])

  raise error_message if is_invalid_url || is_invalid_provider || provider_not_supported
end

def validate_environment_variables(env)
  return unless !env['BITLY_ACCESS_TOKEN'] || !env['TINYURL_ACCESS_TOKEN']

  raise 'Please provide all required environment variables.'
end

def get_provider_token(provider, env)
  provider == 'bitly' ? env['BITLY_ACCESS_TOKEN'] : env['TINYURL_ACCESS_TOKEN']
end

def make_request(uri, params, token, payload, res)
  Net::HTTP.start(uri.host, uri.port, use_ssl: uri.scheme == 'https') do |http|
    request = Net::HTTP::Post.new(uri, 'Content-Type' => 'application/json', 'Authorization' => "Bearer #{token}")
    request.body = params['body'].to_json
    response = http.request(request)

    return res.json(
      parse_response(response, payload['provider'])
    )
  end
end
