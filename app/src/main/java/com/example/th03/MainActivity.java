package com.example.th03;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SinhVienAdapter adapter;
    private List<SinhVien> danhSachSV;
    private DatabaseHelper dbHelper;
    private TextInputEditText edtTimKiem;
    private TextView tvTongSo, tvEmpty;
    private FloatingActionButton fabThem;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDanhSach();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        setupSearch();
        setupFab();
        loadDanhSach();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        tvTongSo = findViewById(R.id.tvTongSo);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabThem = findViewById(R.id.fabThem);
    }

    private void setupRecyclerView() {
        danhSachSV = new ArrayList<>();
        adapter = new SinhVienAdapter(danhSachSV, new SinhVienAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SinhVien sv) {
                Intent intent = new Intent(MainActivity.this, ChiTietSinhVienActivity.class);
                intent.putExtra("sinhvien", sv);
                launcher.launch(intent);
            }

            @Override
            public void onDeleteClick(SinhVien sv, int position) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + sv.getHoTen() + " (" + sv.getMaSV() + ") không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            if (dbHelper.xoaSinhVien(sv.getMaSV())) {
                                loadDanhSach();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    loadDanhSach();
                } else {
                    List<SinhVien> results = dbHelper.timKiemSinhVien(keyword);
                    adapter.updateList(results);
                    updateUI(results.size());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFab() {
        fabThem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemSinhVienActivity.class);
            launcher.launch(intent);
        });
    }

    private void loadDanhSach() {
        danhSachSV = dbHelper.layTatCaSinhVien();
        adapter.updateList(danhSachSV);
        updateUI(danhSachSV.size());
    }

    private void updateUI(int count) {
        tvTongSo.setText("Tổng số: " + count + " sinh viên");
        if (count == 0) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}