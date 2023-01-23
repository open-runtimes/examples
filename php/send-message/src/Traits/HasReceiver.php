<?php

include_once __DIR__.'/../Contracts/Validator.php';

trait HasReceiver
{
    public function validate(Validator $validator, string $receiver): ?string
    {
        return $validator->validate($receiver);
    }
}
