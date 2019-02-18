package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
    private Button bt_takephoto_office;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testview = findViewById(R.id.testview);
        result_tv = findViewById(R.id.result_tv);
        imgFavorite = findViewById(R.id.imgFavorite);
        surfaceView = findViewById(R.id.suer);
        bt_takephoto = findViewById(R.id.bt_takephoto);
        bt_takephoto_office = findViewById(R.id.bt_takephoto_office);
        bt_takephoto_office.setOnClickListener(this);
        bt_takephoto.setOnClickListener(this);
        findViewById(R.id.bt_takephoto_once).setOnClickListener(this);
        sp = getSharedPreferences("message", Context.MODE_PRIVATE);
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
    boolean isFirstToken = true;

    public void postImage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                result.delete(0, result.length());

                if (isFirstToken) {//每次进入第一次获取token
                    token = AuthService.getAuth();
                    isFirstToken = false;
                    sp.edit().putString("token", token).commit();
                } else {

                    token = sp.getString("token", "");
                    if (TextUtils.isEmpty(token)) {
                        token = AuthService.getAuth();
                        sp.edit().putString("token", token).commit();
                    }
                }
                String json = "";
                if (isOnline) {
                    json = FaceDetect.detect(token, encode);
                    SearchFace searchFace = GsonUtils.fromJson(json, SearchFace.class);
                    if (searchFace.getResult() != null) {
                        List<SearchFace.ResultBean.FaceListBean> face_list = searchFace.getResult().getFace_list();
                        SearchFace.ResultBean.FaceListBean faceListBean = face_list.get(0);
                        if (faceListBean != null) {
                            facetoken = faceListBean.getFace_token();
                        }
//TODO
                        SearchFace.ResultBean.FaceListBean.LocationBean location = faceListBean.getLocation();
                        left = location.getLeft();
                        top = location.getTop();
                        width = location.getWidth();
                        height = location.getHeight();
                    }
                }
//                 json = FaceDetect.detect(token, encode);
                String json1 = FaceMatch.match(token, facetoken, encode);
                result.append("检测人脸：\n" + json + "\n" + "对比人脸：\n" + json1);

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
//暂时不用了
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bp = (Bitmap) data.getExtras().get("data");
//        imgFavorite.setImageBitmap(bp);
//
//        encode = Base64Util.encode(getBytesByBitmap(bp));
//        postImage();
//    }

    boolean isStart = false;
    boolean isOnline = true;

    @Override
    public void onClick(View v) {

//        surfaceView.resetStart();
        switch (v.getId()) {
            case R.id.bt_takephoto:
                if (isStart) {
                    bt_takephoto.setText("开始检测人脸");
                    isStart = false;
                    stopTime();
                } else {
                    bt_takephoto.setText("停止检测人脸");
                    isStart = true;
                    startTime();
                }
                break;
            case R.id.bt_takephoto_once:
                takePicture();
                break;
            case R.id.bt_takephoto_office:
                if (isOnline) {//设置离线
                    testview.setBei(3.0f);
                    bt_takephoto_office.setText("开启在线人脸检测");
                    isOnline = false;
                } else {//设置在线
                    testview.setBei(3.5f);
                    bt_takephoto_office.setText("开启离线人脸检测");
                    isOnline = true;
                }
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
                //TODO

                if (!isOnline) {
                    localCheckFace(bitmap1);
                }

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

    int MAX_FACES = 5;

    public void localCheckFace(Bitmap mFaceBitmap) {
        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
        //格式必须为RGB_565才可以识别
        Bitmap bmp = mFaceBitmap.copy(Bitmap.Config.RGB_565, true);
        //返回识别的人脸数
        int faceCount = new FaceDetector(bmp.getWidth(), bmp.getHeight(), MAX_FACES).findFaces(bmp, faces);
        bmp.recycle();
        bmp = null;
        Log.e("tag", "识别的人脸数:" + faceCount);

        if (faceCount > 0) {
            parseBitmap(mFaceBitmap, faces, faceCount);
//            final Bitmap bitmap = parseBitmap(mFaceBitmap,faces, faceCount);
            //显示处理后的图片
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    mIv.setImageBitmap(bitmap);
//                }
//            });
        }
    }
    /**
     * 在人脸上画矩形
     */
//    private Bitmap parseBitmap(Bitmap mFaceBitmap ,FaceDetector.Face[] faces, int faceCount){
//        Bitmap bitmap = Bitmap.createBitmap(mFaceBitmap.getWidth(), mFaceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setColor(Color.YELLOW);
//        mPaint.setStrokeWidth(10);
//        mPaint.setStyle(Paint.Style.STROKE);
//
//        canvas.drawBitmap(mFaceBitmap, 0, 0, mPaint);
//        for (int i = 0; i < faceCount; i++){
//            //双眼的中心点
//            PointF midPoint = new PointF();
//            faces[i].getMidPoint(midPoint);
//            //双眼的距离
//            float eyeDistance = faces[i].eyesDistance();
//            //画矩形
//            canvas.drawRect(midPoint.x - eyeDistance, midPoint.y - eyeDistance, midPoint.x + eyeDistance, midPoint.y + eyeDistance, mPaint);
//        }
//
//        return bitmap;
//    }

    /**
     * 在人脸上画矩形
     */
    private void parseBitmap(Bitmap mFaceBitmap, FaceDetector.Face[] faces, int faceCount) {
        Bitmap bitmap = Bitmap.createBitmap(mFaceBitmap.getWidth(), mFaceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setColor(Color.YELLOW);
//        mPaint.setStrokeWidth(10);
//        mPaint.setStyle(Paint.Style.STROKE);

//        canvas.drawBitmap(mFaceBitmap, 0, 0, mPaint);
        for (int i = 0; i < faceCount; i++) {
            //双眼的中心点
            PointF midPoint = new PointF();
            faces[i].getMidPoint(midPoint);
            //双眼的距离
            float eyeDistance = faces[i].eyesDistance();
            //画矩形
            left = midPoint.x - eyeDistance;
            top = midPoint.y - eyeDistance;
            width = midPoint.x + eyeDistance;
            height = midPoint.y + eyeDistance;

            handler.sendEmptyMessage(0);
//            canvas.drawRect(midPoint.x - eyeDistance, midPoint.y - eyeDistance, midPoint.x + eyeDistance, midPoint.y + eyeDistance, mPaint);
        }

    }
}
