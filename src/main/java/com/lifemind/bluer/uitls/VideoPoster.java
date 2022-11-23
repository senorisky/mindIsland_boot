package com.lifemind.bluer.uitls;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import cn.hutool.core.codec.Base64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

/**
 * @Classname VideoPoster
 * @Description TODO
 * @Date 2022/11/23 21:50
 * @Created by senorisky
 */
public class VideoPoster {
    public static String fetchFrame(String parentPath, String videoPath, String uid) {
        FFmpegFrameGrabber ff = null;
        UUID uuid = UUID.randomUUID();
        File target = new File(parentPath + "\\" + uuid + ".jpg");
        try {
            System.out.println("poster" + parentPath + videoPath);
            ff = new FFmpegFrameGrabber(videoPath);
            ff.start();
            int lenght = ff.getLengthInFrames();
            int i = 0;
            Frame f = null;
            while (i < lenght) {
                // 过滤前5帧，避免出现全黑的图片
                f = ff.grabFrame();
                if ((i > 5) && (f.image != null)) {
                    break;
                }
                i++;
            }
            BufferedImage bi = new Java2DFrameConverter().getBufferedImage(f);
            if (ImageIO.write(bi, "jpg", target)) {
                ff.close();
                ff.stop();
                System.out.println("输出图片成功！");
                return "http://localhost:8081/LifeMind/" + uid + "/" + target.getName();
            } else {
                ff.close();
                ff.stop();
                System.out.println("输出图片失败！");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ff != null) {
                    ff.stop();
                }
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
    }

}
