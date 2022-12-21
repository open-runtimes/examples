<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
        if (!isset($payload['url']) || empty($url = \trim($payload['url']))) {
            throw new \Exception('Please provide valid url.');
        }
        if (!isset($payload['method']) || empty($method = \trim($payload['method']))) {
            throw new \Exception('Please provide valid method.');
        }
        $body = isset($payload['body']) ? \trim($payload['body']) : null;
        $headers = isset($payload['headers']) ? $payload['headers'] : [];
    } catch(\Exception $err) {
        \var_dump($err);

        $res->json([
            'success' => false,
            'message' => 'Payload is invalid: ' . $err->getMessage()
        ]);
        return;
    }
    
    $client = new Client();
    $response = $client->request($method, $url, [
        'body' => $body,
        'headers' => $headers
    ]);

    if ($response->getStatusCode() >= 200 && $response->getStatusCode() < 300) {
        $res->json([
            'success' => true,
            'response' => [
                'headers' => $response->getHeaders(),
                'code' => $response->getStatusCode(),
                'body' => $response->getBody()->getContents()
            ]
        ]);
        return;
    }

    $res->json([
        'success' => false,
        'message' => $response->getBody()->getContents()
    ]);
}
?>