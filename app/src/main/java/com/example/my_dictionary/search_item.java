package com.example.my_dictionary;

public class search_item {
    String search_word;
    String definition;
    String index_word;
    String status;
    boolean isCheck = false;


    public boolean isCheck() {
        return this.isCheck;
    }

    public void setCheck(boolean check) {
        this.isCheck = check;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex_word() {
        return this.index_word;
    }

    public void setIndex_word(String index_word) {
        this.index_word = index_word;
    }


    public String getSearch_word() {
        return this.search_word;
    }

    public void setSearch_word(String search_word) {
        this.search_word = search_word;
    }

    public String getDefinition() {
        return this.definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
