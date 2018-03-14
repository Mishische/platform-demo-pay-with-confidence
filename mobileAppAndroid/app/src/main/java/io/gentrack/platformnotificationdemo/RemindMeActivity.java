package io.gentrack.platformnotificationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RemindMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_me);
        this.setFinishOnTouchOutside(false);

        Button doneButton = this.findViewById(R.id.button_remind_me_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RemindMeActivity.this.finish();
            }
        });
    }
}
