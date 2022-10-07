<?php

require 'vendor/autoload.php';

include_once 'src/DiscordChannel.php';
include_once 'src/SMSChannel.php';
include_once 'src/EmailChannel.php';
include_once 'src/TwitterChannel.php';
include_once 'src/NullChannel.php';

return function($req, $res) {
    try {
        $payload = \json_decode($req['payload'], true);
    } catch (\Exception $e) {
        $res->json([
            'status' => false,
            'message' => $e->getMessage(),
        ]);
    };

    $notificator = match (strtolower($payload['type'])) {
        'sms' => new SMSChannel($req['variables']),
        'email' => new EmailChannel($req['variables']),
        'twitter' => new TwitterChannel($req['variables']),
        'discord' => new DiscordChannel($req['variables']),
        default => new NullChannel,
    };

    $res->json(
        json: $notificator->send(
            payload: $payload,
        ),
    );
};
