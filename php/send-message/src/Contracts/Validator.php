<?php

interface Validator
{
    public function validate(string $receiver): bool;
}
