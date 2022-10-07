<?php

include_once 'Validators/EmailValidator.php';
include_once 'Contracts/Channel.php';
include_once 'Traits/HasReceiver.php';

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;

final class EmailChannel implements Channel
{
    use HasReceiver;
    
    protected Client $client;

    public function __construct(
        private readonly string $apiKey,
        private readonly string $domain,
    ) {
        $this->client = new Client;
    }

    public function send(array $payload): array
    {
        if (! $this->validate(
            validator: new EmailValidator,
            receiver: $payload['receiver'],
        )) {
            return [
                'success' => false,
                'message' => 'Receiver is not a valid email.',
            ];
        }

        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'POST',
                    uri: sprintf('https://api.mailgun.net/v3/%s/messages', $this->domain),
                    headers: [
                        'authorization' => sprintf('Basic %s', base64_encode(sprintf('api:%s', $this->apiKey))),
                    ]
                ),
                options: [
                    RequestOptions::FORM_PARAMS => [
                        'from'    => "SendMessage <mailgun@{$this->domain}>",
                        'to'      => $payload['receiver'],
                        'subject' => $payload['subject'],
                        'text'    => $payload['message'],
                    ],
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
