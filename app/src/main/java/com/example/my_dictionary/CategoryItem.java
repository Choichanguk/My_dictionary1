package com.example.my_dictionary;

public class CategoryItem {
    private String category_name;
    private String category_index;
    private String num_word;
    private String isShared;
    private String download_category_index;

    public String getDownload_category_index() {
        return this.download_category_index;
    }

    public void setDownload_category_index(String download_category_index) {
        this.download_category_index = download_category_index;
    }

    public String getIsShared() {
        return this.isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public String getNum_word() {
        return this.num_word;
    }

    public void setNum_word(String num_word) {
        this.num_word = num_word;
    }

    public String getCategory_index() {
        return this.category_index;
    }

    public void setCategory_index(String category_index) {
        this.category_index = category_index;
    }



    public String getCategory_name() {
        return this.category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
