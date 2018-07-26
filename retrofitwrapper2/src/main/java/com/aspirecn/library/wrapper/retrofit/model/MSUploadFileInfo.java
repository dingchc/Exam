package com.aspirecn.library.wrapper.retrofit.model;

/**
 * 上传文件时，文件的信息
 * Created by ding on 9/26/16.
 */
public class MSUploadFileInfo {

    public String filePath;

    public String fileKey;

    public MSUploadFileInfo(String filePath, String fileKey) {

        this.filePath = filePath;
        this.fileKey = fileKey;
    }


}
