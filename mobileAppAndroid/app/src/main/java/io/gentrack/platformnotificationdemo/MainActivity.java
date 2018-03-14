package io.gentrack.platformnotificationdemo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView background = findViewById(R.id.main_background);
        background.setImageBitmap(createFullScreenBackgroundImage());
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


    private void showRemindMe(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog remindMe = builder.create();
        remindMe.show();
    }
}
