package com.example.th03;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class ThemSinhVienActivity extends AppCompatActivity {

    private TextInputEditText edtMaSV, edtHoTen, edtNgaySinh, edtLop, edtEmail, edtSoDienThoai;
    private TextInputLayout tilMaSV, tilHoTen, tilNgaySinh, tilLop, tilEmail, tilSoDienThoai;
    private RadioGroup rgGioiTinh;
    private MaterialButton btnLuu;
    private MaterialToolbar toolbar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_sinh_vien);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupToolbar();
        setupDatePicker();
        setupSaveButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        edtMaSV = findViewById(R.id.edtMaSV);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtLop = findViewById(R.id.edtLop);
        edtEmail = findViewById(R.id.edtEmail);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        tilMaSV = findViewById(R.id.tilMaSV);
        tilHoTen = findViewById(R.id.tilHoTen);
        tilNgaySinh = findViewById(R.id.tilNgaySinh);
        tilLop = findViewById(R.id.tilLop);
        tilEmail = findViewById(R.id.tilEmail);
        tilSoDienThoai = findViewById(R.id.tilSoDienThoai);
        rgGioiTinh = findViewById(R.id.rgGioiTinh);
        btnLuu = findViewById(R.id.btnLuu);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        edtNgaySinh.setOnClickListener(v -> showDatePicker());
        tilNgaySinh.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    edtNgaySinh.setText(date);
                    tilNgaySinh.setError(null);
                },
                calendar.get(Calendar.YEAR) - 18,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void setupSaveButton() {
        btnLuu.setOnClickListener(v -> {
            if (validateInput()) {
                saveSinhVien();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate Mã SV
        String maSV = edtMaSV.getText().toString().trim();
        if (TextUtils.isEmpty(maSV)) {
            tilMaSV.setError("Vui lòng nhập mã sinh viên");
            isValid = false;
        } else if (dbHelper.kiemTraMaSVTonTai(maSV)) {
            tilMaSV.setError("Mã sinh viên đã tồn tại");
            isValid = false;
        } else {
            tilMaSV.setError(null);
        }

        // Validate Họ tên
        String hoTen = edtHoTen.getText().toString().trim();
        if (TextUtils.isEmpty(hoTen)) {
            tilHoTen.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else {
            tilHoTen.setError(null);
        }

        // Validate Lớp
        String lop = edtLop.getText().toString().trim();
        if (TextUtils.isEmpty(lop)) {
            tilLop.setError("Vui lòng nhập lớp");
            isValid = false;
        } else {
            tilLop.setError(null);
        }

        // Validate Email (optional but must be valid if provided)
        String email = edtEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate Phone (optional but must be valid if provided)
        String phone = edtSoDienThoai.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !phone.matches("^0[0-9]{9,10}$")) {
            tilSoDienThoai.setError("Số điện thoại không hợp lệ (bắt đầu bằng 0, 10-11 chữ số)");
            isValid = false;
        } else {
            tilSoDienThoai.setError(null);
        }

        return isValid;
    }

    private void saveSinhVien() {
        String maSV = edtMaSV.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();
        String gioiTinh = rgGioiTinh.getCheckedRadioButtonId() == R.id.rbNam ? "Nam" : "Nữ";
        String lop = edtLop.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String sdt = edtSoDienThoai.getText().toString().trim();

        SinhVien sv = new SinhVien(maSV, hoTen, ngaySinh, gioiTinh, lop, email, sdt);

        if (dbHelper.themSinhVien(sv)) {
            Toast.makeText(this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm sinh viên!", Toast.LENGTH_SHORT).show();
        }
    }
}
