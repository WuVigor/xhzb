package com.xhzb.nursing.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

public class PDFUtil {

    public static String pdfToString(InputStream  inputStream) {

        try (InputStream source = inputStream;
             PDDocument document = Loader.loadPDF(source.readAllBytes())) {
            // 加载PDF文档
            // 创建一个PDFTextStripper实例来提取文本
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // 从PDF文档中提取文本
            return pdfStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
