<?php
 
function deepgramAudioSummary($fileUrl)
{
    $secretKey = "Your-Secret-Key";
    $request = "https://api.deepgram.com/v2/speech?fileUrl=" + $fileUrl;
    $ch = curl_init($request);
 
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
        "Content-Type: application/json",
        "Authorization: Bearer " + $secretKey
    ));
 
    $response = curl_exec($ch);
    curl_close($ch);
 
    return $response;
}
 
?>
