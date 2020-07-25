package com.example.rozer.booklisting;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Rozer on 2/1/2020.
 */
public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, ArrayList<Book> resource) {
        super(context, 0, resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
       Book bookItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_view, parent, false);
        }
       ImageView bookImage = convertView.findViewById(R.id.book_image);
        bookImage.setImageDrawable(Drawable.createFromPath(bookItem.getDownloadImagePath()));
        TextView bookTitle = convertView.findViewById(R.id.book_title);
        bookTitle.setText(bookItem.getTitle());
        String authors[] = bookItem.getAuthors();
        TextView author1 = convertView.findViewById(R.id.author_1);
        TextView author2 = convertView.findViewById(R.id.author_2);
        TextView author3 = convertView.findViewById(R.id.author_3);
        switch (authors.length){
            case 1: author1.setText(authors[0]);
                    author2.setVisibility(View.GONE);
                    author3.setVisibility(View.GONE);
                    break;

            case 2: author1.setText(authors[0]);
                    author2.setText(authors[1]);
                    if(author2.getVisibility() == View.GONE)
                        author2.setVisibility(View.VISIBLE);
                    author3.setVisibility(View.GONE);
                    break;
            case 3:  author1.setText(authors[0]);
                     author2.setText(authors[1]);
                     if(author2.getVisibility() == View.GONE)
                        author2.setVisibility(View.VISIBLE);
                
                    if(author3.getVisibility() == View.GONE)
                         author3.setVisibility(View.VISIBLE);
                     author3.setText(authors[2]);
                     break;
        }

        return convertView;
    }
}
