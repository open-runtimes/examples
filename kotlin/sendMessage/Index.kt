

fun sendEmailMailgun(variables: map<string, string>, email: string, message: string, subject: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendEmailMailgun",))
}

fun sendMessageDiscordWebhook(variables: map<string, string>, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendMessageDiscordWebhook"))
}

fun sendSmsTwilio(variables: map<string, string>, phoneNumber: string, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendSmsTwilio"))

fun sendTweet(variables: map<string, string>, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendTweet"))
}
