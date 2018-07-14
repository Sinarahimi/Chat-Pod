package com.fanap.podchat.model;

public class SdkImageFile {
    private String originalName;
    private int size;
    private String mimeType;
    private FileImageMetaData file;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public FileImageMetaData getFile() {
        return file;
    }

    public void setFile(FileImageMetaData file) {
        this.file = file;
    }
}
