package io.gentrack.platformnotificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.taplytics.sdk.TLGcmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class MyTLBroadcastReceiver extends TLGcmBroadcastReceiver {
    public static final String ACTION_PAY_BILL = "io.gentrack.platformnotificationdemo.ACTION_PAY_BILL";
    public static final String ACTION_VIEW_BILL = "io.gentrack.platformnotificationdemo.ACTION_VIEW_BILL";
    public static final int NOTIFICATION_ID = 1010;

    @Override
    public void pushOpened(Context context, Intent intent) {
        //A user clicked on the notification!
        Intent billReadyIntent = new Intent(context, BillReadyActivity.class);
        billReadyIntent.putExtras(intent);
        context.startActivity(billReadyIntent);
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

    private Notification createNotification(Context context, Intent intent, JSONObject payload) throws JSONException {
        final String dueAmount = payload.getString("dueAmount");
        final String dueDate = payload.getString("dueDate");
        final String accountName = payload.getString("accountName");
        final String title = String.format("Energise Ltd");
        final String subject = String.format("%s, your monthly bill of $%s (inc. GST) is due by %s", accountName, dueAmount, dueDate);
        final int requestID = (int) System.currentTimeMillis();
        final Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent payBillIntent = new Intent(context, PayBillActivity.class);
        payBillIntent.putExtras(intent);
        payBillIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        payBillIntent.setAction(MyTLBroadcastReceiver.ACTION_VIEW_BILL);
        PendingIntent payBillPendingIntent = PendingIntent.getActivity(context, requestID, payBillIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action payBillAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_assignment_black_24dp),
                "Pay Now",
                payBillPendingIntent).build();

        Intent billReadyIntent = new Intent(context, BillReadyActivity.class);
        billReadyIntent.putExtras(intent);
        billReadyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        billReadyIntent.setAction(MyTLBroadcastReceiver.ACTION_PAY_BILL);

        PendingIntent viewBillPendingIntent = PendingIntent.getActivity(context, requestID, billReadyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action viewBillAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_payment_black_24dp),
                "View Bill",
                viewBillPendingIntent).build();

        return new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(subject)
                .setStyle(new Notification.BigTextStyle().bigText(subject))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_brand))
                .setSmallIcon(R.drawable.ic_notification_small_g)
                .setSound(notificationSound)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLights(Color.BLUE, 5000, 5000)
                .addAction(payBillAction)
                .addAction(viewBillAction)
                .build();
    }
}
