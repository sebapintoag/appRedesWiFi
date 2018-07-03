package redescomputacionales.cl.appredeswifi;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 777;

    private View mRunningBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView loginInfo = (TextView) findViewById(R.id.loginInfoId);

        String loginInfoHtml = "<h2>AppRedesWiFi</h2><br><h3>Accede con tu cuenta usach para poder usar la aplicación</h3>";
        loginInfo.setText(Html.fromHtml(loginInfoHtml));

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .setHostedDomain("usach.cl")
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mRunningBarLogin = findViewById(R.id.runningBarLogin);
        mRunningBarLogin.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String action = getIntent().getStringExtra("ACTION");
        if(action.equals("signout"))
        {
            signOut();
        }
        //Verifica si el usuario ha iniciado sesión
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null)
        {
            //Si hay una sesión iniciada, pasa directamente al MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ACTION", "signout");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            //Pasar al MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            mRunningBarLogin.setVisibility(View.GONE);
            finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            mRunningBarLogin.setVisibility(View.GONE);
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Ha ocurrido un error\nCodigo: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        mRunningBarLogin.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}
