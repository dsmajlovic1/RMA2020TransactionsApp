package ba.unsa.etf.rma.rma2020_16570.Budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.View.IAccount;

public class BudgetFragment extends Fragment {
    private EditText budgetEditText;
    private EditText monthLimitEditText;
    private EditText totalLimitEditText;
    private Button saveButton;

    private IBudgetPresenter budgetPresenter;
    public IBudgetPresenter getBudgetPresenter(){
        if(budgetPresenter==null){
            budgetPresenter = new BudgetPresenter();
        }
        return budgetPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.fragment_budget, container, false);

        //Get resources
        budgetEditText = (EditText)fragmentView.findViewById(R.id.budgetEditText);
        monthLimitEditText = (EditText)fragmentView.findViewById(R.id.monthLimitEditText);
        totalLimitEditText = (EditText)fragmentView.findViewById(R.id.totalLimitEditText);
        saveButton = (Button) fragmentView.findViewById(R.id.saveBudgetButton);

        if(getArguments()!= null && getArguments().containsKey("account")){
            Account account = getArguments().getParcelable("account");

            //Set values
            budgetEditText.setText(account.getBudget().toString());
            monthLimitEditText.setText(account.getMonthLimit().toString());
            totalLimitEditText.setText(account.getTotalLimit().toString());

        }

        saveButton.setOnClickListener(saveOnClickListener);
        return fragmentView;
    }
    /*@Override
    public void onPause(){
        super.onPause();
        IAccount accountInfo = (IAccount) getActivity();
        accountInfo.setBudget(Double.valueOf(budgetEditText.getText().toString()));
        accountInfo.setTotalLimit(Double.valueOf(totalLimitEditText.getText().toString()));
        accountInfo.setMonthLimit(Double.valueOf(monthLimitEditText.getText().toString()));
    }*/

    private View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IAccount accountInfo = (IAccount) getActivity();
            accountInfo.setBudget(Double.valueOf(budgetEditText.getText().toString()));
            accountInfo.setTotalLimit(Double.valueOf(totalLimitEditText.getText().toString()));
            accountInfo.setMonthLimit(Double.valueOf(monthLimitEditText.getText().toString()));
        }
    };

    //onDestroyView spasavanje promjena
}
