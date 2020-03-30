package ba.unsa.etf.rma.rma2020_16570.View;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import ba.unsa.etf.rma.rma2020_16570.R;

public class TextChangedWatcher implements TextWatcher {
    private Context context;
    private EditText parent;

    public TextChangedWatcher(Context context, EditText parent) {
        this.context = context;
        this.parent = parent;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Change background color
        parent.setBackgroundColor(ContextCompat.getColor(context, R.color.textChanged));
    }

    @Override
    public void afterTextChanged(Editable s) {  }
}
