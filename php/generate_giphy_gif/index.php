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

    // Make sure we have environment variables required to execute
    if(
        empty($req['variables']['GIPHY_API_KEY'])
    ) {
        throw new \Exception('Please provide all required environment variables.');
    }
    // Make GET request to api.giphy.com
    $ch = \curl_init(\sprintf('https://api.giphy.com/v1/gifs/search?api_key=%s&q=%s&limit=1', $req['variables']['GIPHY_API_KEY'], \rawurlencode($search)));
    \curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    \curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

    $response = \json_decode(\curl_exec($ch), true);
    \curl_close($ch);

    // Return the search query and the first GIF
    $res->json([
        'search' => $search,
        'gif' => $response["data"][0]['url'],
    ]);
};
