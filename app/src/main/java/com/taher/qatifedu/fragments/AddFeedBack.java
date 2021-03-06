package com.taher.qatifedu.fragments;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Company_Entity;
import com.taher.qatifedu.entity.Section_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.HttpUtils;
import com.taher.qatifedu.utility.NothingSelectedSpinnerAdapter;
import com.taher.qatifedu.utility.Utility;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import static com.taher.qatifedu.MainActivity.alCompaniesMainDataList;

public class AddFeedBack extends Fragment {
    View myFragmentView;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private AppCompatActivity act;
    private ArrayList<Section_Entity> alSectionDataMain;
    private EditText et_name, et_mobile, et_subject, et_body, et_email;
    private Spinner spinnerSections;
    private ArrayList<String> alSectionsnames = null;
    private String name = "";
    private BootstrapButton sendButton;
    public ArrayList<Company_Entity> companyMainCategory;
    private ContentUpdater updater;
    private AppCompatActivity appCompatActivity;
    private ProgressDialog progress = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = /*(SherlockFragmentActivity ) */ (AppCompatActivity) getActivity();
        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.add_feedback, container, false);
            appCompatActivity = (AppCompatActivity) getActivity();
            alSectionDataMain = new ArrayList<>();
            spinnerSections = myFragmentView.findViewById(R.id.section_spinner);
            et_name = myFragmentView.findViewById(R.id.et_name);
            et_mobile = myFragmentView.findViewById(R.id.et_mobile);
            et_subject = myFragmentView.findViewById(R.id.et_subject);
            et_body = myFragmentView.findViewById(R.id.et_body);
            et_email = myFragmentView.findViewById(R.id.et_email);
            sendButton = myFragmentView.findViewById(R.id.btn_send);

            companyMainCategory = new ArrayList<>();
            new FetchCompanies_AdsData().execute();
            sendButton.setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {

                                if (et_name.getText().toString().trim().equals(""))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_name), (AppCompatActivity) getActivity(), null);
                                else if (et_mobile.getText().toString().trim().equals(""))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_mobile),
                                                    (AppCompatActivity) getActivity(),
                                                    null);
                                else if (!et_mobile.getText().toString().trim().equals("")
                                        && et_mobile.getText().toString().trim().length() < 10)
                                    new Utility()
                                            .alert(
                                                    getString(R.string.complete_number),
                                                    (AppCompatActivity) getActivity(),
                                                    null);
                                else if (et_email.getText().toString().trim().equals(""))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_email), (AppCompatActivity) getActivity(), null);
                                else if (!et_email.getText().toString().trim().equals("")
                                        && !isEmailValid(et_email.getText().toString().trim()))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.validemail), (AppCompatActivity) getActivity(), null);
                                else if (spinnerSections.getSelectedItem() == null)
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_section),
                                                    (AppCompatActivity) getActivity(),
                                                    null);
                                else if (et_subject.getText().toString().trim().equals(""))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_subject),
                                                    (AppCompatActivity) getActivity(),
                                                    null);
                                else if (et_body.getText().toString().trim().equals(""))
                                    new Utility()
                                            .alert(
                                                    getString(R.string.enter_body), (AppCompatActivity) getActivity(), null);
                                else {
                                    if (Utility.isConnected((AppCompatActivity) getActivity()))
                                        new SendFeedBack()
                                                .execute(
                                                        new String[]{
                                                                et_name.getText().toString(),
                                                                et_mobile.getText().toString(),
                                                                alSectionDataMain
                                                                        .get(spinnerSections.getSelectedItemPosition() - 1)
                                                                        .getName(),
                                                                et_subject.getText().toString().trim(),
                                                                et_body.getText().toString().trim(),
                                                                et_email.getText().toString().trim()
                                                        });
                                    else
                                        Utility.showToast(
                                                (AppCompatActivity) getActivity(),
                                                getString(R.string.alert_need_internet_connection));
                                }
                            } catch (Exception e) {
                                Log.e(Constants.TAG, "An exception was thrown", e);
                            }
                        }
                    });

            Bundle bundle = this.getArguments();
            name = bundle.getString(Constants.NAME);
            ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
                    .setText(name);

            dbHelper = new DatabaseHelper(act);
            db = dbHelper.getWritableDatabase();
            new FetchSectionsData().execute(new Void[]{null});
        } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
        return myFragmentView;
    }

    private Cursor getSectionsContentsCursor() {
        Cursor crsr = null;
        try {
            String[] from = {Constants.ID, Constants.TITLE};
            crsr = db.query(Constants.SECTIONS_TABLE, from, null, null, null, null, null);
        } catch (Exception ex) {

            ex.getMessage();
        }
        return crsr;
    }

    private Cursor getCompaniesContentsCursor() {
        Cursor crsr = null;
        try {
            String[] from = {Constants.ID, Constants.TITLE};
            crsr = db.query(Constants.COMPANIES_TABLE, from, null, null, null, null, null);
        } catch (Exception ex) {

            ex.getMessage();
        }
        return crsr;
    }

    private class FetchSectionsData extends AsyncTask<Void, Void, Void> {
        ArrayList<Section_Entity> alSectionData = new ArrayList<Section_Entity>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Cursor crsr = getSectionsContentsCursor();
                crsr.moveToFirst();
                if (crsr != null) {
                    while (!crsr.isAfterLast()) {
                        Section_Entity cursorEntity = new Section_Entity();
                        cursorEntity.setId(crsr.getString(0));
                        cursorEntity.setName(crsr.getString(1));

                        alSectionData.add(cursorEntity);
                        crsr.moveToNext();
                    }
                    crsr.close();
                }

                crsr = getCompaniesContentsCursor();
                crsr.moveToFirst();
                if (crsr != null) {
                    while (!crsr.isAfterLast()) {
                        Section_Entity cursorEntity = new Section_Entity();
                        cursorEntity.setId(crsr.getString(0));
                        cursorEntity.setName(crsr.getString(1));

                        alSectionData.add(cursorEntity);
                        crsr.moveToNext();
                    }
                    crsr.close();
                }
            } catch (Exception exception) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (alSectionData != null && alSectionData.size() > 0) {
                alSectionDataMain = alSectionData;
                alSectionsnames = new ArrayList<>();

                for (int i = 0; i < alSectionDataMain.size(); i++) {
                    alSectionsnames.add(alSectionDataMain.get(i).getName());
                }
                ArrayAdapter<String> dataAdapter =
                        new ArrayAdapter<String>(
                                (AppCompatActivity) getActivity(), R.layout.simple_spinner_item, alSectionsnames);
                dataAdapter.setDropDownViewResource(R.layout.spinner_item);

                List<String> companyNames = new ArrayList<>();

                for (Company_Entity entity : companyMainCategory) {
                    companyNames.add(entity.getName());
                }

                ArrayAdapter<String> companyListAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, companyNames);

                spinnerSections.setAdapter(companyListAdapter);

            }
        }
    }

    private class FetchCompanies_AdsData extends AsyncTask<Void, Void, Void> {
        ArrayList<Company_Entity> alCompaniesData = new ArrayList<Company_Entity>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress =
                    ProgressDialog.show(
                            appCompatActivity, getString(R.string.pleaseWait), "", true, true, null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    if (Utility.isConnected(appCompatActivity)) {
                        String strChangeDate = updater.getChangeDate(Constants.ADS_TABLE);
                        updater.ContentsForAds(strChangeDate);
                    }
                    Cursor crsr = getCompaniesCursor();
                    crsr.moveToFirst();
                    if (crsr != null) {
                        while (!crsr.isAfterLast()) {
                            Company_Entity cursorEntity = new Company_Entity();
                            cursorEntity.setId(crsr.getString(0));
                            cursorEntity.setName(crsr.getString(1));
                            cursorEntity.setLogo(crsr.getString(2));
                            cursorEntity.setParentId(crsr.getInt(3));
                            cursorEntity.setSubCategoryType(crsr.getInt(4));
                            cursorEntity.setUnread(updater.AdsUnReadCount(cursorEntity.getId()));
                            alCompaniesData.add(cursorEntity);
                            crsr.moveToNext();
                        }
                        crsr.close();
                    }
                } catch (Exception exception) {
                    return null;
                }
            } catch (Exception exception) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            Log.i("ADS", "PROGRESS DIALOG IS SHOWING " + progress.isShowing());

            progress.dismiss();
            super.onPostExecute(v);

            companyMainCategory.clear();
            if (alCompaniesData != null && alCompaniesData.size() > 0) {
                // alCompaniesMainDataList.clear();
                alCompaniesMainDataList = alCompaniesData;
            } else if (alCompaniesMainDataList.size() == 0) {
                Utility.showToast(appCompatActivity, getString(R.string.nodata_found));
            }

            for (Company_Entity entity : alCompaniesMainDataList) {
                if (entity.getParentId() == 0 && getArguments().getInt(Constants.ID) == 0) {
                    companyMainCategory.add(entity);
                } else if (entity.getParentId() != 0 && getArguments().getInt(Constants.ID) != 0) {
                    if (getArguments().getInt(Constants.ID) == entity.getParentId()) {
                        companyMainCategory.add(entity);
                    }
                }
            }

        }
    }

    private Cursor getCompaniesCursor() {
        Cursor crsr = null;
        try {
            String sql =
                    String.format(
                            Locale.US,
                            "select %s,%s,%s,%s,%s from %s  ",
                            Constants.ID,
                            Constants.TITLE,
                            Constants.LOGO,
                            Constants.PARENTID,
                            Constants.SUB_CATEGORY_TYPE,
                            Constants.COMPANIES_TABLE);
            crsr = db.rawQuery(sql, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return crsr;
    }
    private class SendFeedBack extends AsyncTask<String, Void, Integer> {

        private ProgressDialog progress = null;

        @Override
        protected void onPreExecute() {
            progress =
                    ProgressDialog.show(
                            (AppCompatActivity) getActivity(),
                            getString(R.string.pleaseWait),
                            "",
                            true,
                            true,
                            null);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int code = 0;
            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("name", new StringBody(params[0], Charset.forName("utf-8")));
                entity.addPart("phone", new StringBody(params[1], Charset.forName("utf-8")));
                entity.addPart("section", new StringBody(params[2], Charset.forName("utf-8")));
                entity.addPart("title", new StringBody(params[3], Charset.forName("utf-8")));
                entity.addPart("text", new StringBody(params[4], Charset.forName("utf-8")));
                entity.addPart("email", new StringBody(params[5], Charset.forName("utf-8")));

                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost =
                        new HttpPost("http://qatifedu.com/cmsQatifedu/Services/frmPostFeedback.aspx");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                code = response.getStatusLine().getStatusCode();

                InputStream responseStream = response.getEntity().getContent();
                String responseString = HttpUtils.convertStreamToUTF8String(responseStream);
                Log.i(Constants.TAG, responseString);

            } catch (Exception e) {
                System.out.println("Error in http connection " + e.toString());
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer v) {
            super.onPostExecute(v);
            progress.dismiss();
            if (v == 200) {
                new Utility()
                        .alert(getString(R.string.add_feedback), (AppCompatActivity) getActivity(), onclick);
            } else
                new Utility().alert(getString(R.string.error), (AppCompatActivity) getActivity(), null);
        }
    }

    DialogInterface.OnClickListener onclick =
            new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    et_name.setText("");
                    et_mobile.setText("");
                    et_subject.setText("");
                    et_body.setText("");
                    spinnerSections.setSelection(0);
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
        act.getSupportActionBar()
                .getCustomView()
                .findViewById(R.id.btn_refresh)
                .setVisibility(View.GONE);
        act.getSupportActionBar()
                .getCustomView()
                .findViewById(R.id.extra_category)
                .setVisibility(View.GONE);
        act.getSupportActionBar().getCustomView().findViewById(R.id.calendar).setVisibility(View.GONE);
        super.onResume();

        final Tracker tracker = new UILApplication().getDefaultTracker();

        if (tracker != null) {
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
