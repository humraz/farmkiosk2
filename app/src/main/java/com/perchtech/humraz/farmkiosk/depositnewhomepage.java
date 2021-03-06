package com.perchtech.humraz.farmkiosk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class depositnewhomepage extends AppCompatActivity {
    String kioskid="yolo";
    String tyme;
    String date="";
    int sum = 0;
    String amount;
    int op=0;
    int r;
    int st;
    String usernamee;
    int c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depositnewhomepage);
        SharedPreferences prefs3= getSharedPreferences("kioskname", MODE_PRIVATE);
        kioskid = prefs3.getString("kname",null);
        r = Integer.parseInt(prefs3.getString("rate",null));

        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
        tyme= time.format(new Date());
        Intent in = getIntent();
        st=in.getIntExtra("stock",0);
        c=in.getIntExtra("cash",0);
        SimpleDateFormat mon = new SimpleDateFormat("MM");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        String month= mon.format(new Date());
        String yea= year.format(new Date());
        String o =prefs3.getString("openbal", null);
        op= Integer.parseInt(o);
usernamee=prefs3.getString("kname",null);
        String da= day.format(new Date());
        date=da+"-"+month+"-"+yea;
        Firebase.setAndroidContext(this);

    }
   public void modify(View view)
   {
       EditText ed = (EditText) findViewById(R.id.user2) ;
       amount =ed.getText().toString();
       final Firebase ref = new Firebase("https://kioskfarm.firebaseio.com/KIOSKS/");
       //Value event listener for realtime data update
       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot usersSnapshot) {
               for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                   kioskmake user1 = userSnapshot.getValue(kioskmake.class);
                   String passs = user1.getPass();
                   if (passs.equals(usernamee)) {
                       int o= Integer.parseInt(user1.getYestopeningbal());
                       int d = Integer.parseInt(user1.getDiff());
                       o=o-Integer.parseInt(amount)-d+(c*r);
                       System.out.println("hoiiiiiiii" + o);
                       if (o<0)
                       {
                           Toast.makeText(depositnewhomepage.this, "You are withdrawing more than available!",Toast.LENGTH_LONG).show();
                       }
                       else
                       {
                           int depo;
                           int a=Integer.parseInt(user1.getDiff());
                           depo=a+Integer.parseInt(amount);
                           userSnapshot.getRef().child("diff").setValue(depo);
                           write();


                       }
                     //  userSnapshot.getRef().child("openingbal").setValue(Integer.toString(o));

                   }
               }

           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {
               System.out.println("The read failed: " + firebaseError.getMessage());
           }
       });


   }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, billing.class);
        startActivity(in);
    }
    public void write()
    {

        if (amount == "")
        {
            Toast.makeText(this,"Enter The Amount First" ,Toast.LENGTH_LONG).show();
        }
        else {
            Firebase ref = new Firebase("https://kioskfarm.firebaseio.com/deposits/"+kioskid);
            deposits person = new deposits();

            SimpleDateFormat t = new SimpleDateFormat("HH:mm:ss");

            String time = t.format(new Date());
            SimpleDateFormat mon = new SimpleDateFormat("dd/MM/yyyy");

            String date = mon.format(new Date());
            person.setDate(date);
            person.setTime(time);
            person.setAmount(amount);
           // op=op-Integer.parseInt(amount);
            person.setKid(kioskid);

            ref.push().setValue(person);
            //modify(amount);
            new SweetAlertDialog(depositnewhomepage.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Deposit has been confirmed")
                    .setContentText("Amount Added On date " + date )
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Intent in  = new Intent( depositnewhomepage.this, billing.class);
                            in.putExtra("stock", Integer.toString(st));
                            startActivity(in);

                        }
                    })
                    .show();
            //Toast.makeText(this, "Amount Added On date " + date + "and time " + time, Toast.LENGTH_LONG).show();


        }
    }
}
