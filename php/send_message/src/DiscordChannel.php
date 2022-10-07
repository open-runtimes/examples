<?php

include_once 'Contracts/Channel.php';

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;

final class DiscordChannel implements Channel
{
    private Client $client;

    public function __construct(
        private readonly string $endpoint,
    ) {
        $this->client = new Client(
            config: [
                'headers' => [
                    'content-type' => 'application/json',
                ]
            ]
        );
    }

    public function send(array $payload): array
    {
        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'POST',
                    uri: $this->endpoint,
                ),
                options: [
                    RequestOptions::JSON => [
                        'content' => $payload['message'],
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
