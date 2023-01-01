<?php

interface Channel
{
    public function send(array $payload): array;
}
