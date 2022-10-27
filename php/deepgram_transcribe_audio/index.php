<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
        if (!isset($payload['fileUrl']) || empty($fileUrl = \trim($payload['fileUrl']))) {
            throw new \Exception('Please provide valid file url.');
        }
        if (
            !isset($payload['variables']['deepgramApiSecretKey']) || 
            empty($deepgramSecretApiKey = \trim($payload['variables']['deepgramApiSecretKey']))
        ) {
            throw new \Exception('Please provide valid deepgram api secret key under variables.');
        }

        $extension = pathinfo($fileUrl, PATHINFO_EXTENSION);

        if ($extension !== 'wav') {
            throw new \Exception('Please provide valid file url extension. This should be wav.');
        }
    } catch(\Exception $err) {
        \var_dump($err);

        $res->json([
            'success' => false,
            'message' => $err->getMessage()
        ]);
        return;
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
        $res->json([
            'success' => false,
            'message' => $err->getMessage()
        ]);
        return;
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