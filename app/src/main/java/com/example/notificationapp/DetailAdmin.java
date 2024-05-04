package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationapp.models.Model_code_pin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DetailAdmin extends AppCompatActivity {
    TextView name,pseudo,number,modifier, code_pin,rappel,val;
    ImageView logout;
    EditText editTex,editTex1;
    RelativeLayout deco,pin, planifier;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button btnVal;
    String idAdmin;
    int incr;
    String selectedDayOfWeek, selectedDayOfMonth;
    SharedPreferences sharedPreferences1;
    DatabaseReference databaseReference;
    private BottomSheetDialog bottomSheetDialog;

    private ImageView tvjour, tvsemaine, tvmois,tvmoistest;
    private EditText editTextJour, editTextSemaine, editTextMois,edmoistest,edmoistestnum;
    private Spinner spinnerWeek, spinnerMonth;
    private ArrayAdapter<String> weekAdapter, monthAdapter;
    private List<String> weekDays, monthDays;
    private TextView recupsemaine, recupmois;
    LinearLayout lljour, llmois,llsemaine,llmoistest;

    SharedPreferences.Editor editor1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_admin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("codepin");

        logout =findViewById(R.id.m4);
        deco =findViewById(R.id.deco);
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


        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailAdmin.this, "Update", Toast.LENGTH_SHORT).show();
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(DetailAdmin.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.codepopup, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                editTex = bottomSheetView.findViewById(R.id.editTex);
                editTex1 = bottomSheetView.findViewById(R.id.editTex1);
                btnVal = bottomSheetView.findViewById(R.id.btnVal);
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

        planifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(DetailAdmin.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.planifierpopup, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                tvjour = bottomSheetView.findViewById(R.id.tvjour);
                tvsemaine = bottomSheetView.findViewById(R.id.tvsemaine);
                tvmois = bottomSheetView.findViewById(R.id.tvmois);
                tvmoistest = bottomSheetView.findViewById(R.id.tvmoistest);
                editTextJour = bottomSheetView.findViewById(R.id.edjour);
                editTextSemaine = bottomSheetView.findViewById(R.id.edsemaine);
                editTextMois = bottomSheetView.findViewById(R.id.edmois);
                edmoistest = bottomSheetView.findViewById(R.id.edmoistest);
                edmoistestnum = bottomSheetView.findViewById(R.id.edmoistestnum);
                spinnerWeek = bottomSheetView.findViewById(R.id.spinnerWeek);
                spinnerMonth = bottomSheetView.findViewById(R.id.spinnerMonth);
                recupsemaine = bottomSheetView.findViewById(R.id.recupsemaine);
                recupmois = bottomSheetView.findViewById(R.id.recupmois);
                lljour = bottomSheetView.findViewById(R.id.lljour);
                llmoistest = bottomSheetView.findViewById(R.id.llmoistest);
                llmois = bottomSheetView.findViewById(R.id.llmois);
                llsemaine = bottomSheetView.findViewById(R.id.llsemaine);
                val = bottomSheetView.findViewById(R.id.val);
                editor1.putString("selectedDayOfWeek", selectedDayOfWeek);
                editor1.apply();
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
                tvsemaine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Vérifiez si le TextView est actuellement visible ou non
                        if (llsemaine.getVisibility() == View.VISIBLE) {
                            // Le TextView est visible, alors cachez-le et affichez l'ImageView par défaut
                            llsemaine.setVisibility(View.GONE);
                            tvsemaine.setImageResource(R.drawable.base1); // Image par défaut
                        } else {
                            // Le TextView n'est pas visible, alors affichez-le et changez l'image de l'ImageView
                            llsemaine.setVisibility(View.VISIBLE);
                            tvsemaine.setImageResource(R.drawable.base2); // Nouvelle image
                        }
                    }
                });
                tvmois.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Vérifiez si le TextView est actuellement visible ou non
                        if (llmois.getVisibility() == View.VISIBLE) {
                            // Le TextView est visible, alors cachez-le et affichez l'ImageView par défaut
                            llmois.setVisibility(View.GONE);
                            tvmois.setImageResource(R.drawable.base1); // Image par défaut
                        } else {
                            // Le TextView n'est pas visible, alors affichez-le et changez l'image de l'ImageView
                            llmois.setVisibility(View.VISIBLE);
                            tvmois.setImageResource(R.drawable.base2); // Nouvelle image
                        }
                    }
                });

                spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                         selectedDayOfWeek = weekAdapter.getItem(position);
                        recupsemaine.setText("A chaque " + selectedDayOfWeek+" le message sera envoyé");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Gérer le cas où rien n'est sélectionné
                    }
                });

                spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                         selectedDayOfMonth = monthAdapter.getItem(position);
                        recupmois.setText("A chaque  " + selectedDayOfMonth+" du mois le message sera envoyé");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Gérer le cas où rien n'est sélectionné
                    }
                });

                // Définir les adaptateurs pour les Spinners
                spinnerWeek.setAdapter(weekAdapter);
                spinnerMonth.setAdapter(monthAdapter);
                val.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (editTextMois.getText().toString().isEmpty()||editTextJour.getText().toString().isEmpty()||editTextJour.getText().toString().isEmpty()){
                            if (edmoistest.getText().toString().isEmpty()||edmoistestnum.getText().toString().isEmpty()){
                                Toast.makeText(DetailAdmin.this, "Aucun rappel n'est planifié", Toast.LENGTH_SHORT).show();
                            }else{
                                editor1.clear();
                                editor1.apply();
                                editor1.putString("messageparseconde", edmoistest.getText().toString());
                                editor1.putString("lesnumeros", edmoistestnum.getText().toString());
                                editor1.apply();
                                bottomSheetDialog.dismiss();
                            }
                        }else{
                            if (edmoistest.getText().toString().isEmpty()||edmoistestnum.getText().toString().isEmpty()){
                                editor1.clear();
                                editor1.apply();
                                editor1.putString("messagejour", editTextJour.getText().toString());
                                editor1.putString("messagesemaine", editTextSemaine.getText().toString());
                                editor1.putString("messagemois", editTextMois.getText().toString());
                                editor1.putString("selectedDayOfWeek", selectedDayOfWeek);
                                editor1.putString("selectedDayOfMonth", selectedDayOfMonth);
                                editor1.apply();
                                bottomSheetDialog.dismiss();
                            }else{
                                editor1.clear();
                                editor1.apply();
                                editor1.putString("messagejour", editTextJour.getText().toString());
                                editor1.putString("messagesemaine", editTextSemaine.getText().toString());
                                editor1.putString("messagemois", editTextMois.getText().toString());
                                editor1.putString("selectedDayOfWeek", selectedDayOfWeek);
                                editor1.putString("selectedDayOfMonth", selectedDayOfMonth);
                                editor1.putString("messageparseconde", edmoistest.getText().toString());
                                editor1.putString("lesnumeros", edmoistestnum.getText().toString());
                                editor1.apply();
                                bottomSheetDialog.dismiss();
                            }


                        }



                    }
                });
                bottomSheetDialog.show();
            }
        });

        deco.setOnClickListener(new View.OnClickListener() {
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
        DatabaseReference localiteReference = databaseReference.child(idAdmin).push();
        editor.putString("codepin", code);
        editor.apply();
        String nouvelId = localiteReference.getKey();
        Model_code_pin codePin = new Model_code_pin(nouvelId,code);
        localiteReference.setValue(codePin);
        startActivity(new Intent(DetailAdmin.this, Login.class));
        finish();
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