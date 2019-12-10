package pnj.ac.id.rifqi_afif;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toRegisterActivity(View v)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    public void toLoginActivity(View v)
    {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
