<?php

require_once('vendor/autoload.php');

use Appwrite\Client;
use Appwrite\Services\Databases;

return function ($req, $res) {
    $invalPayloadResponse = [
        'success' => false,
        'message' => 'Payload is invalid.'
    ];
    try {
        $payload = \json_decode($req['payload'], true, flags: JSON_THROW_ON_ERROR);
        $databaseId = \trim($payload['databaseId']);
        $collectionId = \trim($payload['collectionId']);
    } catch(\Exception $err) {
        \var_dump($err);
        $res->json($invalPayloadResponse);
        return;
    }

    if(empty($databaseId) || empty($collectionId)) {
        $res->json($invalPayloadResponse);
        return;
    }

    // Make sure we have envirnment variables required to execute
    if(!$req['variables']['APPWRITE_FUNCTION_ENDPOINT'] ||
       !$req['variables']['APPWRITE_FUNCTION_PROJECT_ID'] ||
       !$req['variables']['APPWRITE_FUNCTION_API_KEY']) {
        $res->json([
            'success' => false,
            'message' => 'Please provide all required variables.'
        ]);
        return;
    }

    $client = new Client();
    $databases = new Databases($client);


    $client
        ->setEndpoint($req['variables']['APPWRITE_FUNCTION_ENDPOINT'])
        ->setProject($req['variables']['APPWRITE_FUNCTION_PROJECT_ID'])
        ->setKey($req['variables']['APPWRITE_FUNCTION_API_KEY']);

    $done = false;
    $sum = 0;
    try {
        while (!$done) {

            $documents = $databases->listDocuments($databaseId, $collectionId)['documents'];
            foreach($documents as $document) {
                \var_dump($document);
                $databases->deleteDocument($databaseId, $collectionId, $document['$id']);
                $sum++;
            }

            if (count($documents) === 0) {
                $done = true;
            }
        }
    } catch(\Exception $err) {
        $res->json([
            'success' => false,
            'message' => $err->getMessage()
        ]);
        return;
    }

    // Return success result
    $res->json([
        'success' => true,
        'sum' => $sum
    ]);
};
