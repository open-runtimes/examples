require "free_google_translate"
require "json"

def main(req, res)
    # Input validation
    text = nil
    source = nil
    target = nil
    begin
        payload = JSON.parse(req.payload)
        text = payload["text"]
        source = payload["source"]
        target = payload["target"]
    rescue Exception => err
        puts err
        raise 'Payload is invalid.'
    end

    if text.nil? or text.empty? or source.nil? or source.empty?  or target.nil? or target.empty?
        raise 'Payload is invalid.'
    end

    # Translate your text
    translation = GoogleTranslate.translate(
        from: source,
        to: target,
        text: text
    )

    # Return the translation
    res.json({
        "text": text,
        "translation": translation
    })
end
