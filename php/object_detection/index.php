<?php

return function ($req, $res) {
    // Input validation
    $url = null;
    try {
        $payload = \json_decode($req['payload'], true);
        $url = \trim($payload['url']);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    if(empty($url)) {
        throw new \Exception('Invalid url.');
    }

    // Make sure we have envirnment variables required to execute
    if(
        empty($req['variables']['CLOUDMERSIVE_API_KEY'])
    ) {
        throw new \Exception('Please provide all required environment variables.');
    }

    // Download image to buffer
    $ch = \curl_init($url);
    \curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    \curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

    $image = \curl_exec($ch);
    \curl_close($ch);

    // Upload to cloudmersive
    $ch = \curl_init('https://api.cloudmersive.com/image/recognize/detect-objects');
    \curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    \curl_setopt($ch, CURLOPT_POST, true);
    \curl_setopt($ch, CURLOPT_POSTFIELDS, [
        'imageFile' => $image,
    ]);
    \curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Apikey: ' . $req['variables']['CLOUDMERSIVE_API_KEY'],
    ]);

    $response = \json_decode(\curl_exec($ch), true);
    \curl_close($ch);

    // Return phone number prefix
    $res->json($response['Objects']);
};