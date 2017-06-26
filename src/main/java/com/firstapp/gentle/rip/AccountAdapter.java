package com.firstapp.gentle.rip;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gentle on 23-02-2017.
 */
public class AccountAdapter extends BaseAdapter
{
    DashBoard context;
    ArrayList<Accounts> accountList;
    ImageView ivIcon;

    public AccountAdapter(DashBoard dashBoardActivity)
    {
        context = dashBoardActivity;

        SQLiteDatabase sdb = context.openOrCreateDatabase("ripdb", Context.MODE_PRIVATE, null);
        Cursor c = sdb.query("idstable", new String[]{"rowid", "*"}, null, null, null, null, null);
        accountList = new ArrayList<>();

        while(c.moveToNext())
        {
            Accounts account = new Accounts();
            account.setRowId(c.getLong(0));
            account.setHost(c.getString(1));
            account.setId(c.getString(2));
            account.setPwd(c.getString(3));
            accountList.add(account);
        }
    }

    @Override
    public int getCount()
    {
        DashBoard.lastListItmPos = accountList.size();
        return DashBoard.lastListItmPos + 1; //at least one item
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup)
    {
        if(pos == accountList.size())
        {
            view = LayoutInflater.from(context).inflate(R.layout.add_account, null);
        }
        else
        {
            //init view conponents
            view = LayoutInflater.from(context).inflate(R.layout.accounts_indiview, null);
            TextView tvRowId = (TextView) view.findViewById(R.id.tvRowId);
            TextView tvHeading = (TextView) view.findViewById(R.id.tvHeading);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            TextView tvUid = (TextView) view.findViewById(R.id.tvUid);
            TextView tvPwd = (TextView) view.findViewById(R.id.tvPwd);

            //set respective data to view conponents
            String hostName = accountList.get(pos).getHost();
            setIcon(hostName);
            tvHeading.setText(hostName);
            tvRowId.setText(String.valueOf(accountList.get(pos).getRowId()));
            tvUid.setText(accountList.get(pos).getUid());
            tvPwd.setText(accountList.get(pos).getPwd());

        }
        return view;
    }

    private void setIcon(String hostName)
    {
        if(hostName.toLowerCase().contains("gmail"))
            ivIcon.setImageResource(R.drawable.gmail32);
        else if(hostName.toLowerCase().contains("yahoo"))
            ivIcon.setImageResource(R.drawable.yahoo32);
        else if(hostName.toLowerCase().contains("facebook"))
            ivIcon.setImageResource(R.drawable.facebook32);
        else if(hostName.toLowerCase().contains("twitter"))
            ivIcon.setImageResource(R.drawable.twitter32);
        else if(hostName.toLowerCase().contains("google"))
            ivIcon.setImageResource(R.drawable.google32);
        else if(hostName.toLowerCase().contains("linkedin"))
            ivIcon.setImageResource(R.drawable.linkedin32);
        else if(hostName.toLowerCase().contains("youtube"))
            ivIcon.setImageResource(R.drawable.youtube32);
/*
        switch (hostName.toLowerCase())
        {
            case "facebook":
                ivIcon.setImageResource(R.drawable.facebook32);
                break;
            case "gmail":
                ivIcon.setImageResource(R.drawable.gmail32);
                break;
            case "twitter":
                ivIcon.setImageResource(R.drawable.twitter32);
                break;
            case "youtube":
                ivIcon.setImageResource(R.drawable.youtube32);
                break;
            case "linkedin":
                ivIcon.setImageResource(R.drawable.linkedin32);
                break;
            default:
                if(hostName.toLowerCase().contains("google")) {
                    ivIcon.setImageResource(R.drawable.google32);
                }
                break;
        }
*/
    }
}
