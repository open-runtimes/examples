<?php

require_once 'vendor/autoload.php';

function makeRequest(
  string $apiUrl,
  string $authorizationTokenKey,
  string $data,
  array $req,
  mixed $res,
): ?array
{
  $authorizationToken = $req['variables'][$authorizationTokenKey];

  if (null === $authorizationToken) {
    $res->json(['success' => false, 'message' => 'Authorization token not set in variables'], 401);

    return null;
  }

  $curl = curl_init();

  curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($curl, CURLOPT_URL, $apiUrl);
  curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
  curl_setopt($curl, CURLOPT_POST, true);
  curl_setopt($curl, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json',
    sprintf('Authorization: Bearer %s', $authorizationToken)
  ]);

  $response = json_decode(curl_exec($curl), true);
  curl_close($curl);

  return $response;
}

function displayResponse(mixed $res, ?string $shortenUrl, ?string $errorMessage): void
{
  if (null === $shortenUrl) {
    $res->json(['success' => false, 'message' => $errorMessage]);
    return;
  }

  $res->json(['success' => true, 'url' => $shortenUrl]);
}

return function ($req, $res) {
  $payload = json_decode($req['payload'], true);

  $chosenProvider = $payload['provider'];
  $longUrl = $payload['url'];

  if (null === $chosenProvider) {
    $res->json(['success' => false, 'message' => 'Provider param is required. [bitly, tinyurl]'], 400);
    return;
  }

  if (false === in_array($chosenProvider, ['bitly', 'tinyurl'])) {
    $res->json(['success' => false, 'message' => 'Provided unsupported provider name. [bitly, tinyurl]'], 400);
    return;
  }

  switch ($chosenProvider) {
    case 'bitly':
      $apiResponse = makeRequest(
        'https://api-ssl.bitly.com/v4/shorten',
        'API_BITLY_AUTHORIZATION_TOKEN',
        json_encode(['long_url' => $longUrl]),
        $req,
        $res,
      );

      displayResponse($res, $apiResponse['link'], $apiResponse['message']);

      break;
    case 'tinyurl':
      $apiResponse = makeRequest(
        'https://api.tinyurl.com/create',
        'API_TINYURL_AUTHORIZATION_TOKEN',
        json_encode(['url' => $longUrl]),
        $req,
        $res,
      );

      displayResponse($res, $apiResponse['data']['tiny_url'], $apiResponse['errors'][0]);

      break;
  }
};
