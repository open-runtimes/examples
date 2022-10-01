// Include voyage module
const Vonage = require('@vonage/server-sdk')

const vonage = new Vonage({
  apiKey: "0ab91e43",
  apiSecret: "81fPXp0J9HbhSC8w"
})

// Initialize with sender and reciever
// mobile number with text message
const from = "Vonage APIs";
const to = "+12345678";
const text = 'Programming is fun!';


vonage.message.sendSms(from, to, text, (err, responseData) => {
    if (err) {
        console.log(err);
    } else {
        if(responseData.messages[0]['status'] === "0") {
            console.log("Message sent successfully.");
        } else {
            console.log("Receiver not in correct format.");
        }
    }
})