package ua.com.vg.scanervg.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import ua.com.vg.scanervg.R;
public class DocContentEdit extends Activity implements View.OnClickListener{

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    Button b6;
    Button b7;
    Button b8;
    Button b9;
    Button b0;
    Button bDot;
    Button btnOk;
    ImageButton imgBtnDel;
    ImageButton imgBtnDeleteContentRow;
    TextView tvDocContentEntName;
    TextView tvDocContentQty;
    String strQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_content_edit);
        this.setFinishOnTouchOutside(false);
        strQty = "0";

        tvDocContentEntName = (TextView) findViewById(R.id.tvDocContentEntName);
        tvDocContentQty = (TextView) findViewById(R.id.tvDocContentQty);

        Intent intent = getIntent();
        strQty = String.valueOf(intent.getDoubleExtra("QTY",0.00));
        tvDocContentEntName.setText(intent.getStringExtra("ENTNAME"));

        b0 = (Button) findViewById(R.id.b0);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        b5 = (Button) findViewById(R.id.b5);
        b6 = (Button) findViewById(R.id.b6);
        b7 = (Button) findViewById(R.id.b7);
        b8 = (Button) findViewById(R.id.b8);
        b9 = (Button) findViewById(R.id.b9);
        bDot = (Button) findViewById(R.id.bDot);
        btnOk = (Button) findViewById(R.id.btnOk);
        imgBtnDel = (ImageButton) findViewById(R.id.imgBtnDelValue);
        imgBtnDeleteContentRow = (ImageButton) findViewById(R.id.imgBtnDeleteContentRow);


        imgBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder(strQty);
                if(sb.length() > 0){
                    sb.setLength(sb.length() - 1);
                    strQty = sb.toString();
                    recalcTextView();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent();
                if(strQty.length() == 0){
                    strQty = "0.00";
                }
                intentResult.putExtra("QTY",strQty);
                setResult(RESULT_OK,intentResult);
                finish();
            }
        });

        imgBtnDeleteContentRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });

        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        bDot.setOnClickListener(this);

        recalcTextView();
    }

    @Override
    public void onClick(View v) {
        StringBuilder sb = new StringBuilder(strQty);
        switch (v.getId()){
            case R.id.b0:
                sb.append('0');
                break;
            case R.id.b1:
                sb.append('1');
                break;
            case R.id.b2:
                sb.append('2');
                break;
            case R.id.b3:
                sb.append('3');
                break;
            case R.id.b4:
                sb.append('4');
                break;
            case R.id.b5:
                sb.append('5');
                break;
            case R.id.b6:
                sb.append('6');
                break;
            case R.id.b7:
                sb.append('7');
                break;
            case R.id.b8:
                sb.append('8');
                break;
            case R.id.b9:
                sb.append('9');
                break;
            case R.id.bDot:
                if(sb.indexOf(".") < 0 && sb.length() > 0){
                    sb.append('.');
                }
                break;
        }
        strQty = sb.toString();
        recalcTextView();
    }

    private void recalcTextView(){
        tvDocContentQty.setText(strQty);
    }
}
