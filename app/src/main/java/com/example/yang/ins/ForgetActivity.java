package com.example.yang.ins;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnFocusChangeListener;

import com.example.yang.ins.Utils.HelloHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

public class ForgetActivity extends AppCompatActivity {

    private ImageButton ib_back,ib_go;
    private EditText et_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ib_back = (ImageButton) findViewById(R.id.ib_forget_back);
        ib_go = (ImageButton) findViewById(R.id.ib_forget_go);
        et_email = (EditText) findViewById(R.id.et_email);
        Drawable db_email=getResources().getDrawable(R.drawable.email);
        db_email.setBounds(0,0,85,85);
        et_email.setCompoundDrawables(db_email,null,null,null);
        //et_email.addTextChangedListener(new JumpTextWatcher(et_email, et_code));
        ib_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = et_email.getText().toString();
                if (email.length() <= 0 || email == null) {
                    showToast("您未填写邮箱");
                    return;
                }
                if (!isEmail(email)) {
                    showToast("邮箱格式非法，请检查");
                    return;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("account", email);
                HelloHttp.sendFirstPostRequest("api/user/checkout", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("LoginActivity", "FAILURE");
                        Looper.prepare();
                        showToast("服务器错误");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("ForgetActivity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(result.equals("NotExist")) {
                            Looper.prepare();
                            showToast("账号不存在");
                            Looper.loop();
                        }
                        else if(result.equals("Exist")) {
                            Intent intent = new Intent(ForgetActivity.this, Forget2Activity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                        else {
                            Looper.prepare();
                            showToast(result);
                            Looper.loop();
                        }
                    }
                });
            }
        });

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /*private class JumpTextWatcher implements TextWatcher {
        private EditText mThisView = null;
        private View mNextView = null;

        public JumpTextWatcher(EditText vThis, View vNext) {
            super();
            mThisView = vThis;
            if(vNext != null) {
                mNextView = vNext;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if(str.indexOf("\r") >= 0 || str.indexOf("\n") >= 0) {      //发现输入回车或换行
                mThisView.setText(str.replace("\r", "").replace("\n", ""));
                if(mNextView != null) {
                    mNextView.requestFocus();
                    if(mNextView instanceof EditText) {         //让光标自动移动到编辑框的文本末尾
                        EditText et = (EditText) mNextView;
                        et.setSelection(et.getText().length());
                    }
                }
            }

        }
    }
    */
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public static boolean isEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}

