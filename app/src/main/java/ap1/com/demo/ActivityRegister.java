package ap1.com.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONObject;
import org.shaded.apache.http.NameValuePair;
import org.shaded.apache.http.client.entity.UrlEncodedFormEntity;
import org.shaded.apache.http.client.methods.HttpPost;
import org.shaded.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class ActivityRegister extends ActionBarActivity implements RestTask.ResponseCallback{

    EditText et_email_reg;
    EditText et_fname_reg;
    EditText et_lname_reg;
    EditText et_password_reg;
    RadioGroup rg_gender_reg;
    String gender;
    Button btn_register_reg;

    String url_register = "http://sto.apengage.io/filemaker/login.php";

    String restTaskResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email_reg = (EditText) findViewById(R.id.et_email_reg);
        et_fname_reg = (EditText) findViewById(R.id.et_fname_reg);
        et_lname_reg = (EditText) findViewById(R.id.et_lname_reg);
        et_password_reg = (EditText) findViewById(R.id.et_password_reg);
        rg_gender_reg = (RadioGroup) findViewById(R.id.rg_gender_reg);
        btn_register_reg = (Button) findViewById(R.id.btn_register_reg);

        rg_gender_reg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                gender = ((RadioButton) ActivityRegister.this.findViewById(group.getCheckedRadioButtonId())).getText().toString();
                Log.e("gender: ", gender);
            }
        });

        btn_register_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerByForm();
            }
        });
    }

    private void registerByForm(){
        HttpPost httpPost = new HttpPost(url_register);
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("registration", "form"));
        params.add(new BasicNameValuePair("gender", gender));
        params.add(new BasicNameValuePair("email", et_email_reg.getText().toString()));
        params.add(new BasicNameValuePair("fName", et_fname_reg.getText().toString()));
        params.add(new BasicNameValuePair("lName", et_lname_reg.getText().toString()));
        params.add(new BasicNameValuePair("password", et_password_reg.getText().toString()));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        }catch (Exception e){}

        RestTask rt_formByReg = new RestTask();
        rt_formByReg.setResponseCallback(ActivityRegister.this);
        rt_formByReg.execute(httpPost);

        try{
            Log.e("rt arrive here", "");
            String response_regByForm = (String) rt_formByReg.get();
            Log.e("regform result", response_regByForm);
            JSONObject response_regByForm_json = new JSONObject(response_regByForm);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(String response)
    {
        this.restTaskResponse = response;
    }

    @Override
    public void onRequestError(Exception error) {
        error.printStackTrace();
    }
}
