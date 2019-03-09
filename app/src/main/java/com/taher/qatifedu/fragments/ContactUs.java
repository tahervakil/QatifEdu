package com.taher.qatifedu.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.ContactUs_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentParser;
import com.taher.qatifedu.utility.Utility;

import java.util.ArrayList;

public class ContactUs extends Fragment {
    View myFragmentView;
    ImageButton btn_Facebook;
    ImageButton btn_Telegram;
    ImageButton btn_Snap;
    ImageButton btn_Twitter;
    ImageButton btn_Instagram;
    ImageButton btn_Whatsapp;
    ImageButton smsButton;
    ImageButton emailButton;
    Button btn_Website, btn_Phone;
    private AppCompatActivity act;
    private String name = "";
    ArrayList<ContactUs_Entity> alContactUsDataMain;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = /*(SherlockFragmentActivity ) */ (AppCompatActivity) getActivity();
        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.contactus, container, false);
            alContactUsDataMain = new ArrayList<ContactUs_Entity>();
            btn_Phone = (Button) myFragmentView.findViewById(R.id.btn_phone);
            btn_Phone.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    AlertDialog.Builder bld = new AlertDialog.Builder(act);
                                    bld.setMessage(getString(R.string.phonecall_msg));
                                    bld.setNegativeButton(
                                            getString(R.string.cancel),
                                            new android.content.DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                    bld.setPositiveButton(
                                            getString(R.string.submit),
                                            new android.content.DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    boolean hasTelephony =
                                                            act.getPackageManager()
                                                                    .hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                                                    if (hasTelephony) {
                                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                        String phonecall = alContactUsDataMain.get(0).getPhone();

                                                        if (!phonecall.contains("tel:"))
                                                            phonecall = "tel:" + phonecall;
                                                        callIntent.setData(Uri.parse(phonecall));

                                                        startActivity(callIntent);
                                                    }
                                                }
                                            });

                                    bld.create().show();

                                } catch (Exception activityException) {
                                    Log.e("helloandroid dialing example", "Call failed", activityException);
                                }
                            }
                        }
                    });

            btn_Website = myFragmentView.findViewById(R.id.btn_website);
            smsButton = myFragmentView.findViewById(R.id.btn_sms);
            emailButton = myFragmentView.findViewById(R.id.btn_email);

            btn_Website.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    String url = alContactUsDataMain.get(0).getWebsite();
                                    if (!url.contains("http://") && !url.contains("https://"))
                                        url = "http://" + url;
                                    OpenSite(url, "الموقع الالكترونى");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    });
            btn_Facebook = myFragmentView.findViewById(R.id.btn_facebook);
            btn_Facebook.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    String url = alContactUsDataMain.get(0).getFacebook();
                                    if (!url.contains("http://") && !url.contains("https://"))
                                        url = "http://" + url;
                                    OpenSite(url, "فايس بوك");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    });

            btn_Snap = myFragmentView.findViewById(R.id.btn_snap);
            btn_Snap.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    String url = alContactUsDataMain.get(0).getSnapChat();
                                    if (!url.contains("http://") && !url.contains("https://"))
                                        url = "http://" + url;
                                    OpenSite(url, "سناب شات");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    });
            btn_Telegram = (ImageButton) myFragmentView.findViewById(R.id.btn_telegram);
            btn_Telegram.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {

                                    String url =
                                            alContactUsDataMain.get(0).getTelegram(); // "https://goo.gl/SW0V2E";//
                                    if (!url.contains("http://") && !url.contains("https://"))
                                        url = "http://" + url;
                                    OpenSite(url, "تيليغرام");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    });

            btn_Twitter = (ImageButton) myFragmentView.findViewById(R.id.btn_twitter);
            btn_Twitter.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    String url = alContactUsDataMain.get(0).getTwitter();
                                    if (!url.contains("https://m.twitter.com/"))
                                        url = "https://m.twitter.com/" + url;
                                    OpenSite(url, "تويتر");
                                } catch (Exception e) {
                                }
                            }
                        }
                    });

            btn_Instagram = (ImageButton) myFragmentView.findViewById(R.id.btn_instagram);
            btn_Instagram.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {
                                    String url = alContactUsDataMain.get(0).getInstegram();
                                    if (!url.contains("http://instagram.com/"))
                                        url = "http://instagram.com/" + url;
                                    OpenSite(url, "انستغرام");
                                } catch (Exception e) {
                                }
                            }
                        }
                    });

            btn_Whatsapp = (ImageButton) myFragmentView.findViewById(R.id.btn_whatsapp);
            btn_Whatsapp.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                                try {

                                    String url =
                                            alContactUsDataMain.get(0).getWhatsapp(); // "https://goo.gl/SW0V2E";//
                                    if (!url.contains("http://") && !url.contains("https://"))
                                        url = "http://" + url;
                                    OpenSite(url, "واتس اب");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    });

            smsButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("sms:0567654432"));
                            smsIntent.putExtra("sms_body", "");
                            startActivity(smsIntent);
                        }
                    });

            emailButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent =
                                    new Intent(
                                            Intent.ACTION_SENDTO, Uri.fromParts("mailto", "qatifedu@gmail.com", null));
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));

                        }
                    });

            Bundle bundle = this.getArguments();
            name = bundle.getString(Constants.NAME);
            ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
                    .setText(name);
        } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
        if (Utility.isConnected(act)) new Fetch_ContactUsData().execute();
        else Utility.showToast(act, getString(R.string.alert_need_internet_connection));
        return myFragmentView;
    }

    @Override
    public void onResume() {
        ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
        ((ImageButton) act.getSupportActionBar().getCustomView().findViewById(R.id.btn_refresh))
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

    private class Fetch_ContactUsData extends AsyncTask<Void, Void, ContactUs_Entity> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
            super.onPreExecute();
        }

        @Override
        protected ContactUs_Entity doInBackground(Void... params) {

            try {
                return new ContentParser("http://qatifedu.com/cmsQatifedu/Services/xmlContact.xml")
                        .getContactUsContents();
            } catch (Exception exception) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ContactUs_Entity ContactUs) {
            super.onPostExecute(ContactUs);
            progress.dismiss();
            if (ContactUs != null) {
                alContactUsDataMain.add(ContactUs);
                btn_Phone.setText(alContactUsDataMain.get(0).getPhone());
                //btn_Website.setText(alContactUsDataMain.get(0).getWebsite().substring(8));
            }

        }
    }

    public void OpenSite(String url, String name) {
        Bundle data = new Bundle();
        InternalBrowser fragment = new InternalBrowser();
        data.putString("url", url);
        data.putString(Constants.NAME, name);
        data.putInt(Constants.TYPE, 1);
        fragment.setArguments(data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (getFragmentManager().findFragmentByTag(name) == null) ;
        ft.addToBackStack(null);
        ft.replace(R.id.frame_container, Constants.MOFragmentStack.push(fragment), name).commit();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = act.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
