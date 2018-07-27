package com.cmcc.library.wrapper.retrofit.model;

/**
 * 上传文件时，文件的信息
 * Created by ding on 9/26/16.
 */
public class CMUploadFileInfo {

    public String filePath;

    public String fileKey;

    public CMUploadFileInfo(String filePath, String fileKey) {

        this.filePath = filePath;
        this.fileKey = fileKey;
    }


}
