package io.gentrack.platformnotificationdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView background = findViewById(R.id.main_background);
        background.setImageBitmap(createFullScreenBackgroundImage());

        FloatingActionButton myFab = this.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BillReadyActivity.class);
                intent.putExtra("accountId", "1123456");
                startActivity(intent);
            }
        });
    }

    private Bitmap createFullScreenBackgroundImage() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Bitmap originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_brand);
        Bitmap scaledImage = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);

        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        Canvas canvas = new Canvas(scaledImage);

        float scaleX = size.x / originalWidth;
        float scaleY = size.y / originalHeight;

        float scale = Math.max(scaleX, scaleY);
        float xTranslation = -(scale * originalWidth - size.x) / 2.0f;
        float yTranslation = -(scale * originalHeight - size.y) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);

        return scaledImage;
    }
}
