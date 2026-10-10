package ru.kaelth4s.framework.util;

import aquality.selenium.elements.interfaces.IElement;
import ru.kaelth4s.framework.enums.JsScript;

import java.nio.file.Paths;

public final class WebUtil {
    private WebUtil() {
    }

    public static void uploadFile(IElement uploadTrigger, String fileResourcePath) {
        String base64 = FileUtil.readResourceAsBase64(fileResourcePath);
        String fileName = Paths.get(fileResourcePath).getFileName().toString();
        JsUtil.execute(JsScript.UPLOAD_FILE, uploadTrigger.getElement(), base64, fileName);
    }
}