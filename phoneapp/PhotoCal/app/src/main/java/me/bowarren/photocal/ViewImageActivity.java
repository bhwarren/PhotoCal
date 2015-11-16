package me.bowarren.photocal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by bhwarren on 11/7/15.
 */
public class ViewImageActivity extends AppCompatActivity {

    ImageView flyerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_fullscreen);

        Bundle b = getIntent().getExtras();
        String path = b.getString("imgPath");

        //flyerView = getLayoutInflater().inflate(R.layout.pic_fullscreen, findViewById(R.layout.pic_fullscreen)).findViewById(R.id.pic_fullscreen_view);
        flyerView = (ImageView) findViewById(R.id.pic_fullscreen_view);
        Bitmap pic = BitmapFactory.decodeFile(path);
        flyerView.setImageBitmap(pic);

    }
}
