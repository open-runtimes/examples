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
        'sms' => new SMSChannel(
            $req['variables']['TWILIO_ACCOUNT_SID'],
            $req['variables']['TWILIO_AUTH_TOKEN'],
            $req['variables']['TWILIO_SENDER'],
        ),
        'email' => new EmailChannel(
            $req['variables']['MAILGUN_API_KEY'],
            $req['variables']['MAILGUN_DOMAIN'],
        ),
        'twitter' => new TwitterChannel(
            $req['variables']['TWITTER_API_KEY'],
            $req['variables']['TWITTER_API_KEY_SECRET'],
            $req['variables']['TWITTER_ACCESS_TOKEN'],
            $req['variables']['TWITTER_ACCESS_KEY_SECRET'],
        ),
        'discord' => new DiscordChannel(
            $req['variables']['DISCORD_WEBHOOK_URL'],
        ),
        default => new NullChannel,
    };

    $res->json(
        json: $notificator->send(
            payload: $payload,
        ),
    );
};
