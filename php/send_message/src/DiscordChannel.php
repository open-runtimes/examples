<?php

include_once 'Contracts/Channel.php';

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;

final class DiscordChannel implements Channel
{
    private Client $client;

    public function __construct(
        private readonly array $environment,
    ) {
        $this->client = new Client(
            config: [
                'headers' => [
                    'content-type' => 'application/json',
                ]
            ]
        );
    }

    public function hasEnvironmentVariables(): string
    {
        return array_key_exists('DISCORD_WEBHOOK_URL', $this->environment);
    }

    public function getEndpoint(): string
    {
        return $this->environment['DISCORD_WEBHOOK_URL'];
    }

    public function send(array $payload): array
    {
        if (! $this->hasEnvironmentVariables()) {
            return [
                'success' => false,
                'message' => 'Environment variables are not set.',
            ];
        }

        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'POST',
                    uri: $this->getEndpoint(),
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
