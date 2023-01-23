<?php

include_once 'Contracts/Channel.php';

final class NullChannel implements Channel
{
    public function send(array $payload): array
    {
        return [
            'success' => false,
            'message' => 'No channel type found.',
        ];
    }
}
