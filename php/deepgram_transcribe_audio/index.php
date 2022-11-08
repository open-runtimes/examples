<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    if (!isset($payload['fileUrl']) || empty($fileUrl = \trim($payload['fileUrl']))) {
        return $res->json([
            'success' => false,
            'message' => 'Please provide a valid file URL.'
        ]);
    }
    if (
        !isset($req['variables']['DEEPGRAM_API_SECRET_KEY']) || 
        empty($deepgramSecretApiKey = \trim($req['variables']['DEEPGRAM_API_SECRET_KEY']))
    ) {
        return $res->json([
            'success' => false,
            'message' => 'Please provide valid deepgram api secret key under variables.'
        ]);
    }

    $extension = pathinfo($fileUrl, PATHINFO_EXTENSION);

    if ($extension !== 'wav') {
        return $res->json([
            'success' => false,
            'message' => 'Please provide valid file url extension. This should be wav.'
        ]);
    }

    $client = new Client();
    $headers = [
        'Authorization' => 'Token ' . $deepgramSecretApiKey,
        'Content-Type' => 'application/json'
    ];
    $body = json_encode([
        'url' => $fileUrl
    ]);
    try {
        $response = $client->request('POST', 'https://api.deepgram.com/v1/listen', [
            'body' => $body,
            'headers' => $headers
        ]);
    } catch(\Exception $err) {
        return $res->json([
            'success' => false,
            'message' => $err->getMessage()
        ]);
    }

    if ($response->getStatusCode() === 200) {
        $deepgramData = $response->getBody()->getContents();
        $res->json([
            'success' => true,
            'deepgramData' => \json_decode($deepgramData)
        ]);
    } else {
        $res->json([
            'success' => false,
            'message' => $response->getBody()->getContents()
        ]);
    }
}
?>