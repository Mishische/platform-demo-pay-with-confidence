package io.gentrack.platformnotificationdemo;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;

import org.json.JSONException;
import org.json.JSONObject;

public class PayBillFragment extends Fragment implements OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener {
    private static final CardType[] SUPPORTED_CARD_TYPES = {
            CardType.VISA,
            CardType.MASTERCARD,
            CardType.AMEX,
            CardType.DINERS_CLUB,
            CardType.UNIONPAY
    };
    protected CardForm mCardForm;
    private SupportedCardTypesView mSupportedCardTypesView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_bill, container, false);
        mSupportedCardTypesView = view.findViewById(R.id.pay_bill_supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mCardForm = view.findViewById(R.id.pay_bill_card_form);
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .actionLabel(getString(R.string.purchase))
                .setup(getActivity());
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        String dueAmount = "100";
        String currency = "NZD";
        try {
            JSONObject payload = getPayload();
            dueAmount = payload.getString("dueAmount");
            currency = payload.getString("currency");
            if (currency.isEmpty()) {
                currency = "NZD";
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to get payload: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        final String sign = (currency == "GBP") ? "\u00a3" : "$";
        SpannableStringBuilder dueAmountTextSpans = new SpannableStringBuilder();
        SpannableString signSpan = new SpannableString(sign);
        signSpan.setSpan(new RelativeSizeSpan(0.33f), 0, sign.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString amountSpan = new SpannableString(dueAmount);
        amountSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, dueAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dueAmountTextSpans.append(signSpan);
        dueAmountTextSpans.append(amountSpan);

        TextView dueAmountText = view.findViewById(R.id.pay_bill_due_amount);
        dueAmountText.setText(dueAmountTextSpans);
        return view;
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {
        Toast.makeText(getActivity(), R.string.payment_success, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PayBillFragment.this.getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 2000);
    }

    private JSONObject getPayload() throws JSONException, NullPointerException {
        Bundle bundle = getActivity().getIntent().getExtras();
        String custom_keys = bundle.get("custom_keys").toString();
        return new JSONObject(custom_keys);
    }
}
