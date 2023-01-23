<?php

include_once __DIR__.'/../Contracts/Validator.php';

final class EmailValidator implements Validator
{
    public function validate(string $receiver): bool
    {
        return !!filter_var($receiver, FILTER_VALIDATE_EMAIL);
    }
}
