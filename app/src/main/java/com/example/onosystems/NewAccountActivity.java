package com.example.onosystems;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

/*
 *   消費者の新規アカウントを作成する画面
 */

public class NewAccountActivity extends AppCompatActivity {

    EditText editName, editPassword1, editPassword2, editMail, editAddress, editTel;
    String name="", mail="", password1="", password2="", address="";
    long tel = 0;
    String responce = "";
    AlertDialog alertDialog;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        editName = findViewById(R.id.name);
        editMail = findViewById(R.id.mail);
        editPassword1 = findViewById(R.id.password1);
        editPassword2 = findViewById(R.id.password2);
        editTel = findViewById(R.id.tel);
        editAddress = findViewById(R.id.address);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // アカウント作成ボタン
        Button createAccountButton2 = findViewById(R.id.createAccountButton2);
        createAccountButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  キーボードを閉じる
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // 入力文字の取得
                name = editName.getText().toString();
                mail = editMail.getText().toString();
                password1 = editPassword1.getText().toString();
                password2 = editPassword2.getText().toString();
                String phone = editTel.getText().toString();
                address = editAddress.getText().toString();

                // 入力文字の判定
                if (!isEmpty(name)) {
                    editName.setError("名前を入力して下さい");
                } else if (!isEmpty(password1)) {
                    editPassword1.setError("パスワードを入力して下さい");
                } else if (!isEmpty(password2)) {
                    editPassword2.setError("パスワードを再入力して下さい");
                } else if (!password1.equals(password2)) {
                    editPassword2.setError("パスワードが一致しません");
                } else if (!isEmpty(mail)) {
                    editMail.setError("メールアドレスを入力して下さい");
                } else if (!isEmailValid(mail)) {
                    editMail.setError("@が含まれていません");
                } else if (!isEmpty(phone)) {
                    editTel.setError("電話番号を入力して下さい");
                } else if (!isTelValid(phone)) {
                    editTel.setError("11桁で入力して下さい");
                } else if (!isEmpty(address)) {
                    editAddress.setError("住所を入力して下さい");
                } else {
                    tel = Long.parseLong(phone);
                    createNewAccount(name, mail, tel, address, password1);
                }
            }
        });
    }

    public boolean isEmpty(String text) {
        return !text.equals("");
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isTelValid(String phoneLen) {
        int lengthTel = 11;
        return phoneLen.length() == lengthTel;
    }

    // 新規アカウントの作成
    public void createNewAccount(String name, String mail, long tel, String address, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("mail", mail);
            json.put("tel", tel);
            json.put("address", address);
            json.put("password", password);
            PostAsync postAsync = new PostAsync();
            postAsync.setRef(new PostAsync.Callback() {
                @Override
                public void callback(String accountResult) {
                    try {
                        JSONObject jsonObject = new JSONObject(accountResult);
                        responce = jsonObject.getString("result");
                        checkResult();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            postAsync.execute(PostURL.getRegisterAccountURL(), json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkResult() {
        if (this.responce.equals("ok")) {
            alertDialog = new AlertDialog.Builder(NewAccountActivity.this)
                    .setMessage("アカウントを作成しました")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ログイン画面に戻る
                            finish();
                        }
                    }).show();
        } else {
            alertDialog = new AlertDialog.Builder(NewAccountActivity.this)
                    .setMessage("アカウントの作成に失敗しました" + responce)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }
}