package fri.picture.robot_picture.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64Util {

    public static Boolean uploadImg(String baseCode, String imgPath, String imgName) {
        boolean flag = false;
        byte[] bs = new byte[1024];
        bs = Base64.getMimeDecoder().decode(baseCode);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        file = new File(imgPath);
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(imgPath + imgName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bs);
            flag = true;
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return flag;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


