<?php

return function($req, $res) {
    $fileUrl = null;
    try {
        $payload = \json_decode($req['payload'], true);
        $fileUrl = \trim($payload['fileUrl']);
    } catch(\Exception $err) {
        $res->json([
            'success' => false,
            'message' => 'Cannot read payload.'
        ]);
        return;
    }

    $secretKey = $req['variables']['DEEPGRAM_API_KEY'];
    $request = "https://api.deepgram.com/v1/listen?summarize=true&punctuate=true";
    $ch = curl_init($request);
 
    $payload = json_encode( array( "url" => $fileUrl ) );
    curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
        "Content-Type: application/json",
        "Authorization: Token " . $secretKey
    ));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
    $response = curl_exec($ch);

    if (curl_errno($ch)) {
        $res->json([
            'success' => false,
            'message' => 'Unexpected request error.'
        ]);
        return;
    } else {
        $resultStatus = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        if ($resultStatus != 200) {
            $res->json([
                'success' => false,
                'message' => 'HTTP ' . $resultStatus . ' error.'
            ]);
            return;
        }
    }

    curl_close($ch);
 
    $res->json([
        'success' => true,
        'deepgramData' => \json_decode($response)
    ]);
}

?>
