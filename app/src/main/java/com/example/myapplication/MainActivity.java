package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.utils.Base64Util;
import com.example.myapplication.utils.FaceMatch;
import com.example.myapplication.utils.GsonUtils;
import com.example.myapplication.utils.MyView;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imgFavorite;
    private String[] permissions = {Manifest.permission.CAMERA};
    private MyView surfaceView;
    private TextView result_tv;
    private MyTestView testview;
    private Timer timer;
    private TimerTask task;
    private Button bt_takephoto;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testview = findViewById(R.id.testview);
        result_tv = findViewById(R.id.result_tv);
        imgFavorite = findViewById(R.id.imgFavorite);
        surfaceView = findViewById(R.id.suer);
        bt_takephoto = findViewById(R.id.bt_takephoto);
        bt_takephoto.setOnClickListener(this);
        findViewById(R.id.bt_takephoto_once).setOnClickListener(this);
        sp = getSharedPreferences("message",Context.MODE_PRIVATE);
//        startTime();

    }

    private void startTime() {
        if (timer == null) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    takePicture();
                }
            };
        }

        timer.schedule(task, 3000, 1000);
    }

    private void stopTime() {
        if (timer != null) {
            task.cancel();
            task = null;
            timer.cancel();
            timer = null;
        }
    }

    public float left, top, width, height;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//        super.handleMessage(msg);
            result_tv.setText(result.toString());
            testview.setView(left, top, width, height);
            testview.invalidate();

        }
    };
    String facetoken = "";
    StringBuilder result = new StringBuilder();
    String token;
    boolean isFirstToken =true;
    public void postImage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                result.delete(0, result.length());

                if(isFirstToken){//每次进入第一次获取token
                    token = AuthService.getAuth();
                    isFirstToken=false;
                    sp.edit().putString("token",token).commit();
                }else{

                    token= sp.getString("token","");
                    if(TextUtils.isEmpty(token)){
                        token = AuthService.getAuth();
                        sp.edit().putString("token",token).commit();
                    }
                }



                String json = FaceDetect.detect(token, encode);
                String json1 = FaceMatch.match(token, facetoken, encode);
                result.append("检测人脸：\n" + json + "\n" + "对比人脸：\n" + json1);
                SearchFace searchFace = GsonUtils.fromJson(json, SearchFace.class);
                if (searchFace.getResult() != null) {
                    List<SearchFace.ResultBean.FaceListBean> face_list = searchFace.getResult().getFace_list();
                    SearchFace.ResultBean.FaceListBean faceListBean = face_list.get(0);
                    if (faceListBean != null) {
                        facetoken = faceListBean.getFace_token();
                    }

                    SearchFace.ResultBean.FaceListBean.LocationBean location = faceListBean.getLocation();
                    left = location.getLeft();
                    top = location.getTop();
                    width = location.getWidth();
                    height = location.getHeight();


                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    public byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private String encode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        imgFavorite.setImageBitmap(bp);

        encode = Base64Util.encode(getBytesByBitmap(bp));

        postImage();
    }

    boolean isStart = false;

    @Override
    public void onClick(View v) {

//        surfaceView.resetStart();
        switch(v.getId()){
        case R.id.bt_takephoto  :
            if (isStart) {
                bt_takephoto.setText("开始检测人脸");
                isStart=false;
                stopTime();
            } else {
                bt_takephoto.setText("停止检测人脸");
                isStart=true;
                startTime();
            }
        break;
            case R.id.bt_takephoto_once:
                takePicture();
                break;
        }

//        takePicture();
    }

    private void takePicture() {
        surfaceView.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

//                Matrix matrix = new Matrix();
//                matrix.setRotate(90);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Bitmap bitmap1 = adjustPhotoRotation(bitmap, -90);
                imgFavorite.setImageBitmap(bitmap1);

                encode = Base64Util.encode(getBytesByBitmap(bitmap1));

                postImage();
                surfaceView.startPreviews();
                surfaceView.isTaking = false;
//                surfaceView.releaseCamera();
            }
        });
    }

    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        m.postScale(-1, 1);
        try {

            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

            return bm1;
        } catch (OutOfMemoryError ex) {
        }

        return null;

    }
}
