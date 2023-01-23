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
        private readonly array $environment,
    ) {
        $this->client = new Client;
    }

    public function hasEnvironmentVariables(): string
    {
        return array_key_exists('TWILIO_ACCOUNT_SID', $this->environment)
            && array_key_exists('TWILIO_AUTH_TOKEN', $this->environment)
            && array_key_exists('TWILIO_SENDER', $this->environment);
    }

    public function getAccountSID(): string
    {
        return $this->environment['TWILIO_ACCOUNT_SID'];
    }

    public function getToken(): string
    {
        return $this->environment['TWILIO_AUTH_TOKEN'];
    }

    public function getPhoneNumber(): string
    {
        return $this->environment['TWILIO_SENDER'];
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
            validator: new PhoneNumberValidator(
                client: $this->client,
                SID: $this->getAccountSID(),
                token: $this->getToken(),
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
                    uri: sprintf('https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json', rawurlencode($this->getAccountSID())),
                    headers: [
                        'content-type'  => 'application/x-www-form-urlencoded',
                    ],
                ),
                options: [
                    RequestOptions::BODY => Query::build(
                        params: [
                            'To'   => $payload['receiver'],
                            'From' => $this->getPhoneNumber(),
                            'Body' => $payload['message'],
                        ],
                        encoding: PHP_QUERY_RFC1738,
                    ),
                    RequestOptions::AUTH => [
                        $this->getAccountSID(), $this->getToken(),
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
