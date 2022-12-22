require 'appwrite'

def main(req, res)
  client = Appwrite::Client.new
  database = Appwrite::Databases.new(client)

  if !req.variables['APPWRITE_FUNCTION_ENDPOINT'] or !req.variables['APPWRITE_FUNCTION_API_KEY']
    return res.json({
      :success => false,
      :message => 'Environment variables are not set. Function cannot use Appwrite SDK.',
    })
  end

  database_id = nil
  collection_id = nil
  begin
    payload = JSON.parse(req.payload)
    database_id = (payload['databaseId'] || '').delete(' ')
    collection_id = (payload['collectionId'] || '').delete(' ')
  rescue Exception => err
    return res.json({
      :success => false,
      :message => 'Payload is invalid.',
    })
  end

  if !database_id || database_id == ''
    return res.json({
      :success => false,
      :message => 'databaseId was not provided.',
    })
  end

  if !collection_id || collection_id == ''
    return res.json({
      :success => false,
      :message => 'collectionId was not provided.',
    })
  end

  client
    .set_endpoint(req.variables['APPWRITE_FUNCTION_ENDPOINT'])
    .set_project(req.variables['APPWRITE_FUNCTION_PROJECTID'])
    .set_key(req.variables['APPWRITE_FUNCTION_API_KEY'])
    .set_self_signed(true)

  done = false
  while !done do
    document_list = nil
    begin
      document_list = database.list_documents(database_id: database_id, collection_id: collection_id)
    rescue Exception => err
      return res.json({
        :success => false,
        :message => 'Failed to get documents list.',
      })
    end

    document_list.documents.each do |document|
      database.delete_document(database_id: database_id, collection_id: collection_id, document_id: document.id)
    end

    if document_list.documents.count <= 0
      done = true
    end
  end

  return res.json({
    :success => true,
  })
end
