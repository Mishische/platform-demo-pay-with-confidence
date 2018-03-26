package io.gentrack.platformnotificationdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RemindMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_me);

        this.setFinishOnTouchOutside(false);

        String remindDay = getIntent().getStringExtra("remindDay");
        SpannableStringBuilder remindMessage = new SpannableStringBuilder();
        SpannableString dueDaySpan = new SpannableString(remindDay);
        dueDaySpan.setSpan(new StyleSpan(Typeface.BOLD), 0, remindDay.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        remindMessage.append("We will remind you again on ");
        remindMessage.append(dueDaySpan);
        remindMessage.append(".");
        TextView remindMessageText = this.findViewById(R.id.remind_message);
        remindMessageText.setText(remindMessage);

        Button doneButton = this.findViewById(R.id.button_remind_me_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RemindMeActivity.this.finish();
            }
        });
    }
}
