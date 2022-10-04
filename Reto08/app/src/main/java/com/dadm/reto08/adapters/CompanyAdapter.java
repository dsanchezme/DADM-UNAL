package com.dadm.reto08.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dadm.reto08.R;
import com.dadm.reto08.models.Company;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.MyViewHolder> {
    private List<Company> companies;

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View companyInfo = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.company_info, viewGroup, false);
        return new MyViewHolder(companyInfo);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Company company = companies.get(i);

        String companyName = company.getName();
        String companyClassification = company.getClassification();

        myViewHolder.name.setText(companyName);
        myViewHolder.classification.setText(companyClassification);
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, classification;

        MyViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.companyName);
            this.classification = itemView.findViewById(R.id.companyClassification);
        }
    }
}
