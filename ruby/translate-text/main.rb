require "free_google_translate"
require "json"

def main(request, response)

    # If there is no text to translate, return an error.
    if request.payload.nil? || request.payload.empty? 
        return response.json({
            code: 400,
            message: "No payload provided."
        })
    end

    # Parse the payload.
    payload = JSON.parse(request.payload)
    text = payload["text"]
    source_language = payload["sourceLanguage"]
    target_language = payload["targetLanguage"]

    # If the text, or source or destination languages are empty, return an error
    if text.nil? || text.empty? || source_language.nil? || source_language.empty? || destination_language.nil? || destination_language.empty?
        return response.json({ 
            code: 400,
            message: "The text, source language and target language are required."
        })
    end

    # Translate your text
    translation = GoogleTranslate.translate(
        from: source_language,
        to: target_language,
        text: text
    )

    # Return the translation
    response.json({
        "translation": translation
    })
end
