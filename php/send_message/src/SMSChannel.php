<?php

include_once 'Validators/PhoneNumberValidator.php';
include_once 'Contracts/Channel.php';
include_once 'Traits/HasReceiver.php';

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Query;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;

final class SMSChannel implements Channel
{
    use HasReceiver;

    private Client $client;

    public function __construct(
        private readonly string $SID,
        private readonly string $token,
        private readonly string $phoneNumber,
    ) {
        $this->client = new Client;
    }

    public function send(array $payload): array
    {
        if (! $this->validate(
            validator: new PhoneNumberValidator(
                client: $this->client,
                SID: $this->SID,
                token: $this->token,
            ),
            receiver: $payload['receiver'],
        )) {
            return [
                'success' => false,
                'message' => 'Receiver is not a valid phone number.',
            ];
        }

        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'POST',
                    uri: sprintf('https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json', rawurlencode($this->SID)),
                    headers: [
                        'content-type'  => 'application/x-www-form-urlencoded',
                    ],
                ),
                options: [
                    RequestOptions::BODY => Query::build(
                        params: [
                            'To'   => $payload['receiver'],
                            'From' => $this->phoneNumber,
                            'Body' => $payload['message'],
                        ],
                        encoding: PHP_QUERY_RFC1738,
                    ),
                    RequestOptions::AUTH => [
                        $this->SID, $this->token,
                    ],
                    RequestOptions::ALLOW_REDIRECTS => false,
                ],
            );
        } catch (\Exception $e) {
            return [
                'success' => false,
                'message' => $e->getMessage(),
            ];
        }

        return [
            'success' => (200 <= $response->getStatusCode() && $response->getStatusCode() < 300),
        ];
    }
}
