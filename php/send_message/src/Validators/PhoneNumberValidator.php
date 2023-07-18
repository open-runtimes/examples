<?php

include_once __DIR__.'/../Contracts/Validator.php';

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;

final class PhoneNumberValidator implements Validator
{
    public function __construct(
        private readonly Client $client,
        private readonly string $SID,
        private readonly string $token,
    ) {
    }

    public function validate(string $receiver): bool
    {
        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'GET',
                    uri: sprintf('https://lookups.twilio.com/v1/PhoneNumbers/%s', rawurlencode($receiver)),
                ),
                options: [
                    RequestOptions::AUTH => [
                        $this->SID, $this->token,
                    ],
                    RequestOptions::ALLOW_REDIRECTS => false,
                ],
            );
        } catch (\Exception) {
            return false;
        }

        return $response->getStatusCode() === 200;
    }
}
