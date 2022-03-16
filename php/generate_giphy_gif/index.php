<?php

return function ($req, $res) {
    // Input validation
    $search = null;
    try {
        $payload = \json_decode($req['payload'], true);
        $search = \trim($payload['search']);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    if(empty($search)) {
        throw new \Exception('Invalid Search Query.');
    }

    // Make sure we have envirnment variables required to execute
    if(
        empty($req['env']['GIPHY_API_KEY'])
    ) {
        throw new \Exception('Please provide all required environment variables.');
    }

    // Download image to buffer
    $ch = \curl_init("https://api.giphy.com/v1/gifs/search?api_key={$req['env']['GIPHY_API_KEY']}&q={$search}&limit=1");
    \curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    \curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

    $response = \json_decode(\curl_exec($ch), true);
    \curl_close($ch);

    // Return phone number prefix
    $res->json([
        'search' => $search,
        'gif' => $response["data"][0]['url'],
    ]);
};