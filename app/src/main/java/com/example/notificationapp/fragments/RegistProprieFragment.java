package com.example.notificationapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notificationapp.LoginAdmin;
import com.example.notificationapp.MainActivity;
import com.example.notificationapp.PopupRegister;
import com.example.notificationapp.PopusCostum;
import com.example.notificationapp.R;
import com.example.notificationapp.RegisterAdmin;
import com.example.notificationapp.models.Admin;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistProprieFragment  extends Fragment {

    private TextInputEditText userName,numero,Userprenom, passwordEdt, confirmPwdEdt;
    private TextView loginTV;
    private TextView registerBtn;
    int incr;
    PopusCostum popusCostum;
    PopupRegister popup;
    DatabaseReference databaseReference ;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_admin, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        sharedPreferences = getContext().getSharedPreferences("Admin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // initializing all our variables.
        userName = view.findViewById(R.id.UserName);
        Userprenom = view.findViewById(R.id.Userprenom);
        numero = view.findViewById(R.id.UserEmail);
        passwordEdt = view.findViewById(R.id.idEdtPassword);
        confirmPwdEdt = view.findViewById(R.id.idEdtConfirmPassword);
        loginTV = view.findViewById(R.id.idTVLoginUser);
        registerBtn = view.findViewById(R.id.idBtnRegister);


        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a login activity on clicking login text.
                Intent i = new Intent(getContext(), LoginAdmin.class);
                startActivity(i);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNames = userName.getText().toString();
                String userPrenom = Userprenom.getText().toString();
                String numeros = numero.getText().toString();
                String pwd = passwordEdt.getText().toString();
                String cnfPwd = confirmPwdEdt.getText().toString();
                if (!userNames.isEmpty() || !pwd.isEmpty() || !cnfPwd.isEmpty()){

                    // checking if the password and confirm password is equal or not.
                    if (!pwd.equals(cnfPwd)) {
                        Toast.makeText(getContext(), "Veuillez verifier vos mots de passe..", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(userNames) &&TextUtils.isEmpty(userPrenom) &&TextUtils.isEmpty(numeros) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd)) {

                        // checking if the text fields are empty or not.
                        Toast.makeText(getContext(), "Veuillez saisir vos identifiants..", Toast.LENGTH_SHORT).show();
                    }else {

                        popusCostum = new PopusCostum(getActivity());
                        popusCostum.setCancelable(false);
                        popusCostum.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popusCostum.show();
                        popusCostum.getComf().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popusCostum.cancel();
                                popup = new PopupRegister(getActivity());
                                popup.setCancelable(false);
                                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popup.show();
                                addAdmin(userNames,userPrenom,numeros,pwd);
                            }
                        });

                        popusCostum.getRetour().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popusCostum.cancel();
                            }
                        });
                    }
                }
            }
        });
        return  view;
    }
    private void addAdmin(String userNames, String userPrenom, String numeros, String pwd) {
        LocalDate currentDate = null;
        String formattedDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DateTimeFormatter dateFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = currentDate.format(dateFormatter);
        }
        String finalFormattedDate = formattedDate;
        databaseReference.orderByChild("numeros").equalTo(numeros).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Vérifiez si dataSnapshot contient des données
                if (dataSnapshot.exists()) {
                    popup.cancel();
                    // L'utilisateur existe déjà avec ce numéro de téléphone, affichez un message d'erreur
                    Toast.makeText(getContext(), "Ce numéro de téléphone est déjà associé à un compte.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference localiteReference = databaseReference.push();
                    String nouvelId = localiteReference.getKey();
                    editor.putString("id", nouvelId);
                    editor.putString("nom", userNames);
                    editor.putString("prenom", userPrenom);
                    editor.putString("numero", numeros);
                    editor.putString("mdp", pwd);
                    editor.putString("proprie", "proprie");
                    editor.apply();
                    Admin nouveauAdmin =new Admin(nouvelId,userNames,userPrenom,numeros,pwd, finalFormattedDate);
                    localiteReference.setValue(nouveauAdmin);
                    popup.cancel();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                popup.cancel();
            }
        });
    }

}
