package com.linyi;

import com.linyi.user.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-16 23:21
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAsync
public class DataGenerate {
    @Autowired
    private LoginService loginService;

    @Test
    public void generateUser() throws IOException {
        String file = "D:\\code\\DiTing\\diting-server\\src\\test\\java\\com\\linyi\\user.csv";
// 指定字符编码
        Charset charset = StandardCharsets.UTF_8;
// 指定缓存
        int bufferSize = 5 * 1024 * 1024;
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), charset), bufferSize);
        String template = "%s,%s,%s";
        String head = String.format(template, "token", "uid", "target_id");
        writer.write(head);
        writer.newLine();
        for(int i=1;i<=1000;i++){
            String token = loginService.login((long) i);
            for (int j=1;j<=1000;j++){
                String line = String.format(template, token, i, j);
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
