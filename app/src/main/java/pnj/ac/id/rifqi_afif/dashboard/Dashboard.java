package pnj.ac.id.rifqi_afif.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

import pnj.ac.id.rifqi_afif.Globals;
import pnj.ac.id.rifqi_afif.MainActivity;
import pnj.ac.id.rifqi_afif.R;
import pnj.ac.id.rifqi_afif.db.DBHelper;
import pnj.ac.id.rifqi_afif.db.PicturesTable;
import pnj.ac.id.rifqi_afif.db.UsersTable;

public class Dashboard extends AppCompatActivity
{
    DBHelper dbHelper = null;
    UsersTable usersTable = null;
    String username = null;
    String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dbHelper = DBHelper.getInstance(this);
        login();
        ((TextView)findViewById(R.id.txt_username_get)).setText(username);



    }

    private void login()
    {
        Intent intent = getIntent();
        username = intent.getStringExtra(Globals.USERNAME);
        password = intent.getStringExtra(Globals.PASSWORD);

        usersTable = dbHelper.login(username, password);
    }

    public void SaveImg(View v)
    {
        Intent intent = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK)
        {
            Uri selectedfile = data.getData(); //The uri with the location of the file

            try
            {
                Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
                byte bitmap[] = Globals.getBitmapAsByteArray(img);

                PicturesTable picturesTable = new PicturesTable();
                picturesTable.setUsername(username);
                picturesTable.setName(Globals.getFileName(this, selectedfile));
                picturesTable.setPicture(bitmap);
                dbHelper.savePicture(picturesTable);
                getAllPictures(null);

            } catch (IOException e) {
                Log.d("IO ERROR", "Cant retrieve image from device");
                e.printStackTrace();
                Toast.makeText(this, "cannot save image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getPictures(List <PicturesTable> pictures)
    {
        TableLayout picture_layout = findViewById(R.id.layout_picture_view);
        picture_layout.removeAllViews();

        LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        text_params.setMargins(0,
                0,
                0,
                Globals.dpToPx(this, 10));

        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );

        for(int x=0; x<pictures.size(); x++)
        {
            final PicturesTable picturesTable = pictures.get(x);
            int id = picturesTable.getId();
            final String name = picturesTable.getName();
            byte bitmap[] = picturesTable.getPicture();
            Bitmap img = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);

            TextView idtext = new TextView(this);
            String idt = getString(R.string.txt_id)+id;
            idtext.setText(idt);
            idtext.setLayoutParams(text_params);

            TextView nametext = new TextView(this);
            String namet = getString(R.string.txt_name)+name;
            nametext.setText(namet);
            nametext.setLayoutParams(text_params);

            ImageView imgview = new ImageView(this);
            imgview.setImageBitmap(img);
            imgview.setAdjustViewBounds(true);
            imgview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgview.setLayoutParams(img_params);
            imgview.setId(id);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(idtext);
            linearLayout.addView(nametext);
            linearLayout.addView(imgview);
            linearLayout.setBackground(getDrawable(R.drawable.border));

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            row.addView(linearLayout);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View linear = ((ViewGroup)v).getChildAt(0);
                    TextView name = (TextView) ((ViewGroup)linear).getChildAt(1);
                    ImageView img = (ImageView) ((ViewGroup)linear).getChildAt(2);
                    final int id = img.getId();
                    ImageView image = new ImageView(Dashboard.this);
                    image.setImageDrawable(img.getDrawable());
                    new AlertDialog.Builder(Dashboard.this)
                            .setTitle(name.getText())
                            .setView(image)
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(R.string.txt_delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dbHelper.deletePicture(id);
                                    getAllPictures(null);
                                }
                            })
                            .setNeutralButton(R.string.txt_update_name, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final EditText update = new EditText(Dashboard.this);
                                    new AlertDialog.Builder(Dashboard.this)
                                            .setTitle(getString(R.string.txt_update_name))
                                            .setView(update)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    PicturesTable picturesTable1 = new PicturesTable();
                                                    picturesTable.setId(id);
                                                    picturesTable.setName(update.getText().toString());
                                                    dbHelper.updatePictureName(picturesTable);
                                                    getAllPictures(null);
                                                }
                                            })
                                            .show();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .show();
                }
            });

            picture_layout.addView(row);
        }
    }

    public void getAllPictures(View v)
    {
        getPictures(dbHelper.getPictures(DBHelper.PICTURES_SELECTION_USERNAME, username));
    }

    public void search(View v)
    {
        String keyword = "%"+((EditText)findViewById(R.id.edit_search)).getText().toString()+"%";
        getPictures(dbHelper.getPictures(DBHelper.PICTURES_SELECTION_NAME, keyword));
    }

    public void deleteAllIMG(View v)
    {
        dbHelper.deletePictures(username);
        getAllPictures(null);
    }

    public void logout(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {

    }
}
