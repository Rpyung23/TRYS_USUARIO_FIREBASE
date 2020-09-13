package com.virtualcode7ecuadorvigitrack.trys.broadcast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class cBroadcastMsmAuthPhoneFire extends BroadcastReceiver
{
    private SmsMessage[] mSmsMessage;
    private byte[] mBytes;
    private String body_sms="";
    private String numerphone_sms="";
    private EditText mEditTextDijit1;
    private EditText mEditTextDijit2;
    private EditText mEditTextDijit3;
    private EditText mEditTextDijit4;
    private EditText mEditTextDijit5;
    private EditText mEditTextDijit6;

    public EditText getmEditTextDijit1() {
        return mEditTextDijit1;
    }

    public void setmEditTextDijit1(EditText mEditTextDijit1) {
        this.mEditTextDijit1 = mEditTextDijit1;
    }

    public EditText getmEditTextDijit2() {
        return mEditTextDijit2;
    }

    public void setmEditTextDijit2(EditText mEditTextDijit2) {
        this.mEditTextDijit2 = mEditTextDijit2;
    }

    public EditText getmEditTextDijit3() {
        return mEditTextDijit3;
    }

    public void setmEditTextDijit3(EditText mEditTextDijit3) {
        this.mEditTextDijit3 = mEditTextDijit3;
    }

    public EditText getmEditTextDijit4() {
        return mEditTextDijit4;
    }

    public void setmEditTextDijit4(EditText mEditTextDijit4) {
        this.mEditTextDijit4 = mEditTextDijit4;
    }

    public EditText getmEditTextDijit5() {
        return mEditTextDijit5;
    }

    public void setmEditTextDijit5(EditText mEditTextDijit5) {
        this.mEditTextDijit5 = mEditTextDijit5;
    }

    public EditText getmEditTextDijit6() {
        return mEditTextDijit6;
    }

    public void setmEditTextDijit6(EditText mEditTextDijit6) {
        this.mEditTextDijit6 = mEditTextDijit6;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            Bundle mBundle = intent.getExtras();
            if (mBundle!=null)
            {
                Object[] mypdu = (Object[]) mBundle.get("pdus");
                mSmsMessage = new SmsMessage[mypdu.length];
                for (int i =0;i<mypdu.length;i++)
                {
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    {
                        String format = mBundle.getString("format");
                        mSmsMessage[i] = SmsMessage.createFromPdu((byte[]) mypdu[i],format);
                    }else
                        {
                            mSmsMessage[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                        }
                    body_sms = mSmsMessage[i].getMessageBody();
                    numerphone_sms = mSmsMessage[i].getOriginatingAddress();
                }
            }

            if (!body_sms.toString().equals(""))
            {
                Log.e("CODE",body_sms.substring(0,6));

                if(getmEditTextDijit1()!=null)
                {
                    getmEditTextDijit1().setText(String.valueOf((body_sms.charAt(0))));
                }
                if(getmEditTextDijit2()!=null)
                {
                    getmEditTextDijit2().setText(String.valueOf((body_sms.charAt(1))));
                }
                if(getmEditTextDijit3()!=null)
                {
                    getmEditTextDijit3().setText(String.valueOf((body_sms.charAt(2))));
                }
                if(getmEditTextDijit4()!=null)
                {
                    getmEditTextDijit4().setText(String.valueOf((body_sms.charAt(3))));
                }
                if(getmEditTextDijit5()!=null)
                {
                    getmEditTextDijit5().setText(String.valueOf((body_sms.charAt(4))));
                }
                if(getmEditTextDijit6()!=null)
                {
                    getmEditTextDijit6().setText(String.valueOf((body_sms.charAt(5))));
                }

            }


        }
    }
}
