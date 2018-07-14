package com.fanap.podchat.model;

public class SdkFile {
    private String originalName;
    private int size;
    private String mimeType;
    private FileMetaDataContent file;

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

    public FileMetaDataContent getFile() {
        return file;
    }

    public void setFile(FileMetaDataContent file) {
        this.file = file;
    }
}
