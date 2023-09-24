package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import Models.ParentAccount;

public class ParentSignUp extends AppCompatActivity {

    Button signUpBtn;
    TextView logInTV;
    EditText mailETxt,passETxt,passREETxt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String mailStr,passStr;
    Boolean RegistedFlg;
    ParentAccount parentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_sign_up);
        init();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegistedFlg==false) {
                    if (TextUtils.isEmpty(mailETxt.getText().toString())) {
                        mailETxt.setError("Please Enter Your Mail");
                    } else if (TextUtils.isEmpty(passETxt.getText().toString())) {
                        passETxt.setError("Please Set Password");
                    } else if(TextUtils.isEmpty(passREETxt.getText().toString()))
                    {
                        passREETxt.setError("Please Re-Enter The Password");
                    }
                    else if(passETxt.getText().toString().trim().equals(passREETxt.getText().toString().trim())){
                            mailStr = mailETxt.getText().toString().trim();
                            passStr = passETxt.getText().toString().trim();
                            mAuth.createUserWithEmailAndPassword(mailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ParentSignUp.this, "Registed", Toast.LENGTH_SHORT).show();
                                        signUpBtn.setText("Verify");
                                        RegistedFlg = true;
                                    } else {
                                        Toast.makeText(ParentSignUp.this, "Mail is Already Registed", Toast.LENGTH_SHORT).show();
                                        Log.d("shubham",task.getException().toString());
                                    }
                                }
                            });
                    }
                    else{
                        passREETxt.setError("Password Not Match");
                    }
                }
                else if(RegistedFlg==true)
                {
                    ProgressDialog progressDialog = new ProgressDialog(ParentSignUp.this);
                    progressDialog.setTitle("Sending Mail");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.cancel();
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ParentSignUp.this, "Email Send", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ParentSignUp.this);
                                builder.setTitle("Verification Mail Sent");
                                builder.setMessage("Please Verify Your Mail To Log In");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        parentAccount.setMail(mailStr);
                                        setRelation(parentAccount);
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                }
            }
        });
        logInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        }
        void init()
        {
            parentAccount = new ParentAccount();
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            RegistedFlg = false;
            signUpBtn = findViewById(R.id.SignUpBtn);
            mailETxt = findViewById(R.id.SignUpMailETxt);
            passETxt = findViewById(R.id.SignUpPassETxt);
            passREETxt = findViewById(R.id.SignUpREPassETxt);
            logInTV = findViewById(R.id.loginTV);
        }
        void setRelation(ParentAccount parentAccount)
        {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("Mail",parentAccount.getMail());
            hashMap.put("LinkChild",parentAccount.getLinkchild());
            db.collection("Relation").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("tag","Parent mail added to firestore");
            }
        });
        }
    }