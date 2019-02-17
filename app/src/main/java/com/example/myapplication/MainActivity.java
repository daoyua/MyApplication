package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.utils.Base64Util;
import com.example.myapplication.utils.FaceMatch;
import com.example.myapplication.utils.GsonUtils;
import com.example.myapplication.utils.MyView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imgFavorite;
    private String[] permissions = {Manifest.permission.CAMERA};
    private MyView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPersion();
        imgFavorite = findViewById(R.id.imgFavorite);
        surfaceView = findViewById(R.id.suer);
       findViewById(R.id.bt_takephoto).setOnClickListener(this);


    }

    private void initPersion() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            } else {
//                startCamera();
            }
        }
    }

    private void startCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
//                .setMessage("由于支付宝需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用支付宝")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }     // 用户权限 申请 的回调方法

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
//                    startCamera();
                }
            }
        }
    }

    private void showDialogTipUserGoToAppSettting() {
    }

    public void porstImage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String token = AuthService.getAuth();
                String json = FaceDetect.detect(token, encode);
//                String json = FaceMatch.match(token, encode);
                SearchFace searchFace = GsonUtils.fromJson(json, SearchFace.class);
                if(searchFace.getResult()!=null){
                    List<SearchFace.ResultBean.FaceListBean> face_list = searchFace.getResult().getFace_list();
                    SearchFace.ResultBean.FaceListBean faceListBean = face_list.get(0);
                    SearchFace.ResultBean.FaceListBean.LocationBean location = faceListBean.getLocation();

                    surfaceView.drawMyView(location.getLeft(), location.getTop(), location.getWidth(), location.getHeight());
                    Log.e("token", token + "");
                }

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

        porstImage();
    }

    @Override
    public void onClick(View v) {
        surfaceView.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Matrix matrix = new Matrix();
//                matrix.setRotate(90);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                imgFavorite.setImageBitmap(bitmap);

                encode = Base64Util.encode(getBytesByBitmap(bitmap));

                porstImage();
            }
        });
    }
}
