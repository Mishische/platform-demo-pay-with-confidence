package io.gentrack.platformnotificationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class PayBillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);

        Toolbar toolbar = findViewById(R.id.pay_bill_toolbar);
        setSupportActionBar(toolbar);

    }

}
