package hinapolina.com.sharelocation.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import hinapolina.com.sharelocation.R;

public class UberLoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitle("Uber Login");
        initUI();
        onClickItems();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI(){
        btnLogin = (Button) findViewById(R.id.btn_uber_login);
        btnSignUp = (Button) findViewById(R.id.btn_uber_sign_in);
    }

    private void onClickItems(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               WebView webView = (WebView) findViewById(R.id.webView1);
//                webView.getSettings().setJavaScriptEnabled(true);
//                webView.loadUrl("http://www.google.com");

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://auth.uber.com/login/?next_url=https%3A%2F%2Friders.uber.com%2F&state=1Tr08Eva0CK1NPQKQjXjG1QNlPH1xTP_9Tvn7w5xo_k%3D"));
                startActivity(browserIntent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://auth.uber.com/login/?next_url=https%3A%2F%2Fpartners.uber.com"));
                startActivity(browserIntent);
            }
        });
    }

}
