package com.firstapp.gentle.rip;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashBoard extends AppCompatActivity
{
    ListView lvDashBoard;
    AlertDialog alertDialog;
    static int lastListItmPos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SQLiteDatabase sdb = openOrCreateDatabase("ripdb", Context.MODE_PRIVATE, null);
        sdb.execSQL("create table if not exists idstable(host varchar(50), uid varchar(100), pwd varchar(50))");

/*
                ---Deprecate piece ofcode---
        Button btnChange = (Button) findViewById(R.id.btnChange);
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePass();
                }
            });
*/
        Button btnAddAcc = (Button) findViewById(R.id.btnAddAcc);
            btnAddAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAccount();
                }
            });

        lvDashBoard = (ListView) findViewById(R.id.lvDashBoard);
        lvDashBoard.setAdapter(new AccountAdapter(this));

        lvDashBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == DashBoard.lastListItmPos)
                {//last item selected
                    DashBoard.this.addAccount();
                }
                else
                {
                    View llSecret = view.findViewById(R.id.llSecret);
                    View tvTouch = view.findViewById(R.id.tvTouch);
                    if (tvTouch.getVisibility() == View.GONE) {
                        tvTouch.setVisibility(View.VISIBLE);
                        llSecret.setVisibility(View.GONE);
                    }
                    else
                    {
                        llSecret.setVisibility(View.VISIBLE);
                        tvTouch.setVisibility(View.GONE);
                    }
                }
            }
        });

        registerForContextMenu(lvDashBoard);
    }

    void toggleKeypad(int flag)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (flag)
        {
            case 0:
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                break;
            case 1:
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showPswdAlert();
        toggleKeypad(1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lvDashBoard.setVisibility(View.INVISIBLE);
    }

    private boolean validUser(String pass)
    {
        SharedPreferences spf = getSharedPreferences("ripspf", Context.MODE_PRIVATE);
        if(spf.getString("ownerpass", "0000").equals(pass))
            return true;
        else
            return false;
    }
    private void showPswdAlert()
    {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setIcon(R.drawable.login);
        adb.setTitle("Login");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText etPass = new EditText(this);
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPass.setHint("Enter password");
        layout.addView(etPass);

        TextView tvPrompt = new TextView(this);
        tvPrompt.setText("*default is 0000");
        layout.addView(tvPrompt);

        Button btnOk = new Button(this);
        btnOk.setText("Ok");
        layout.addView(btnOk);
        adb.setView(layout);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validUser(etPass.getText().toString()))
                {//go to dashboard
                    lvDashBoard.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                    toggleKeypad(0);
                }
                else
                {
                    Toast.makeText(DashBoard.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog = adb.show();
    }
//---------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.cPass)
            changePass();
        else if(item.getItemId() == R.id.info)
            startActivity(new Intent(this, InfoActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if(info.position == lastListItmPos)
        {/*do nothing*/}
        else
        {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId() == R.id.delete)
        {//delete selected
            TextView tv = (TextView) info.targetView.findViewById(R.id.tvRowId);
            alertDelete(tv.getText().toString());
        }
        else if(item.getItemId() == R.id.edit)
        {//delete selected
            TextView tvHost = (TextView) info.targetView.findViewById(R.id.tvHeading);
            TextView tvId = (TextView) info.targetView.findViewById(R.id.tvUid);
            TextView tvPwd = (TextView) info.targetView.findViewById(R.id.tvPwd);
            TextView tv = (TextView) info.targetView.findViewById(R.id.tvRowId);
            alertEdit(tvHost.getText(), tvId.getText(), tvPwd.getText(), tv.getText());
        }
        //update: make common fn for cpyid & cpypwd
        else if(item.getItemId() == R.id.cpyId)
        {
            TextView tvId = (TextView) info.targetView.findViewById(R.id.tvUid);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("id", tvId.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "id copied", Toast.LENGTH_SHORT).show();
        }
        else
        {
            TextView tvPwd = (TextView) info.targetView.findViewById(R.id.tvPwd);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", tvPwd.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "password copied", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }
//---------------
    private void alertEdit(CharSequence host, CharSequence uid, CharSequence pwd, final CharSequence rowid)
    {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //adb.setIcon(R.drawable.addaccount);
        adb.setTitle("Edit Account");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText  etHost = new EditText(this);
        etHost.setHint("Enter host name:(eg- Facebook, Gmail,.. etc): ");
        etHost.setText(host);
        layout.addView(etHost);

        final EditText  etUid = new EditText(this);
        etUid.setHint("Enter user-id: ");
        etUid.setText(uid);
        layout.addView(etUid);

        final EditText  etPass = new EditText(this);
        etPass.setHint("password");
        //etNewPass2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        etPass.setText(pwd);
        layout.addView(etPass);

        adb.setView(layout);

        adb.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SQLiteDatabase sdb = openOrCreateDatabase("ripdb", Context.MODE_PRIVATE, null);

                ContentValues values = new ContentValues();
                values.put("host", etHost.getText().toString().trim());
                values.put("uid", etUid.getText().toString().trim());
                values.put("pwd", etPass.getText().toString().trim());
                //long res = sdb.insert("idstable", null, values);
                int res = sdb.update("idstable", values, "rowid=?", new String[]{(String) rowid});
                if(res > 0)
                {
                    Toast.makeText(DashBoard.this, "1 item changed", Toast.LENGTH_SHORT).show();
                    lvDashBoard.setAdapter(new AccountAdapter(DashBoard.this));
                }
                else
                    Toast.makeText(DashBoard.this, "somethig is wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        adb.show();
    }
//---------------

    private void alertDelete(final String rowId)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.delete);
        adb.setTitle("Delete");
        adb.setMessage("Do you want to delete???");
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getApplicationContext(), "I chose to delete..", Toast.LENGTH_SHORT).show();
                deleteAccount(rowId);
            }
        });

        adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        adb.show();
    }

    void deleteAccount(String rowid)
    {
        SQLiteDatabase sdb = openOrCreateDatabase("ripdb", Context.MODE_PRIVATE, null);
        int res = sdb.delete("idstable", "rowid=?", new String[]{rowid});
        if(res <= 0)
            Toast.makeText(getApplicationContext(), "No items deleted", Toast.LENGTH_SHORT).show();
        else
            lvDashBoard.setAdapter(new AccountAdapter(this));
    }
//---------------

    void addAccount()
    {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.addacc32);
        adb.setTitle("Add Account");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText  etHost = new EditText(this);
        etHost.setHint("Enter host name:(eg- Facebook, Gmail,.. etc): ");
        layout.addView(etHost);

        final EditText  etUid = new EditText(this);
        etUid.setHint("Enter user-id: ");
        layout.addView(etUid);

        final EditText  etPass = new EditText(this);
        etPass.setHint("password");
        //etNewPass2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        layout.addView(etPass);

        adb.setView(layout);

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SQLiteDatabase sdb = openOrCreateDatabase("ripdb", Context.MODE_PRIVATE, null);

                ContentValues values = new ContentValues();
                values.put("host", etHost.getText().toString().trim());
                values.put("uid", etUid.getText().toString().trim());
                values.put("pwd", etPass.getText().toString().trim());
                long res = sdb.insert("idstable", null, values);
                if(res > 0)
                {
                    Toast.makeText(DashBoard.this, "1 item added ", Toast.LENGTH_SHORT).show();
                    lvDashBoard.setAdapter(new AccountAdapter(DashBoard.this));
                    toggleKeypad(0);//hide
                }
                else
                    Toast.makeText(DashBoard.this, "somethig is wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleKeypad(0);
            }
        });
        adb.show();
        toggleKeypad(1);
    }
//---------------

    void changePass()
    {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.changepass);
        adb.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText  etOldPass = new EditText(this);
        etOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etOldPass.setHint("Enter old password");
        layout.addView(etOldPass);

        final EditText  etNewPass1 = new EditText(this);
        etNewPass1.setHint("Enter new password");
        etNewPass1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass1);

        final EditText  etNewPass2 = new EditText(this);
        etNewPass2.setHint("Reenter new password");
        etNewPass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass2);

        adb.setView(layout);

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SharedPreferences spf = getSharedPreferences("ripspf", Context.MODE_PRIVATE);
                SharedPreferences.Editor spfe = spf.edit();

                String oldpwd = etOldPass.getText().toString();
                String newpwd = etNewPass1.getText().toString();

                if(oldpwd.equals(spf.getString("ownerpass", "0000")) && newpwd.equals(etNewPass2.getText().toString())) {
                    spfe = spf.edit();
                    spfe.putString("ownerpass", newpwd);
                    spfe.commit();

                    Toast.makeText(DashBoard.this, "password changed", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(DashBoard.this, "password NOT changed", Toast.LENGTH_LONG).show();

                toggleKeypad(0);//hide
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleKeypad(0);
            }
        });
        adb.show();
        toggleKeypad(1);
    }
//---------------
//---------------
/*
    //copied from internet
@Override
public void onConfigurationChanged(Configuration newConfig)
{
    super.onConfigurationChanged(newConfig);

    // Checks the orientation of the screen
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    {
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        if(alertDialog != null)
            alertDialog.dismiss();
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        if(alertDialog != null)
            alertDialog.dismiss();
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
    }
    // Checks whether a hardware keyboard is available
    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
        Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
    } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
        Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
    }
}
*/
}
