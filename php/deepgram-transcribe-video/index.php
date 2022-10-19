<?php

function deepgramTranscribeVideo($fileUrl) {

    $apiKey = '<API-KEY>';
    $apiSecret = '<API-SECRET>';
    $requestUrl = 'https://api.deepgram.com/v2/speech';

    $requestData = array(
        'url' => $fileUrl,
    );

    $requestOptions = array(
        'http' => array(
            'method' => 'POST',
            'header' => "Content-Type: application/json\r\n" .
                        "Authorization: Basic " . base64_encode("$apiKey:$apiSecret") . "\r\n",
            'content' => json_encode($requestData),
        ),
    );

    $context = stream_context_create($requestOptions);
    $response = file_get_contents($requestUrl, false, $context);

    return json_decode($response, true);
}

?>
