package io.gentrack.platformnotificationdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BillReadyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_ready, container, false);
        try {
            JSONObject payload = getPayload();
            updateDetails(view, payload);
            updateConsumptionChart(view, payload);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to get payload: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private JSONObject getPayload() throws JSONException, NullPointerException {
        Bundle bundle = getActivity().getIntent().getExtras();
        String custom_keys = bundle.get("custom_keys").toString();
        return new JSONObject(custom_keys);
    }

    private void updateDetails(View view, JSONObject payload) throws JSONException {
        TextView accountIDText = view.findViewById(R.id.bill_ready_details_account_id);
        String accountId = payload.getString("accountId");
        accountIDText.setText(String.format("Account %s", accountId));

        String currency = "NZD";
        try {
            currency = payload.getString("currency");
        } catch (Exception e) {
        }
        final String sign = (currency == "GBP") ? "\u00a3" : "$";
        final String dueAmount = payload.getString("dueAmount");
        final String dueDate = payload.getString("dueDate");
        final String dueBy = "Due by ";

        SpannableStringBuilder dueAmountTextSpans = new SpannableStringBuilder();
        SpannableString signSpan = new SpannableString(sign);
        signSpan.setSpan(new RelativeSizeSpan(0.33f), 0, sign.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString amountSpan = new SpannableString(dueAmount);
        amountSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, dueAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dueAmountTextSpans.append(signSpan);
        dueAmountTextSpans.append(amountSpan);

        TextView dueAmountText = view.findViewById(R.id.bill_ready_due_amount);
        dueAmountText.setText(dueAmountTextSpans);

        SpannableStringBuilder dueDateTextSpans = new SpannableStringBuilder();
        SpannableString dueBySpan = new SpannableString(dueBy);
        dueBySpan.setSpan(new StyleSpan(Typeface.ITALIC), 0, dueBy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString dueDateSpan = new SpannableString(dueDate);
        dueDateSpan.setSpan(new RelativeSizeSpan(1.5f), 0, dueDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dueDateTextSpans.append(dueBySpan);
        dueDateTextSpans.append(dueDateSpan);

        TextView dueDateText = view.findViewById(R.id.bill_ready_due_date);
        dueDateText.setText(dueDateTextSpans);
    }

    private void updateConsumptionChart(View view, JSONObject payload) throws JSONException, ParseException {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date balanceDate = df.parse(payload.getString("balanceDate"));
        JSONArray recentConsumptions = payload.getJSONArray("recentConsumptions");
        float range = 0;
        ArrayList <BarEntry> entries = new ArrayList <BarEntry>();
        for (int i = 0; i < recentConsumptions.length(); ++i) {
            String item = recentConsumptions.getString(i);
            float val = Float.parseFloat(item);
            if (i == 0) {
                range = val;
            }
            if (range < val) {
                range = val;
            }
            entries.add(new BarEntry(i, val));
        }

        BarChart chart = view.findViewById(R.id.bill_ready_consumption_chart);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(recentConsumptions.length());
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(entries.size());
        xAxis.setValueFormatter(new XAxisMonthFormatter(balanceDate, entries.size()));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum((int) (Math.ceil(range * 1.2f / 10)) * 10);
        leftAxis.setValueFormatter(new YAxisUsageFormatter());

        chart.getAxisRight().setEnabled(false);

        BarDataSet dataSet;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, "Electricity Monthly Consumption History");
            dataSet.setDrawIcons(false);
            ArrayList <IBarDataSet> dataSets = new ArrayList <IBarDataSet>();
            dataSets.add(dataSet);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(20f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }

    }

    private class XAxisMonthFormatter implements IAxisValueFormatter {

        final private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        private Date mEndDate;
        private int mNumMonth;

        public XAxisMonthFormatter(Date endDate, int numMonth) {
            mEndDate = new Date(endDate.getTime());
            mNumMonth = numMonth;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value < mNumMonth && value >= 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mEndDate);
                cal.add(Calendar.MONTH, -mNumMonth + 1 + Math.round(value));
                return simpleDateFormat.format(cal.getTime());
            }
            return "";
        }
    }

    private class YAxisUsageFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat = new DecimalFormat("#0.0");

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value) + " Kwh";
        }
    }
}
