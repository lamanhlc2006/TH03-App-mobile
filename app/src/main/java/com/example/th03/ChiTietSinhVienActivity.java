package com.example.th03;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.TextView;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class ChiTietSinhVienActivity extends AppCompatActivity {

    private TextInputEditText edtMaSV, edtHoTen, edtNgaySinh, edtLop, edtEmail, edtSoDienThoai;
    private TextInputLayout tilMaSV, tilHoTen, tilNgaySinh, tilLop, tilEmail, tilSoDienThoai;
    private RadioGroup rgGioiTinh;
    private MaterialButton btnChinhSua, btnLuu, btnXoa;
    private MaterialToolbar toolbar;
    private TextView tvAvatarLarge, tvHoTenHeader, tvMaSVHeader;
    private DatabaseHelper dbHelper;
    private SinhVien currentSV;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_sinh_vien);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupToolbar();
        loadSinhVienData();
        setupButtons();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvAvatarLarge = findViewById(R.id.tvAvatarLarge);
        tvHoTenHeader = findViewById(R.id.tvHoTenHeader);
        tvMaSVHeader = findViewById(R.id.tvMaSVHeader);
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
        btnChinhSua = findViewById(R.id.btnChinhSua);
        btnLuu = findViewById(R.id.btnLuu);
        btnXoa = findViewById(R.id.btnXoa);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSinhVienData() {
        currentSV = (SinhVien) getIntent().getSerializableExtra("sinhvien");
        if (currentSV == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu sinh viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set header
        String hoTen = currentSV.getHoTen();
        tvHoTenHeader.setText(hoTen);
        tvMaSVHeader.setText("MSV: " + currentSV.getMaSV());
        if (hoTen != null && !hoTen.isEmpty()) {
            String[] parts = hoTen.trim().split("\\s+");
            String lastPart = parts[parts.length - 1];
            tvAvatarLarge.setText(String.valueOf(lastPart.charAt(0)).toUpperCase());
        }

        // Set form fields
        edtMaSV.setText(currentSV.getMaSV());
        edtHoTen.setText(currentSV.getHoTen());
        edtNgaySinh.setText(currentSV.getNgaySinh());
        edtLop.setText(currentSV.getLop());
        edtEmail.setText(currentSV.getEmail());
        edtSoDienThoai.setText(currentSV.getSoDienThoai());

        if ("Nữ".equals(currentSV.getGioiTinh())) {
            rgGioiTinh.check(R.id.rbNu);
        } else {
            rgGioiTinh.check(R.id.rbNam);
        }
    }

    private void setupButtons() {
        btnChinhSua.setOnClickListener(v -> toggleEditMode());

        btnLuu.setOnClickListener(v -> {
            if (validateInput()) {
                updateSinhVien();
            }
        });

        btnXoa.setOnClickListener(v -> confirmDelete());

        // Setup date picker for edit mode
        edtNgaySinh.setOnClickListener(v -> {
            if (isEditing) showDatePicker();
        });
        tilNgaySinh.setEndIconOnClickListener(v -> {
            if (isEditing) showDatePicker();
        });
    }

    private void toggleEditMode() {
        isEditing = !isEditing;

        // Enable/disable fields (maSV always disabled)
        edtHoTen.setEnabled(isEditing);
        edtNgaySinh.setEnabled(isEditing);
        edtLop.setEnabled(isEditing);
        edtEmail.setEnabled(isEditing);
        edtSoDienThoai.setEnabled(isEditing);
        findViewById(R.id.rbNam).setEnabled(isEditing);
        findViewById(R.id.rbNu).setEnabled(isEditing);

        if (isEditing) {
            btnChinhSua.setText("Hủy");
            btnLuu.setVisibility(View.VISIBLE);
            toolbar.setTitle("Chỉnh sửa sinh viên");
        } else {
            btnChinhSua.setText("Chỉnh sửa");
            btnLuu.setVisibility(View.GONE);
            toolbar.setTitle("Chi tiết sinh viên");
            // Reset to original data
            loadSinhVienData();
        }
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

    private boolean validateInput() {
        boolean isValid = true;

        String hoTen = edtHoTen.getText().toString().trim();
        if (TextUtils.isEmpty(hoTen)) {
            tilHoTen.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else {
            tilHoTen.setError(null);
        }

        String lop = edtLop.getText().toString().trim();
        if (TextUtils.isEmpty(lop)) {
            tilLop.setError("Vui lòng nhập lớp");
            isValid = false;
        } else {
            tilLop.setError(null);
        }

        String email = edtEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        String phone = edtSoDienThoai.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !phone.matches("^0[0-9]{9,10}$")) {
            tilSoDienThoai.setError("Số điện thoại không hợp lệ");
            isValid = false;
        } else {
            tilSoDienThoai.setError(null);
        }

        return isValid;
    }

    private void updateSinhVien() {
        currentSV.setHoTen(edtHoTen.getText().toString().trim());
        currentSV.setNgaySinh(edtNgaySinh.getText().toString().trim());
        currentSV.setGioiTinh(rgGioiTinh.getCheckedRadioButtonId() == R.id.rbNam ? "Nam" : "Nữ");
        currentSV.setLop(edtLop.getText().toString().trim());
        currentSV.setEmail(edtEmail.getText().toString().trim());
        currentSV.setSoDienThoai(edtSoDienThoai.getText().toString().trim());

        if (dbHelper.capNhatSinhVien(currentSV)) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);

            // Switch back to view mode
            isEditing = false;
            edtHoTen.setEnabled(false);
            edtNgaySinh.setEnabled(false);
            edtLop.setEnabled(false);
            edtEmail.setEnabled(false);
            edtSoDienThoai.setEnabled(false);
            findViewById(R.id.rbNam).setEnabled(false);
            findViewById(R.id.rbNu).setEnabled(false);

            btnChinhSua.setText("Chỉnh sửa");
            btnLuu.setVisibility(View.GONE);
            toolbar.setTitle("Chi tiết sinh viên");

            // Update header
            tvHoTenHeader.setText(currentSV.getHoTen());
            String hoTen = currentSV.getHoTen();
            if (hoTen != null && !hoTen.isEmpty()) {
                String[] parts = hoTen.trim().split("\\s+");
                String lastPart = parts[parts.length - 1];
                tvAvatarLarge.setText(String.valueOf(lastPart.charAt(0)).toUpperCase());
            }
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + currentSV.getHoTen() + " (" + currentSV.getMaSV() + ") không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (dbHelper.xoaSinhVien(currentSV.getMaSV())) {
                        Toast.makeText(this, "Đã xóa sinh viên!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
