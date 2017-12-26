package com.gznb.fastdfs.gznbfastdfstest;

import com.gznb.fastdfs.client.FastDFSClient;
import com.gznb.fastdfs.constants.FDFSConstants;
import org.csource.common.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Map;

@SpringBootApplication
public class GznbFastdfsTestApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(GznbFastdfsTestApplication.class);

    @Autowired
    private FastDFSClient fastDFSClient;

    public static void main(String[] args) {
        SpringApplication.run(GznbFastdfsTestApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        String filePath = testUpload();
        testDownload(filePath);
        testDelete(filePath);
        testDownload(filePath);
    }

    private String testUpload() throws IOException {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        File testFile = new File(path + "/test.xlsx");
        InputStream input = null;
        try {
            input = new FileInputStream(testFile);
        } catch (FileNotFoundException e) {
            logger.error("读取文件异常！" + e);
        }
        byte[] byt = new byte[input.available()];
        input.read(byt);
        String uploadPath = fastDFSClient.uploadFile(byt, "test.xlsx", null);
        logger.info("uploadPath:{}", uploadPath);
        return uploadPath;
    }

    private void testDownload(String filePath) {
        byte[] bytes = null;
        Map<String,String> metaMap = null;
        try {
            // 获取meta信息
          metaMap = fastDFSClient.getFileMetaData(filePath);
            bytes = fastDFSClient.downloadFile(filePath);
            if (bytes == null) {
                logger.error("文件不存在！");
                return;
            }
        } catch (Exception e) {
            logger.error("下载失败！" + e);
        }
        BufferedOutputStream bufferedOutputStream = null;
        try {
            File file = new File("D://" + metaMap.get(FDFSConstants.META_DATA_FILE_NAME));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(bytes);
        } catch (Exception e) {
            logger.error("保存文件失败！" + e);
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    logger.error("关闭输出流失败！" + e);
                }
            }
        }
    }

    private void testDelete(String filePath) {
        try {
            boolean result = fastDFSClient.deleteFile(filePath);
            logger.info("删除文件结果：{}", result);
        } catch (Exception e) {
            logger.error("删除失败！" + e);
        }
    }
}
