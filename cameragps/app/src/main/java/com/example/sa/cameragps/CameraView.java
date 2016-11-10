package com.example.sa.cameragps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView
implements SurfaceHolder.Callback, PictureCallback {
        private Camera mCamera = null;
        private Location mLocation;
        @SuppressLint("SdCardPath")
        private static final String SDCARD_FOLDER = "/sdcard/CameraSample/";

        public CameraView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // API Level 11で廃止予定に
        // 保存用フォルダ作成
        File dirs = new File(SDCARD_FOLDER);
        if(!dirs.exists()) {
        dirs.mkdir();
        }
        }

        @Override
public void onPictureTaken(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_kkmmss");
        String datName = "P" + date.format(new Date()) + ".jpg";
        try {
        // データ保存
        savePhotoData(datName, data);
        } catch (Exception e) { // TODO Auto-generated catch block
        if(mCamera != null) {
        mCamera.release();
        mCamera = null;
        }
        }
        // プレビュー再開
        mCamera.startPreview();
        }

        private void savePhotoData(String datName, byte[] data) throws Exception {
        // TODO Auto-generated method stub
        FileOutputStream outStream = null;

        try {
        outStream = new FileOutputStream(SDCARD_FOLDER + datName);
        outStream.write(data);
        outStream.close();
        } catch (Exception e) {
        if(outStream != null) {
        outStream.close();
        }
        throw e;
        }
        }

        @Override
public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
        if (mLocation != null) {
        // パラメータ取得
        Camera.Parameters params = mCamera.getParameters();
        // GPS情報設定
        params.setGpsLatitude(mLocation.getLatitude());
        params.setGpsLongitude(mLocation.getLongitude());
        params.setGpsAltitude(mLocation.getAltitude());
        params.setGpsTimestamp(System.currentTimeMillis());
        // パラメータ設定
        mCamera.setParameters(params);
        }
        // シャッターを切る
        mCamera.takePicture(null, null, this);
        }
        return true;
        }

        @Override
public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera = Camera.open();
        // パラメータ取得
        Camera.Parameters params = mCamera.getParameters();
        // サイズ：640x480に設定
        params.setPictureSize(640, 480);
        // パラメータ設定
        mCamera.setParameters(params);
        try {
        mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        }

        @Override
public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        // プレビュー画面のサイズ設定
        Camera.Parameters params = mCamera.getParameters();
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        Size size = previewSizes.get(0);
        params.setPreviewSize(size.width, size.height);
        mCamera.setParameters(params);
        // プレビュー開始
        mCamera.startPreview();
        }

        @Override
public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        }

        public void setLocation(Location location) {
        mLocation = location;
        }
        }
