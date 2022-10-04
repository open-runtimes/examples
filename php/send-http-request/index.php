<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
        $url = \trim($payload['url']);
        $method = \trim($payload['method']);
        $body = \trim($payload['body']);
        $headers = $payload['headers'];
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
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
                'code' => 200,
                'body' => $response->getBody()->getContents()
            ]
        ]);
    }

    $res->json([
        'success' => false,
        'message' => $response->getBody()->getContents()
    ]);
}
?>