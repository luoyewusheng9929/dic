package com.gzhu.dic_platform.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/video")
public class VideoStreamController {

    @GetMapping("/video-stream")
    public void streamVideo(HttpServletResponse response) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", "rtmp://localhost/live/stream", "-f", "mpegts", "-codec:v", "mpeg1video", "-s", "640x480", "-b:v", "800k", "-r", "30", "pipe:1");
            Process process = pb.start();

            response.setContentType("video/mp2t");
            OutputStream out = response.getOutputStream();
            InputStream in = process.getInputStream();

            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            out.flush();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
