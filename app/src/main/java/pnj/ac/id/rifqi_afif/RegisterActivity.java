package pnj.ac.id.rifqi_afif;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import pnj.ac.id.rifqi_afif.dashboard.Dashboard;
import pnj.ac.id.rifqi_afif.db.DBHelper;
import pnj.ac.id.rifqi_afif.db.UsersTable;


public class RegisterActivity extends AppCompatActivity
{
    DBHelper dbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper = DBHelper.getInstance(this);


    }
    public void register(View v)
    {
        String username = ((EditText)findViewById(R.id.username_edit)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_edit)).getText().toString();

        if(!username.equals(Globals.EMPTY_STRING) && !password.equals(Globals.EMPTY_STRING))
        {
            UsersTable usersTable = new UsersTable();
            usersTable.setUsername(username);
            usersTable.setPassword(password);

            dbHelper.register(usersTable);
            ((EditText) findViewById(R.id.username_edit)).setText(Globals.EMPTY_STRING);
            ((EditText) findViewById(R.id.password_edit)).setText(Globals.EMPTY_STRING);

            Intent intent = new Intent(this, Dashboard.class);
            intent.putExtra(Globals.USERNAME, username);
            intent.putExtra(Globals.PASSWORD, password);
            startActivity(intent);
        }

    }
}
