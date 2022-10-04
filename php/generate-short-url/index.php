<?php

require_once 'vendor/autoload.php';

return function ($req, $res) {
  $payload = json_decode($req['payload'], true);

  $chosenProvider = $payload['provider'] ?? null;
  $longUrl = $payload['url'] ?? null;

  if (null === $chosenProvider) {
    $res->json(['success' => false, 'message' => 'Provider param is required. [bitly, tinyurl]'], 400);
    return;
  }

  if (false === in_array($chosenProvider, ['bitly', 'tinyurl'])) {
    $res->json(['success' => false, 'message' => 'Provided unsupported provider name. [bitly, tinyurl]'], 400);
    return;
  }

  // Prepare request data
  switch ($chosenProvider) {
    case 'bitly':
      $apiUrl = 'https://api-ssl.bitly.com/v4/shorten';
      $authorizationToken = $req['env']['API_BITLY_AUTHORIZATION_TOKEN'] ?? null;
      $data = json_encode(['long_url' => $longUrl]);
      break;
    case 'tinyurl':
      $apiUrl = 'https://api.tinyurl.com/create';
      $authorizationToken = $req['env']['API_TINYURL_AUTHORIZATION_TOKEN'] ?? null;
      $data = json_encode(['url' => $longUrl]);
      break;
    default:
      $apiUrl = null;
      $authorizationToken = null;
      $data = null;
  }

  if (null === $authorizationToken) {
    $res->json(['success' => false, 'message' => 'Authorization token not set in variables'], 401);
    return;
  }

  // Make request
  $curl = curl_init();
  curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($curl, CURLOPT_URL, $apiUrl);
  curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
  curl_setopt($curl, CURLOPT_POST, true);
  curl_setopt($curl, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json',
    sprintf('Authorization: Bearer %s', $authorizationToken)
  ]);

  $apiResponse = json_decode(curl_exec($curl), true);
  curl_close($curl);

  // Extract request result
  switch ($chosenProvider){
    case 'bitly':
      $shortenUrl = $apiResponse['link'] ?? null;
      $errorMessage = $apiResponse['message'] ?? null;
      break;
    case 'tinyurl':
      $shortenUrl = $apiResponse['data']['tiny_url'] ?? null;
      $errorMessage = $apiResponse['errors'][0] ?? null;
      break;
    default:
      $shortenUrl = null;
      $errorMessage = 'Error.';
  }

  if (null === $shortenUrl) {
    $res->json(['success' => false, 'message' => $errorMessage], 401);
    return;
  }

  $res->json(['success' => true, 'url' => $shortenUrl]);
};
