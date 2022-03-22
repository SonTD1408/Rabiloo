package com.namtg.egovernment.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class DownloadFileService {

    public XWPFDocument prepareForDownloadFileWord(HttpServletResponse response, String fileName) throws IOException {
        XWPFDocument document = new XWPFDocument();
        response.setContentType("application/msword; charset=UTF-8");
        response.setHeader("Content-disposition", "attachment; fileName=\"" + fileName + "\"");

        document.write(response.getOutputStream());
        return document;
    }
}
