fun sendMessageDiscordWebhook(variables: Map<String, String>, message: String?): Map<String, Any>{
    val webhook = variables["DISCORD_WEBHOOK_URL"]

    val url = URL(webhook)
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
    val body = "{\"content\":\"$message\"}"
    conn.addRequestProperty("Content-Type", "application/json")
    conn.requestMethod = "POST"
    conn.doOutput = true
    val os: OutputStream = conn.outputStream
    val input: ByteArray = body.toByteArray(Charsets.UTF_8)
    println(input)
    os.write(input)
    val responseCode: Int = conn.responseCode // To Check for 200
    os.close()
    conn.disconnect()
    if (responseCode / 100 == 2) {    //HTTP code of 2xx means success (most of the time)
        return mapOf("success" to true,
        "message" to "You called sendMessageDiscordWebhook")
    }
    else {
        return mapOf("success" to false,
                     "message" to conn.getResponseMessage())    
    } 
}