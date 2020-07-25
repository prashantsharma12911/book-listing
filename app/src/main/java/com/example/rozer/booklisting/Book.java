package com.example.rozer.booklisting;

/**
 * Created by Rozer on 2/1/2020.
 */
public class Book {
    private String title;
    private String[] authors;
    private  String image;
    private String previewLink;

    public Book(String title,String[] authors,String image,String previewLink){
        this.title = title;
        this.authors = authors;
        this.image = image;
        this.previewLink = previewLink;
    }
    public void setImagePath(String path){
        this.image = path;
    }

    public String getTitle(){
        return title;
    }
    public String[] getAuthors(){
        return authors;
    }
    public String getDownloadImagePath(){
        return image;
    }
    public String getPreviewLink(){
        return previewLink;
    }
}
