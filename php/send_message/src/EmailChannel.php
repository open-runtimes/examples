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
        private readonly array $environment,
    ) {
        $this->client = new Client;
    }

    public function hasEnvironmentVariables(): string
    {
        return array_key_exists('MAILGUN_API_KEY', $this->environment)
            && array_key_exists('MAILGUN_DOMAIN', $this->environment);
    }

    public function getApiKey(): string
    {
        return $this->environment['MAILGUN_API_KEY'];
    }

    public function getDomain(): string
    {
        return $this->environment['MAILGUN_DOMAIN'];
    }

    public function send(array $payload): array
    {
        if (! $this->hasEnvironmentVariables()) {
            return [
                'success' => false,
                'message' => 'Environment variables are not set.',
            ];
        }

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
                    uri: sprintf('https://api.mailgun.net/v3/%s/messages', $this->getDomain()),
                    headers: [
                        'authorization' => sprintf('Basic %s', base64_encode(sprintf('api:%s', $this->getApiKey()))),
                    ]
                ),
                options: [
                    RequestOptions::FORM_PARAMS => [
                        'from'    => sprintf('SendMessage <mailgun@%s>', $this->getDomain()),
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
