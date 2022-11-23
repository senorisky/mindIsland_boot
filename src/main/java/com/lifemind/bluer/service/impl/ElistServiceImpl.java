package com.lifemind.bluer.service.impl;

import com.lifemind.bluer.entity.Elist;
import com.lifemind.bluer.mapper.ElistMapper;
import com.lifemind.bluer.service.IElistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Service
public class ElistServiceImpl extends ServiceImpl<ElistMapper, Elist> implements IElistService {

    @Override
    public void downResource(String userId, String name, HttpServletResponse response) {
        File file = new File("/LifeMind/" + userId + "/" + name);
        System.out.println(file);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            response.setContentType("video/mp4");
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
