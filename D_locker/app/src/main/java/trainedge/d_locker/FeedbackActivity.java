package trainedge.d_locker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText erfeed, ermail;
    int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        erfeed = (EditText) findViewById(R.id.er_feed);
        ermail = (EditText) findViewById(R.id.er_mail);
        Button submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(this);
        ermail.addTextChangedListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.submit) {
            submitf();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String email = s.toString();
        if (email.isEmpty() || email.length() < 10 || !email.contains("@") || !email.contains(".com")) {
            ermail.setError("Please give a correct email address.");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void submitf() {
        String str1 = erfeed.getText().toString();
        String str2 = ermail.getText().toString();

        if (str1.isEmpty()) {
            erfeed.setError("Required");
            return;
        }
        if (!str2.contains("@") || str2.isEmpty() || str2.length() < 10) {
            ermail.setError("Please enter a valid email address.");
            return;
        }
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "shwetatiwari20aug.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback info");
        intent.putExtra(Intent.EXTRA_TEXT, str1);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }else{
                Toast.makeText(this,"NO APP FOUND",Toast.LENGTH_SHORT).show();
        }
    }

}