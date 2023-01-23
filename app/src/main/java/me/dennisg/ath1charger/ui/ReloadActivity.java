package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import me.dennisg.ath1charger.R;

public class ReloadActivity extends AppCompatActivity {
    private Button authBtn;
    private Button charge;
    private TextInputEditText keyEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Reload");

        this.keyEditor = (TextInputEditText) findViewById(R.id.enterKeyEdit);
        this.charge = (Button) findViewById(R.id.rl_btn);
        this.authBtn = (Button) findViewById(R.id.checkAuth_btn);

        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = keyEditor.getText().toString();
                if (s.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Ticket's Key!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (s.length() != 8) {
                    Toast.makeText(getApplicationContext(), "Invalid Key!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(ReloadActivity.this, Charger.class);
                Bundle b = new Bundle();

                b.putString("pwd", s.trim());
                i.putExtras(b);

                startActivity(i);
                finish();
            }
        });

        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "This Feature Is Not Available Yet", Toast.LENGTH_SHORT).show();
            }
        });

    }

}