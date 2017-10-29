package hinapolina.com.sharelocation.handler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.activities.fingerprint.FingerPrintActivity;
import hinapolina.com.sharelocation.network.FirebaseHelper;

import static hinapolina.com.sharelocation.R.id.textView;


/**
 * Created by hinaikhan on 10/28/17.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseHelper firebaseHelper;


    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        onAuthenticationFailure("Fingerprint Authentication error\n" + errString);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        onAuthenticationFailure("Fingerprint Authentication help\n" + helpString);
    }


    @Override
    public void onAuthenticationFailed() {
        onAuthenticationFailure("Fingerprint Authentication failed.");
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
       // result.getCryptoObject().getCipher().
        byte[] cipherBytes = result.getCryptoObject().getCipher().getIV();
        Log.d("CharBytesArray", Arrays.toString(cipherBytes));
        FingerPrintActivity  fingerPrintActivity = (FingerPrintActivity) context;
        fingerPrintActivity.onAuthenticationSuccessful(cipherBytes);
    }


    public void onAuthenticationFailure(String msg) {
        FingerPrintActivity  fingerPrintActivity = (FingerPrintActivity) context;
        fingerPrintActivity.onAuthenticationFailure(msg);
    }

}




