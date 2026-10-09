package com.example.th03;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "quanlysinhvien.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SINHVIEN = "sinhvien";
    private static final String COL_MA_SV = "maSV";
    private static final String COL_HO_TEN = "hoTen";
    private static final String COL_NGAY_SINH = "ngaySinh";
    private static final String COL_GIOI_TINH = "gioiTinh";
    private static final String COL_LOP = "lop";
    private static final String COL_EMAIL = "email";
    private static final String COL_SO_DIEN_THOAI = "soDienThoai";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SINHVIEN + " ("
                + COL_MA_SV + " TEXT PRIMARY KEY, "
                + COL_HO_TEN + " TEXT NOT NULL, "
                + COL_NGAY_SINH + " TEXT, "
                + COL_GIOI_TINH + " TEXT, "
                + COL_LOP + " TEXT, "
                + COL_EMAIL + " TEXT, "
                + COL_SO_DIEN_THOAI + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SINHVIEN);
        onCreate(db);
    }

    public boolean themSinhVien(SinhVien sv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MA_SV, sv.getMaSV());
        values.put(COL_HO_TEN, sv.getHoTen());
        values.put(COL_NGAY_SINH, sv.getNgaySinh());
        values.put(COL_GIOI_TINH, sv.getGioiTinh());
        values.put(COL_LOP, sv.getLop());
        values.put(COL_EMAIL, sv.getEmail());
        values.put(COL_SO_DIEN_THOAI, sv.getSoDienThoai());
        long result = db.insert(TABLE_SINHVIEN, null, values);
        db.close();
        return result != -1;
    }

    public boolean capNhatSinhVien(SinhVien sv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HO_TEN, sv.getHoTen());
        values.put(COL_NGAY_SINH, sv.getNgaySinh());
        values.put(COL_GIOI_TINH, sv.getGioiTinh());
        values.put(COL_LOP, sv.getLop());
        values.put(COL_EMAIL, sv.getEmail());
        values.put(COL_SO_DIEN_THOAI, sv.getSoDienThoai());
        int result = db.update(TABLE_SINHVIEN, values, COL_MA_SV + "=?", new String[]{sv.getMaSV()});
        db.close();
        return result > 0;
    }

    public boolean xoaSinhVien(String maSV) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_SINHVIEN, COL_MA_SV + "=?", new String[]{maSV});
        db.close();
        return result > 0;
    }

    public List<SinhVien> layTatCaSinhVien() {
        List<SinhVien> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SINHVIEN + " ORDER BY " + COL_HO_TEN, null);
        if (cursor.moveToFirst()) {
            do {
                SinhVien sv = new SinhVien();
                sv.setMaSV(cursor.getString(cursor.getColumnIndexOrThrow(COL_MA_SV)));
                sv.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(COL_HO_TEN)));
                sv.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow(COL_NGAY_SINH)));
                sv.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow(COL_GIOI_TINH)));
                sv.setLop(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOP)));
                sv.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
                sv.setSoDienThoai(cursor.getString(cursor.getColumnIndexOrThrow(COL_SO_DIEN_THOAI)));
                list.add(sv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<SinhVien> timKiemSinhVien(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SINHVIEN
                + " WHERE " + COL_MA_SV + " LIKE ? OR " + COL_HO_TEN + " LIKE ?"
                + " ORDER BY " + COL_HO_TEN;
        Cursor cursor = db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                SinhVien sv = new SinhVien();
                sv.setMaSV(cursor.getString(cursor.getColumnIndexOrThrow(COL_MA_SV)));
                sv.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(COL_HO_TEN)));
                sv.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow(COL_NGAY_SINH)));
                sv.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow(COL_GIOI_TINH)));
                sv.setLop(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOP)));
                sv.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
                sv.setSoDienThoai(cursor.getString(cursor.getColumnIndexOrThrow(COL_SO_DIEN_THOAI)));
                list.add(sv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean kiemTraMaSVTonTai(String maSV) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_SINHVIEN + " WHERE " + COL_MA_SV + "=?", new String[]{maSV});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
}
