<?php

include_once 'Contracts/Channel.php';

use GuzzleHttp\Client;
use GuzzleHttp\HandlerStack;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;
use GuzzleHttp\Subscriber\Oauth\Oauth1;

final class TwitterChannel implements Channel
{
    private Client $client;

    public function __construct(
        private readonly string $consumerKey,
        private readonly string $consumerSecret,
        private readonly string $tokenKey,
        private readonly string $tokenSecret,
    ) {
        $stack = HandlerStack::create();

        $stack->push(
            middleware: new Oauth1(
                config: [
                    'consumer_key'    => $consumerKey,
                    'consumer_secret' => $consumerSecret,
                    'token'           => $tokenKey,
                    'token_secret'    => $tokenSecret,
                ]
            ),
        );

        $this->client = new Client(
            config: [
                'handler' => $stack,
            ],
        );
    }

    public function send(array $payload): array
    {
        try {
            $response = $this->client->send(
                request: new Request(
                    method: 'POST',
                    uri: 'https://api.twitter.com/1.1/statuses/update.json'
                ),
                options: [
                    RequestOptions::QUERY => [
                        'status' => $payload['message'],
                    ],
                    RequestOptions::AUTH => 'oauth',
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
