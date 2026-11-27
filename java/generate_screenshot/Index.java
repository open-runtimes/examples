import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.UUID;

/**
 * @Author:Shizhan
 * @ProjectName:screenshot
 * @currentTime: 2023/7/26 16:02
 */

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws AWTException, URISyntaxException, IOException {
        String payloadString = req.getPayload();
        final Gson gson = new Gson();
        Map<String, Object> payload = gson.fromJson(payloadString, Map.class);
        Map<String, Object> responseData = new HashMap<>();
        //get the url
        String urlInput = payload.get("url").toString();
        //test if the url is valid
        long lo = System.currentTimeMillis();
        boolean isValid = false;
        URL url;
        try {
            url = new URL(urlInput);
            InputStream in = url.openStream();
            //the input is true
            isValid = true;
        } catch (Exception e1) {
            //the input is false
            url = null;
        }
        //if the url is not valid, return false
        if(!isValid){
            responseData.put("success",false);
            responseData.put("message","Website could not be reached.");
            return res.json(responseData);
        }
        //get the absolute path
        File file = new File("src/main/resources/upload");
        String pathname = file.getAbsolutePath();
        //JDK 1.6 and up
        Desktop.getDesktop().browse(new URI(urlInput));
        Robot robot = new Robot();
        robot.delay(5000);
        Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        int width = (int) d.getWidth();
        int height = (int) d.getHeight();
        //maximize the browser.
        robot.keyPress(KeyEvent.VK_F11);
        robot.delay(2000);
        Image image = robot.createScreenCapture(new Rectangle(0, 0, width, height));
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics q = bi.createGraphics();
        q.drawImage(image, 0, 0, width, height, null);
        //get the File
        File outputfile = new File(pathname + "/"+ UUID.randomUUID() +".jpg");
        //get the absolute path
        String absolutePath = outputfile.getAbsolutePath();
        ImageIO.write(bi, "jpg", outputfile);

        // transfer to base 64 format.
        byte[] fileByte = null;
        String base64Str = "";
        try {
            File fileNew = new File(absolutePath);
            fileByte = Files.readAllBytes(fileNew.toPath());
           base64Str = "data:image/png;base64," + Base64.encodeBase64String(fileByte);
        } catch (IOException e) {
            //return the false message
            e.printStackTrace();
            responseData.put("success",false);
            responseData.put("message","Website could not be reached.");
            return res.json(responseData);
        }
        //return the corrent value.
        responseData.put("success", true);
        responseData.put("screenshot", base64Str);
        return res.json(responseData);
    }
