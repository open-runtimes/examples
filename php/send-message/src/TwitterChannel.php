<?php

include_once 'Contracts/Channel.php';

use GuzzleHttp\Client;
use GuzzleHttp\HandlerStack;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\RequestOptions;
use GuzzleHttp\Subscriber\Oauth\Oauth1;

final class TwitterChannel implements Channel
{
    public function __construct(
        private readonly array $environment,
    ) {
    }

    public function hasEnvironmentVariables(): string
    {
        return array_key_exists('TWITTER_API_KEY', $this->environment)
            && array_key_exists('TWITTER_API_KEY_SECRET', $this->environment)
            && array_key_exists('TWITTER_ACCESS_TOKEN', $this->environment)
            && array_key_exists('TWITTER_ACCESS_KEY_SECRET', $this->environment);
    }

    public function getConsumerKey(): string
    {
        return $this->environment['TWITTER_API_KEY'];
    }

    public function getConsumerSecret(): string
    {
        return $this->environment['TWITTER_API_KEY_SECRET'];
    }

    public function getTokenKey(): string
    {
        return $this->environment['TWITTER_ACCESS_TOKEN'];
    }

    public function getTokenSecret(): string
    {
        return $this->environment['TWITTER_ACCESS_KEY_SECRET'];
    }

    public function client(): Client
    {
        $stack = HandlerStack::create();

        $stack->push(
            middleware: new Oauth1(
                config: [
                    'consumer_key'    => $this->getConsumerKey(),
                    'consumer_secret' => $this->getConsumerSecret(),
                    'token'           => $this->getTokenKey(),
                    'token_secret'    => $this->getTokenSecret(),
                ]
            ),
        );

        return new Client(
            config: [
                'handler' => $stack,
            ],
        );
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
            $response = $this->client()->send(
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
