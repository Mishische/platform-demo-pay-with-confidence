package io.gentrack.platformnotificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.taplytics.sdk.TLGcmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyTLBroadcastReceiver extends TLGcmBroadcastReceiver {
    public static final String ACTION_REMIND = "io.gentrack.platformnotificationdemo.ACTION_REMIND";
    public static final String ACTION_PAY_BILL = "io.gentrack.platformnotificationdemo.ACTION_PAY_BILL";
    public static final String ACTION_VIEW_BILL = "io.gentrack.platformnotificationdemo.ACTION_VIEW_BILL";
    public static final int NOTIFICATION_ID = 1010;

    @Override
    public void pushOpened(Context context, Intent intent) {
        //A user clicked on the notification!
    }

    @Override
    public void pushDismissed(Context context, Intent intent) {
        //The push has been dismissed :(
    }

    @Override
    public void pushReceived(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String custom_keys = bundle.get("custom_keys").toString();
            JSONObject payload = new JSONObject(custom_keys);
            Notification notification = createNotification(context, intent, payload);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to notify: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Notification createNotification(Context context, Intent intent, JSONObject payload) throws JSONException, ParseException {
        final String dueAmount = payload.getString("dueAmount");
        final String accountName = payload.getString("accountName");

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        final Date dueDate = dateFormat.parse(payload.getString("dueDate"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.DATE, -1);
        final Date remindDate = cal.getTime();

        final String dueDay = weekDayFormat.format(dueDate);
        final String remindDay = weekDayFormat.format(remindDate);
        final String dueBy = " due by ";

        SpannableStringBuilder subjectSpans = new SpannableStringBuilder();
        SpannableString amountSpan = new SpannableString(dueAmount);
        amountSpan.setSpan(new RelativeSizeSpan(1.5f), 0, dueAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString dueBySpan = new SpannableString(dueBy);
        dueBySpan.setSpan(new StyleSpan(Typeface.ITALIC), 0, dueBy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString daySpan = new SpannableString(dueDay);
        daySpan.setSpan(new RelativeSizeSpan(1.5f), 0, dueDay.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        subjectSpans.append("$");
        subjectSpans.append(amountSpan);
        subjectSpans.append(dueBySpan);
        subjectSpans.append(daySpan);

        SpannableStringBuilder remindLabelSpans = new SpannableStringBuilder();
        SpannableString remindDaySpan = new SpannableString(remindDay);
        remindDaySpan.setSpan(new StyleSpan(Typeface.ITALIC), 0, remindDay.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        remindLabelSpans.append("Remind me on\n");
        remindLabelSpans.append(remindDaySpan);

        final int requestID = (int) System.currentTimeMillis();
        final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent payIntent = new Intent(context, PayBillActivity.class);
        payIntent.putExtras(intent);
        payIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        payIntent.setAction(MyTLBroadcastReceiver.ACTION_PAY_BILL);
        PendingIntent payPendingIntent = PendingIntent.getActivity(context, requestID, payIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent remindIntent = new Intent(context, RemindMeActivity.class);
        remindIntent.putExtra("remindDay", remindDay);
        remindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        remindIntent.setAction(MyTLBroadcastReceiver.ACTION_REMIND);
        PendingIntent remindPendingIntent = PendingIntent.getActivity(context, requestID, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent viewIntent = new Intent(context, BillReadyActivity.class);
        viewIntent.putExtras(intent);
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        viewIntent.setAction(MyTLBroadcastReceiver.ACTION_VIEW_BILL);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, requestID, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews customViewBig = new RemoteViews(context.getPackageName(), R.layout.view_notification_big);
        customViewBig.setTextViewText(R.id.notification_subject, subjectSpans);
        customViewBig.setTextViewText(R.id.remind_button, remindLabelSpans);

        customViewBig.setOnClickPendingIntent(R.id.pay_button, payPendingIntent);
        customViewBig.setOnClickPendingIntent(R.id.remind_button, remindPendingIntent);
        customViewBig.setOnClickPendingIntent(R.id.view_button, viewPendingIntent);

        RemoteViews customViewSmall = new RemoteViews(context.getPackageName(), R.layout.view_notification_small);
        customViewSmall.setTextViewText(R.id.notification_subject, subjectSpans);
        customViewSmall.setTextViewText(R.id.remind_button, remindLabelSpans);

        customViewSmall.setOnClickPendingIntent(R.id.pay_button, payPendingIntent);
        customViewSmall.setOnClickPendingIntent(R.id.remind_button, remindPendingIntent);
        customViewSmall.setOnClickPendingIntent(R.id.view_button, viewPendingIntent);

        return new Notification.Builder(context)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_large))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setSound(sound)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLights(Color.BLUE, 5000, 5000)
                .setCustomContentView(customViewSmall)
                .setCustomBigContentView(customViewBig)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .build();
    }
}
