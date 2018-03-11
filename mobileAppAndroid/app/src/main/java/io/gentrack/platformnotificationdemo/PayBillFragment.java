package io.gentrack.platformnotificationdemo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_bill, container, false);
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

        TextView dueAmountText = view.findViewById(R.id.pay_bill_due_amount);

        String sign = "$";
        if (currency == "GBP") {
            sign = "\u00a3";
        }
        String description = String.format("<small>Due Amount: </small>%s<b>%s</b>", sign, dueAmount);
        dueAmountText.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
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
