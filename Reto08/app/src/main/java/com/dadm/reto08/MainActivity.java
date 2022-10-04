package com.dadm.reto08;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dadm.reto08.adapters.CompanyAdapter;
import com.dadm.reto08.controllers.CompanyController;
import com.dadm.reto08.helpers.DBHelper;
import com.dadm.reto08.helpers.RecyclerTouchListener;
import com.dadm.reto08.models.Company;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Company> companiesList;
    private RecyclerView recyclerView;
    private CompanyAdapter companyAdapter;
    private CompanyController companyController;
    private FloatingActionButton fabAddCompany;
    private DBHelper dbHelper = new DBHelper(this);
    private EditText filterByNameInput;
    private Spinner filterByClassificationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        companyController = new CompanyController(MainActivity.this);

//        dbHelper.deleteDatabase();

        recyclerView = findViewById(R.id.recyclerViewCompanies);
        fabAddCompany = findViewById(R.id.fabAddCompany);

        companiesList = new ArrayList<>();
        companyAdapter = new CompanyAdapter(companiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(companyAdapter);

        filterByNameInput = findViewById(R.id.filterByName);
        filterByClassificationInput = findViewById(R.id.filterByClassification);

        retrieveCompaniesList();

        filterByNameInput.setText("");
        filterByClassificationInput.setSelection(0);

        String[] items = new String[]{"", "Consultoria","Desarrollo a la medida","Fabrica de software"};
        ArrayAdapter<String> classificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        filterByClassificationInput.setAdapter(classificationAdapter);

        filterByNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                retrieveCompaniesFiltered(charSequence, filterByClassificationInput.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterByClassificationInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                retrieveCompaniesFiltered(filterByNameInput.getText(), items[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {

                Company selectedCompany = companiesList.get(position);
                Intent intent = new Intent(MainActivity.this, EditCompanyActivity.class);
                intent.putExtra("companyId", selectedCompany.getId());
                intent.putExtra("companyName", selectedCompany.getName());
//                intent.putExtra("companyWebPage", selectedCompany.getWebPage());
//                intent.putExtra("companyPhoneNumber", selectedCompany.getPhoneNumber());
//                intent.putExtra("companyEmail", selectedCompany.getEmail());
//                intent.putExtra("companyProducts", selectedCompany.getProductsServices());
                intent.putExtra("companyClassification", selectedCompany.getClassification());
                startActivity(intent);
            }

            @Override // Un toque largo
            public void onLongClick(View view, int position) {
                final Company companyToDelete = companiesList.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivity.this)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                companyController.deleteCompany(companyToDelete.getId());
                                retrieveCompaniesList();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Comfirmar")
                        .setMessage("¿Seguro que desea ELIMINAR el registro de " + companyToDelete.getName() + "?")
                        .create();
                dialog.show();

            }

        }));

        fabAddCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCompanyActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveCompaniesList();
    }

    public void retrieveCompaniesList() {
        if (companyAdapter == null) return;
        companiesList = companyController.retrieveAllCompanies();
        companyAdapter.setCompanies(companiesList);
        companyAdapter.notifyDataSetChanged();
    }

    public void retrieveCompaniesFiltered(CharSequence name, String classification){
        if (companyAdapter == null) return;
        companiesList = companyController.retrieveFilteredCompanies(String.valueOf(name).toLowerCase(), classification);
        companyAdapter.setCompanies(companiesList);
        companyAdapter.notifyDataSetChanged();
        System.out.println("I am changing to " + String.valueOf(name).toLowerCase());
    }
}