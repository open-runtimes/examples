#include <iostream>
#include <string>
#include <curl/curl.h>

static size_t WriteCallback(void *contents, size_t size, size_t nmemb, void *userp)
{
    ((std::string *) userp)->append((char *) contents, size * nmemb);
    return size * nmemb;
}

static RuntimeResponse &main(const RuntimeRequest &req, RuntimeResponse &res) {
    Json::Value response;

    std::string provider;
    std::string url;

    Json::CharReaderBuilder builder;
    Json::CharReader *reader = builder.newCharReader();
    Json::Value payload;

    bool parsingSuccessful = reader->parse(
        req.payload.c_str(),
        req.payload.c_str() + req.payload.size(),
        &payload,
        nullptr
    );

    if (parsingSuccessful)
    {
        provider = payload["provider"].asString();
        url = payload["url"].asString();
    }

    if (provider.empty())
    {
        response["success"] = false;
        response["message"] = "Please enter provider";
        return res.json(response);
    }

    if (provider.empty())
    {
        response["success"] = false;
        response["message"] = "Please enter URL";
        return res.json(response);
    }

    std::string apiKey;

    if(provider == "bitly") {
        apiKey = req.variables["BITLY_API_KEY"].asString();
    } else if(provider == "tinyurl") {
        apiKey = req.variables["TINYURL_API_KEY"].asString();
    } else {
        response["success"] = false;
        response["message"] = "Invalid provider.";
        return res.json(response);
    }

    if (apiKey.empty())
    {
        response["success"] = false;
        response["message"] = "Please configure API key.";
        return res.json(response);
    }

    std::string responseBuffer;
    long httpCode = 0;

    CURL *curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &responseBuffer);
    curl_easy_getinfo (curl, CURLINFO_RESPONSE_CODE, &httpCode);

    if(provider == "bitly") {
        std::string url = "https://api-ssl.bitly.com/v4/shorten";
        std::string body = "{\"long_url\":\"" + url + "\"}";
        std::string authHeader = "Authorization: Bearer " + apiKey;

        struct curl_slist *headers;
        headers = NULL;
        headers = curl_slist_append(headers, "Content-Type: application/json");
        headers = curl_slist_append(headers, authHeader.c_str());

        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body.c_str());
        curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST");
    } else if(provider == "tinyurl") {
    }

    CURLcode curlStatus = curl_easy_perform(curl);
    curl_easy_cleanup(curl);


    if(httpCode < 400) {
        // TODO: Finish (get response + return url)
        Json::Value responseJson;
        reader->parse(
            responseBuffer.c_str(),
            responseBuffer.c_str() + responseBuffer.size(),
            &responseJson,
            nullptr
        );

        delete reader;

        response["success"] = true;
        response["data"] = responseJson;
        response["httpCode"] = httpCode;
        return res.json(response);
    } else {
        std::string message = "Could not generate url with error " + std::to_string(httpCode) +  ": " + responseBuffer.c_str();
        response["success"] = false;
        response["message"] = message;
        return res.json(response);
    }

}