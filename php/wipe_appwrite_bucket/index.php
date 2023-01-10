<?php

require_once('vendor/autoload.php');

use Appwrite\Client;
use Appwrite\Services\Storage;

return function ($req, $res) {
    // Input validation
    $bucketId = null;
    try {
        $payload = \json_decode($req['payload'], true);
        $bucketId = \trim($payload['bucketId']);
    } catch(\Exception $err) {
        \var_dump($err);
        $res->json([
            'success' => false,
            'message' => 'Payload is invalid.'
        ]);
        return;
    }

    if(empty($bucketId)) {
        $res->json([
            'success' => false,
            'message' => 'Invalid bucket id.'
        ]);
        return;
    }

    // Make sure we have envirnment variables required to execute
    if(!$req['variables']['APPWRITE_FUNCTION_ENDPOINT'] ||
       !$req['variables']['APPWRITE_FUNCTION_PROJECT_ID'] ||
       !$req['variables']['APPWRITE_FUNCTION_API_KEY']) {
        $res->json([
            'success' => false,
            'message' => 'Please provide all required environment variables.'
        ]);
        return;
    }

    $client = new Client();
    $storage = new Storage($client);

    $client
        ->setEndpoint($req['variables']['APPWRITE_FUNCTION_ENDPOINT'])
        ->setProject($req['variables']['APPWRITE_FUNCTION_PROJECT_ID'])
        ->setKey($req['variables']['APPWRITE_FUNCTION_API_KEY']);

    $done = false;
    try {
        while (!$done) {
            $files = $storage->listFiles($bucketId)['files'];
            foreach ($files as $file) {
                $storage->deleteFile($bucketId, $file['$id']);
            }

            if (count($files) == 0) {
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
        'success' => true
    ]);
};
