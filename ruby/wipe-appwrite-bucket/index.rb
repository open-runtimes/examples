require 'appwrite'

def main(req, res)
  client = Appwrite::Client.new
  storage = Appwrite::Storage.new(client)

  if !req.variables['APPWRITE_FUNCTION_ENDPOINT'] or !req.variables['APPWRITE_FUNCTION_API_KEY']
    return res.json({
      :success => false,
      :message => 'Environment variables are not set. Function cannot use Appwrite SDK.',
    })
  end

  bucket_id = nil
  begin
    payload = JSON.parse(req.payload)
    bucket_id = (payload['bucketId'] || '').delete(' ')
  rescue Exception => err
    return res.json({
      :success => false,
      :message => 'Payload is invalid.',
    })
  end

  if !bucket_id || bucket_id == ''
    return res.json({
      :success => false,
      :message => 'bucketId was not provided.',
    })
  end

  client
    .set_endpoint(req.variables['APPWRITE_FUNCTION_ENDPOINT'])
    .set_project(req.variables['APPWRITE_FUNCTION_PROJECTID'])
    .set_key(req.variables['APPWRITE_FUNCTION_API_KEY'])
    .set_self_signed(true)

  file_list = nil
  begin
    file_list = storage.list_files(bucket_id: bucket_id)
  rescue Exception => err
    return res.json({
      :success => false,
      :message => 'Failed to get file list.',
    })
  end

  file_list.files.each do |file|
    storage.delete_file(bucket_id: bucket_id, file_id: file.id)
  end

  return res.json({
    :success => true,
  })
end
