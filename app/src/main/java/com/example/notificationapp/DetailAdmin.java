package com.example.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notificationapp.models.Message;
import com.example.notificationapp.models.ModelContract;
import com.example.notificationapp.models.Model_code_pin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DetailAdmin extends AppCompatActivity {
    TextView name,pseudo,number, code_pin,rappel,val;
    RelativeLayout logout,modifier,upda;
    EditText editTex,editTex1;
    RelativeLayout deco,pin, planifier,annonce;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button btnVal;
    String idAdmin;
    int incr;
    String selectedDayOfWeek, selectedDayOfMonth;
    SharedPreferences sharedPreferences1;
    DatabaseReference databaseReference,databaseReferenceM;
    private BottomSheetDialog bottomSheetDialog;

    private ImageView tvjour,tvmoistest,retour;
    private EditText editTextJour,edmoistest,edmoistestnum;
    TextView tvdate,tvheure;
    ImageView ivdate,ivheure;
    private Spinner spinnerWeek, spinnerMonth;
    private ArrayAdapter<String> weekAdapter, monthAdapter;
    private List<String> weekDays, monthDays;
    private TextView recupsemaine, recupmois;
    LinearLayout lljour, llmois,llsemaine,llmoistest;
    String codep,codepinId;
    AlertPaiement popup;

    SharedPreferences.Editor editor1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_admin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("codepin");
        databaseReferenceM = FirebaseDatabase.getInstance().getReference().child("message");

        upda =findViewById(R.id.upda);
        annonce =findViewById(R.id.annonce);
        logout =findViewById(R.id.logout);
        retour =findViewById(R.id.m00);
        pin =findViewById(R.id.pin);
        planifier =findViewById(R.id.planifier);
        name =findViewById(R.id.t1);
        pseudo =findViewById(R.id.t2);
        number =findViewById(R.id.t3);
        code_pin =findViewById(R.id.t5);
        rappel =findViewById(R.id.t6);
        modifier =findViewById(R.id.upda);
        sharedPreferences = getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
         sharedPreferences1 = getSharedPreferences("rappel", Context.MODE_PRIVATE);
         editor1 = sharedPreferences1.edit();

        idAdmin = sharedPreferences.getString("id", "");
        String nom = sharedPreferences.getString("nom", "");
        String prenom = sharedPreferences.getString("prenom", "");
        String numero = sharedPreferences.getString("numero", "");
        String mdp = sharedPreferences.getString("mdp", "");
        codep = sharedPreferences.getString("codepin", "");
        codepinId = sharedPreferences.getString("codepinId", "");
        name.setText(nom);
        pseudo.setText(prenom);
        number.setText(numero);

        weekDays = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        monthDays = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            monthDays.add(String.valueOf(i));
        }
        weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekDays);
        monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthDays);

        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        annonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailAdmin.this,AnnonceInfos.class));
                finish();
            }
        });

        upda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("contrats").child(idAdmin);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(DetailAdmin.this, "Le contrat existe dejà", Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(new Intent(DetailAdmin.this,ContratLoyer.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseDatabase", "Erreur lors de la vérification du contrat", databaseError.toException());
                    }
                });
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailAdmin.this,MainActivity.class));
                finish();
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup = new AlertPaiement(DetailAdmin.this);
                popup.setCancelable(true);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
                if (!codep.isEmpty()){
                    popup.setTitreText("Avertissement");
                    popup.setMessageText("Voullez-vous vraiment changer votre code pin?");
                    popup.setCancelText("OUI");
                    popup.setCancelBackground(R.drawable.bg_circle_red);
                    popup.setCancelTextColor(R.color.white);
                }else{
                    popup.setTitreText("Code pin");
                    popup.setMessageText("Voullez-vous vraiment ajouter un code pin?");
                    popup.setCancelText("OUI");
                    popup.setCancelBackground(R.drawable.bg_circle_green);
                    popup.setCancelTextColor(R.color.white);
                }
                popup.show();
                popup.getRetour().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.cancel();
                        bottomSheetDialog = new BottomSheetDialog(DetailAdmin.this);
                        View bottomSheetView = getLayoutInflater().inflate(R.layout.codepopup, null);
                        bottomSheetDialog.setContentView(bottomSheetView);
                        editTex = bottomSheetView.findViewById(R.id.editTex);
                        editTex1 = bottomSheetView.findViewById(R.id.editTex1);
                        btnVal = bottomSheetView.findViewById(R.id.btnVal);
                        if (!codep.isEmpty()){
                            editTex.setText(codep);
                            editTex1.setText(codep);
                        }
                        btnVal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!editTex.getText().toString().isEmpty() || !editTex1.getText().toString().isEmpty()){
                                    if (!editTex.getText().toString().equals(editTex1.getText().toString())){
                                        Toast.makeText(DetailAdmin.this, "Verifier le code pin", Toast.LENGTH_SHORT).show();

                                    }else{
                                        addCode(editTex.getText().toString());
                                    }
                                }else{
                                    Toast.makeText(DetailAdmin.this, "Remplisser tout les champs", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        bottomSheetDialog.show();
                    }
                });
            }
        });


        //PLANIFICATION DE RAPPEL
        planifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(DetailAdmin.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.planifierpopup, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                val = bottomSheetView.findViewById(R.id.val);
                tvdate = bottomSheetView.findViewById(R.id.tvdate);
                tvheure = bottomSheetView.findViewById(R.id.tvheure);
                ivdate = bottomSheetView.findViewById(R.id.ivdate);
                ivheure = bottomSheetView.findViewById(R.id.ivheure);
                tvjour = bottomSheetView.findViewById(R.id.tvjour);
                tvmoistest = bottomSheetView.findViewById(R.id.tvmoistest);
                editTextJour = bottomSheetView.findViewById(R.id.edjour);
                edmoistest = bottomSheetView.findViewById(R.id.edmoistest);
                edmoistestnum = bottomSheetView.findViewById(R.id.edmoistestnum);
                lljour = bottomSheetView.findViewById(R.id.lljour);
                llmoistest = bottomSheetView.findViewById(R.id.llmoistest);
                editor1.putString("selectedDayOfWeek", selectedDayOfWeek);
                editor1.apply();

                ivdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                DetailAdmin.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        // Mettez à jour votre interface utilisateur avec la date sélectionnée
                                        tvdate.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                                    }
                                },
                                year,
                                month,
                                day
                        );
                        datePickerDialog.show();
                    }

                });
                ivheure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        int hourOfDay ; // Heure sélectionnée
                        int minuteh ;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                DetailAdmin.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        @SuppressLint("DefaultLocale") String formattedHour = String.format("%02d", hourOfDay);
                                        @SuppressLint("DefaultLocale") String formattedMinute = String.format("%02d", minute);
                                        // Mettez à jour votre interface utilisateur avec l'heure sélectionnée
                                        tvheure.setText(formattedHour + ":" + formattedMinute);
                                    }
                                },
                                hour,
                                minute,
                                true
                        );
                        timePickerDialog.show();
                    }

                });
                tvjour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Vérifiez si le TextView est actuellement visible ou non
                        if (lljour.getVisibility() == View.VISIBLE) {
                            // Le TextView est visible, alors cachez-le et affichez l'ImageView par défaut
                            lljour.setVisibility(View.GONE);
                            tvjour.setImageResource(R.drawable.base1); // Image par défaut
                        } else {
                            // Le TextView n'est pas visible, alors affichez-le et changez l'image de l'ImageView
                            lljour.setVisibility(View.VISIBLE);
                            tvjour.setImageResource(R.drawable.base2); // Nouvelle image
                        }
                    }
                });
                tvmoistest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Vérifiez si le TextView est actuellement visible ou non
                        if (llmoistest.getVisibility() == View.VISIBLE) {
                            // Le TextView est visible, alors cachez-le et affichez l'ImageView par défaut
                            llmoistest.setVisibility(View.GONE);
                            tvmoistest.setImageResource(R.drawable.base1); // Image par défaut
                        } else {
                            // Le TextView n'est pas visible, alors affichez-le et changez l'image de l'ImageView
                            llmoistest.setVisibility(View.VISIBLE);
                            tvmoistest.setImageResource(R.drawable.base2); // Nouvelle image
                        }
                    }
                });

                val.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (editTextJour.getText().toString().isEmpty()){
                            if (edmoistest.getText().toString().isEmpty()||edmoistestnum.getText().toString().isEmpty()){
                                Toast.makeText(DetailAdmin.this, "Aucun rappel n'est planifié", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }else{
                                editor1.clear();
                                editor1.apply();
                                editor1.putString("messageparseconde", edmoistest.getText().toString());
                                editor1.putString("lesnumeros", edmoistestnum.getText().toString());
                                editor1.apply();
                                bottomSheetDialog.dismiss();
                            }
                        }else{
                            if (edmoistest.getText().toString().isEmpty()|| edmoistestnum.getText().toString().isEmpty()){
                                Message msge=new Message("messagejour",editTextJour.getText().toString());
                                databaseReferenceM.child(idAdmin).child("messagejour").setValue(msge);
                                bottomSheetDialog.dismiss();
                            }else{
                                editor1.clear();
                                editor1.apply();
                                editor1.putString("messageparseconde", edmoistest.getText().toString());
                                editor1.putString("lesnumeros", edmoistestnum.getText().toString());
                                editor1.apply();
                                Message msge=new Message("messagejour",editTextJour.getText().toString());
                                databaseReferenceM.child(idAdmin).child("messagejour").setValue(msge);
                                bottomSheetDialog.dismiss();
                            }
                        }



                    }
                });
                bottomSheetDialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.apply();
                startActivity(new Intent(DetailAdmin.this, LoginAdmin.class));
                finish();
            }
        });
    }


    private void addCode(String code) {
        if (codep.isEmpty()){
            DatabaseReference localiteReference = databaseReference.child(idAdmin).push();
            editor.putString("codepin", code);
            editor.apply();
            String nouvelId = localiteReference.getKey();
            Model_code_pin codePin = new Model_code_pin(nouvelId,code);
            localiteReference.setValue(codePin);
            startActivity(new Intent(DetailAdmin.this, Login.class));
            finish();
        }else{
            DatabaseReference localiteReference = databaseReference.child(idAdmin).child(codepinId);
            localiteReference.child("code").setValue(code);
            editor.putString("codepin", code);
            editor.apply();
            startActivity(new Intent(DetailAdmin.this, Login.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        incr++;
        if (incr == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}