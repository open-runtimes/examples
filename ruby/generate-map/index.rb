require 'open-street-map'
require 'json'
require 'uri'
require 'net/http'
require 'base64'

def error(res, message)
  return res.json({"success": false, "message": message})
end

def generate_tile_number(lat_as_deg, lon_as_deg, zoom)
  lat_as_rad = lat_as_deg/180 * Math::PI
  n = 2.0 ** zoom
  x = ((lon_as_deg + 180.0) / 360.0 * n).to_i
  y = ((1.0 - Math::log(Math::tan(lat_as_rad) + (1 / Math::cos(lat_as_rad))) / Math::PI) / 2.0 * n).to_i
  return {:x => x, :y =>y}
end

def main(req, res)
  lng = nil
  lat = nil
  begin
    payload = JSON.parse(req.payload)
    lng = payload["lng"]
    lat = payload["lat"]
  rescue
    return error(res, "The payload must contain lng and lat")
  end

  if (lng == nil || lat == nil)
    return error(res, "The payload must contain lng and lat")
  elsif (!lng.is_a?(Integer) && !lat.is_a?(Integer)) || (!lng.is_a?(Float) && !lat.is_a?(Float))
    return error(res, "The longitude and latitude must be a number")
  elsif (!(-180..180).include? lng)
    return error(res, "The longitude must be between -180 and 180")
  elsif (!(-90..90).include? lat)
    return error(res, "The latitude must be between -90 and 90")
  end
  # Check if User-Agent env var exists
  user_agent_header = nil
  begin
    user_agent_header = req.env['USER_AGENT']
  rescue
    return error(res, "Missing 'User-Agent' enviornment variable.")
  end

  zoom = 16
  tiles = generate_tile_number(lat, long, zoom)
  uri = URI("https://tile.openstreetmap.org/#{zoom}/#{tiles[:x]}/#{tiles[:y]}.png")

  api_req = Net::HTTP::Get.new(uri)
  api_req['User-Agent'] = user_agent_header
  api_res = Net::HTTP.start(uri.hostname, uri.port, use_ssl: uri.scheme == 'https') { |http|
    http.request(api_req)
  }
  base64_api_res =  Base64.strict_encode64(api_res.body)

  res.json({"success":true,"image":base64_api_res})
end
