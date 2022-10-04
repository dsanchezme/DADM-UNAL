package com.dadm.reto08;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dadm.reto08.controllers.CompanyController;
import com.dadm.reto08.models.Company;

public class EditCompanyActivity extends AppCompatActivity {

    private TextView formTitle;
    private Button saveCompanyButton, cancelProcessButton;
    private EditText nameInput, webPageInput, phoneNumberInput, emailInput,productsInput;
    private Spinner classificationInput;
    private CompanyController companyController;
    private Company company;
    TextView classificationError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_company);

        Bundle extras = getIntent().getExtras();

        if (extras == null){
            finish();
            return;
        }

        companyController = new CompanyController(EditCompanyActivity.this);

        long id = extras.getLong("companyId");
        Cursor companyCursor = companyController.retrieveCompany(id);
        int rowsFound = companyCursor.getCount();
        if (companyCursor == null || !companyCursor.moveToFirst()){
            finish();
            return;
        }

        company = companyController.populateCompany(companyCursor);

        nameInput = findViewById(R.id.nameInput);
        webPageInput = findViewById(R.id.webPageInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        emailInput = findViewById(R.id.emailInput);
        productsInput = findViewById(R.id.productsInput);
        classificationInput = findViewById(R.id.classificationInput);

        saveCompanyButton = findViewById(R.id.saveCompany);
        cancelProcessButton = findViewById(R.id.cancelProcess);

        System.out.println("##############");
        System.out.println(company);
        System.out.println("##############");

        nameInput.setText(company.getName());
        webPageInput.setText(company.getWebPage());
        phoneNumberInput.setText(company.getPhoneNumber());
        emailInput.setText(company.getEmail());
        productsInput.setText(company.getProductsServices());

        formTitle = findViewById(R.id.companyFormTitle);
        formTitle.setText(getString(R.string.new_company));

        System.out.println("Required: "+ company.getClassification());

        String[] items = new String[]{"Consultoria","Desarrollo a la medida","Fabrica de software"};

        ArrayAdapter<String> classificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        classificationInput.setAdapter(classificationAdapter);

        for (int i=0; i< items.length; i++){
            System.out.println("Found: "+ items[i]);
            if (items[i].equalsIgnoreCase(company.getClassification())){
                classificationInput.setSelection(i);
            }
        }

        saveCompanyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                nameInput.setError(null);
                webPageInput.setError(null);
                phoneNumberInput.setError(null);
                emailInput.setError(null);
                productsInput.setError(null);

                String name = nameInput.getText().toString();
                String webPage = webPageInput.getText().toString();
                String phoneNumber = phoneNumberInput.getText().toString();
                String email = emailInput.getText().toString();
                String products = productsInput.getText().toString();

                if (name.trim().isEmpty()){
                    nameInput.setError(getString(R.string.name_error));
                    nameInput.requestFocus();
                    return;
                }

                if (classificationInput.getSelectedView() == null){
                    classificationError.setError(getString(R.string.classification_error));
                    classificationError.setTextColor(Color.RED);
                    classificationError.setText("ERROR");
                    return;
                }

                String classification = ((TextView) classificationInput.getSelectedView()).getText().toString();

                Company company = new Company(id, name, webPage, phoneNumber, email, products, classification);

                long id = companyController.updateCompany(company);
                if (id == -1){
                    Toast.makeText(EditCompanyActivity.this, "Hubo un error al actualizar la información", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        cancelProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


}