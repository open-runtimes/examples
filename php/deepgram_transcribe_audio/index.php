<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

function buildTranscriptFromResponse(array $response) {
    $channels = $response['results']['channels'];
    $transcript = '';

    foreach($channels as $channel) {
        $alternatives = $channel['alternatives'];
        foreach($alternatives as $alternative) {
            $transcript.= $alternative['transcript'];
        }
    }

    return $transcript;
}

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
        $url = \trim($payload['fileUrl']);
        $deepgramSecretApiKey = \trim($payload['variables']['deepgramApiSecretKey']);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    $client = new Client();
    $headers = [
        'Authorization' => 'Token ' . $deepgramSecretApiKey,
        'Content-Type' => 'application/json'
    ];
    $body = json_encode([
        'url' => $url
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
        $decodedResponse = (\json_decode($response->getBody()->getContents(), true));
        $transcript = buildTranscriptFromResponse($decodedResponse);
        $res->json([
            'success' => true,
            'deepgramData' => json_encode($transcript)
        ]);
    } else {
        $res->json([
            'success' => false,
            'message' => $response->getBody()->getContents()
        ]);
    }
}
?>