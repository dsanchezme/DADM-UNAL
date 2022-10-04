package com.dadm.reto08.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.dadm.reto08.helpers.DBHelper;
import com.dadm.reto08.models.Company;

import java.io.File;
import java.util.ArrayList;

public class CompanyController {

    private DBHelper dbHelper;
    private String tableName = "company";

    public CompanyController(Context context){
        dbHelper = new DBHelper(context);
    }

    public long createCompany(Company company){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", company.getName());
        contentValues.put("webPage", company.getWebPage());
        contentValues.put("phoneNumber", company.getPhoneNumber());
        contentValues.put("email", company.getEmail());
        contentValues.put("productsServices", company.getProductsServices());
        contentValues.put("classification", company.getClassification());
        return database.insert(tableName, null, contentValues);
    }

    public Cursor retrieveCompany(long id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        return database.rawQuery( "SELECT * FROM " + tableName + " WHERE id=" + id + "", null );
    }

    public int updateCompany(Company company){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", company.getName());
        contentValues.put("webPage", company.getWebPage());
        contentValues.put("phoneNumber", company.getPhoneNumber());
        contentValues.put("email", company.getEmail());
        contentValues.put("productsServices", company.getProductsServices());
        contentValues.put("classification", company.getClassification());
        return database.update(tableName, contentValues, "id = ? ", new String[] {String.valueOf(company.getId())});
    }

    public int deleteCompany(long id){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(tableName,"id = ? ", new String[] { String.valueOf(id) });

    }

    public ArrayList<Company> retrieveFilteredCompanies(String name, String classification) {
        ArrayList<Company> companies = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor =  database.rawQuery("SELECT id, name, classification FROM " + tableName +
                                              " WHERE LOWER(name) LIKE '%" + name + "%'" +
                                              "AND classification LIKE '%" + classification + "%'", null);

        if (cursor == null || !cursor.moveToFirst()){
            return companies;
        }

        do {
            long companyId = cursor.getLong(0);
            String companyName = cursor.getString(1);
            String companyClassification = cursor.getString(2);
            Company company = new Company(companyName, companyClassification);
            company.setId(companyId);
            companies.add(company);
        } while (cursor.moveToNext());

        cursor.close();
        return companies;
    }

    public ArrayList<Company> retrieveAllCompanies() {
        ArrayList<Company> companies = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] columns = {"id", "name", "classification"};
        Cursor cursor =  database.query( tableName, columns, null ,null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()){
            return companies;
        }

        do {
            long companyId = cursor.getLong(0);
            String companyName = cursor.getString(1);
            String companyClassification = cursor.getString(2);
            Company company = new Company(companyName, companyClassification);
            company.setId(companyId);
            companies.add(company);
        } while (cursor.moveToNext());

        cursor.close();
        return companies;
    }

    public Company populateCompany(Cursor cursor){
        try{
            int idIndex = cursor.getColumnIndexOrThrow("id");
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            int webPageIndex = cursor.getColumnIndexOrThrow("webPage");
            int phoneNumberIndex = cursor.getColumnIndexOrThrow("phoneNumber");
            int emailIndex = cursor.getColumnIndexOrThrow("email");
            int productsServicesIndex = cursor.getColumnIndexOrThrow("productsServices");
            int classificationIndex = cursor.getColumnIndexOrThrow("classification");

            long id = cursor.getLong(idIndex);
            String name = cursor.getString(nameIndex);
            String webPage = cursor.getString(webPageIndex);
            String phoneNumber = cursor.getString(phoneNumberIndex);
            String email = cursor.getString(emailIndex);
            String productsServices = cursor.getString(productsServicesIndex);
            String classification = cursor.getString(classificationIndex);
            return new Company(id, name, webPage, phoneNumber, email, productsServices, classification);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
