<?php

return function ($req, $res) {

    // Make sure we have envirnment variables required to execute
    if(
        empty($req['env']['DEEPGRAM_API_KEY'])
    ) {
        throw new \Exception('Please provide all required environment variables.');
    }

    // Make sure the payload is populated
    try {
        $payload = \json_decode($req['payload'], true);
        $file_url = \trim($payload['fileUrl']);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    if(empty($file_url)) {
        throw new \Exception('Invalid url.');
    }
    
    $ch = \curl_init("https://api.deepgram.com/v1/listen?detect_language=true&punctuate=true");
    // Authorization: Token <YOUR_SECRET>
    \curl_setopt($ch, CURLOPT_HTTPHEADER, array("Authorization: Token " . $req['env']['DEEPGRAM_API_KEY'], "Content-Type: application/json"));
    \curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    \curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode(array("url" => $file_url)));

    $response = \curl_exec($ch);
    $httpcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

    if ($response !== false && $httpcode === 200) {
        $return = [
            'success' => true,
            'deepgramData' => \json_decode($response, true),
        ];
    } else {
        $return = [
            'success' => false,
            'message' => 'HTTP code: ' . $httpcode . ' Body: ' . $response,
        ];
    }
    \curl_close($ch);

    $res->json($return);
};