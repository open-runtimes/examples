<?php

return function ($req, $res) {

    // Make sure we have variables required to execute
    if(
        empty($req['variables']['DEEPGRAM_API_KEY'])
    ) {
        $res->json([
            'success' => false, 
            'message' => 'Please provide all required environment variables.',
        ]);
    }

    // Make sure the payload is populated
    try {
        $payload = \json_decode($req['payload'], true);
        $file_url = \trim($payload['fileUrl']);
    } catch(\Exception $err) {
        $res->json([
            'success' => false, 
            'message' => 'Payload is invalid with error ' . $err->getMessage(),
        ]);
    }

    if(empty($file_url)) {
        $res->json([
            'success' => false, 
            'message' => 'Invalid url. ' . $file_url,
        ]);
    }
    
    $ch = \curl_init("https://api.deepgram.com/v1/listen?detect_language=true&punctuate=true");
    // Authorization: Token <YOUR_SECRET>
    \curl_setopt($ch, CURLOPT_HTTPHEADER, array("Authorization: Token " . $req['variables']['DEEPGRAM_API_KEY'], "Content-Type: application/json"));
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