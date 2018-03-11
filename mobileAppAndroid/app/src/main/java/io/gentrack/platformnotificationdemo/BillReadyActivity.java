package io.gentrack.platformnotificationdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class BillReadyActivity extends AppCompatActivity {

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_ready);
        Toolbar toolbar = findViewById(R.id.bill_ready_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton payNowButton = this.findViewById(R.id.bill_ready_pay_now);
        payNowButton.setImageBitmap(textAsBitmap("Pay", 60, Color.WHITE));
        payNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(BillReadyActivity.this, PayBillActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
            }
        });
    }

}
